package mypals.ml.shape.model;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ObjModelShape extends Shape {

    public Vec3 modelCenter = Vec3.ZERO;
    public SimpleOBJModel model;

    public ObjModelShape(RenderingType type,
                         Consumer<DefaultTransformer> transform,
                         ResourceLocation resourceLocation,
                         Vec3 center,
                         Color color) {
        this(type, transform, resourceLocation, center, color, false);
    }


    protected ObjModelShape(RenderingType type,Color color, boolean seeThrough) {
        super(type,color, seeThrough);
    }

    public ObjModelShape(RenderingType type,
                         Consumer<DefaultTransformer> transform,
                         ResourceLocation resourceLocation,
                         Vec3 center,
                         Color color,
                         boolean seeThrough) {
        super(type,transform, color,center, seeThrough);
        try {
            loadOBJ(resourceLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.transformer.setShapeWorldPivot(center);

        syncLastToTarget();
        generateRawGeometry(false);
    }

    public static class SimpleOBJModel {
        public final ArrayList<Vec3> vertices = new ArrayList<>();
        public final ArrayList<int[]> faces = new ArrayList<>();
    }

    public void loadOBJ(ResourceLocation location) throws IOException {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        if (manager.getResource(location).isEmpty()) return;

        Optional<Resource> optional = manager.getResource(location);
        if (optional.isEmpty()) return;

        Resource resource = optional.get();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open()))) {
            SimpleOBJModel m = new SimpleOBJModel();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                switch (parts[0]) {
                    case "v" -> {
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        double z = Double.parseDouble(parts[3]);
                        m.vertices.add(new Vec3(x, y, z));
                    }
                    case "f" -> {
                        int[] faceIndices = new int[parts.length - 1];
                        for (int i = 1; i < parts.length; i++) {
                            String[] vParts = parts[i].split("/");
                            faceIndices[i - 1] = Integer.parseInt(vParts[0]) - 1;
                        }
                        m.faces.add(faceIndices);
                    }
                }
            }

            this.model = m;
        }

    }


    public Vec3 calculateShapeCenterPos() {
        if (model == null || model.vertices.isEmpty()) return Vec3.ZERO;

        Vec3 min = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Vec3 max = new Vec3(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);

        for (Vec3 v : model.vertices) {
            min = new Vec3(Math.min(min.x, v.x), Math.min(min.y, v.y), Math.min(min.z, v.z));
            max = new Vec3(Math.max(max.x, v.x), Math.max(max.y, v.y), Math.max(max.z, v.z));
        }

        this.modelCenter = new Vec3(
                (min.x + max.x) / 2,
                (min.y + max.y) / 2,
                (min.z + max.z) / 2
        );

        return modelCenter;
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        model_vertexes.clear();
        if (model == null) return;

        model_vertexes.addAll(model.vertices);

        List<Integer> indices = new ArrayList<>();
        for (int[] face : model.faces) {
            if (face.length < 3) continue;
            for (int i = 1; i < face.length - 1; i++) {
                indices.add(face[0]);
                indices.add(face[i]);
                indices.add(face[i + 1]);
            }
        }

        this.indexBuffer = indices.stream().mapToInt(i -> i).toArray();
    }

    @Override
    protected void drawInternal(VertexBuilder builder) {
        if (model_vertexes.isEmpty() || indexBuffer == null || indexBuffer.length < 3)
            return;

        builder.putColor(baseColor);

        for (int i = 0; i < indexBuffer.length; i += 3) {
            Vec3 v0 = model_vertexes.get(indexBuffer[i]);
            Vec3 v1 = model_vertexes.get(indexBuffer[i + 1]);
            Vec3 v2 = model_vertexes.get(indexBuffer[i + 2]);

            builder.putVertex(v0);
            builder.putVertex(v1);
            builder.putVertex(v2);
        }
    }
}

