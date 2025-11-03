package mypals.ml.shape.model;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
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
import java.util.function.BiConsumer;

public class ObjModelShape extends Shape {

    public Color color = Color.white;
    public Vec3 modelCenter = Vec3.ZERO;
    public SimpleOBJModel model;

    public ObjModelShape(RenderingType type, BiConsumer<? super DefaultTransformer, Shape> transform, ResourceLocation resourceLocation, Vec3 center, Color color) {
        this(type, transform,resourceLocation,center,color,false);
    }

    protected ObjModelShape(RenderingType type) {
        super(type);
    }

    protected ObjModelShape(RenderingType type, boolean seeThrough) {
        super(type, seeThrough);
    }

    public ObjModelShape(RenderingType type, BiConsumer<? super DefaultTransformer, Shape> transform, ResourceLocation resourceLocation, Vec3 center, Color color, boolean seeThrough) {
        super(type, transform, seeThrough);
        this.transformer = new DefaultTransformer(this);
        try {
            loadOBJ(resourceLocation);
        }catch (IOException e){
            e.printStackTrace();
        }
        this.color = color;
        this.centerPoint = center;
        this.centerPoint = calculateShapeCenterPos();
        this.transformer.setShapeCenterPos(center);
    }

    public static class SimpleOBJModel {
        public ArrayList<Vec3> vertices = new ArrayList<>();
        public ArrayList<int[]> faces = new ArrayList<>();
    }


    public void loadOBJ(ResourceLocation location) throws IOException {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();

        if(manager.getResource(location).isPresent()) {
            Resource resource = manager.getResource(location).get();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open()));
            SimpleOBJModel m = new SimpleOBJModel();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                switch (parts[0]) {
                    case "v":
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        double z = Double.parseDouble(parts[3]);
                        m.vertices.add(new Vec3(x, y, z));
                        break;
                    case "f":
                        int[] faceIndices = new int[parts.length - 1];
                        for (int i = 1; i < parts.length; i++) {
                            String vert = parts[i];
                            String[] vParts = vert.split("/");
                            faceIndices[i - 1] = Integer.parseInt(vParts[0]) - 1;
                        }
                        m.faces.add(faceIndices);
                        break;
                }
            }

            this.model = m;
        }
    }

    @Override
    public void draw(VertexBuilder builder) {
        builder.putColor(this.color);
        if (model == null) return;
        for (int[] face : model.faces) {
            if (face.length < 3) continue;

            for (int i = 1; i < face.length - 1; i++) {
                Vec3 v0 = model.vertices.get(face[0]).subtract(modelCenter).add(centerPoint);
                Vec3 v1 = model.vertices.get(face[i]).subtract(modelCenter).add(centerPoint);
                Vec3 v2 = model.vertices.get(face[i + 1]).subtract(modelCenter).add(centerPoint);

                builder.putVertex(v0);
                builder.putVertex(v1);
                builder.putVertex(v2);
            }
        }
    }
    @Override
    public Vec3 calculateShapeCenterPos(){
        if (model == null || model.vertices.isEmpty()) return centerPoint;

        Vec3 min = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Vec3 max = new Vec3(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);

        for (Vec3 v : model.vertices) {
            min = new Vec3(Math.min(min.x,v.x), Math.min(min.y,v.y), Math.min(min.z,v.z));
            max = new Vec3(Math.max(max.x,v.x), Math.max(max.y,v.y), Math.max(max.z,v.z));
        }

        Vec3 modelCenter = new Vec3(
                (min.x + max.x)/2,
                (min.y + max.y)/2,
                (min.z + max.z)/2
        );
        this.modelCenter = modelCenter;
        return this.centerPoint;
    }
}
