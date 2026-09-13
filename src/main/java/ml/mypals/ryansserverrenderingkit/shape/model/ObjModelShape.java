package ml.mypals.ryansserverrenderingkit.shape.model;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static ml.mypals.ryansserverrenderingkit.RyansServerRenderingKit.LOGGER;

public class ObjModelShape extends Shape {

    public Vec3 modelCenter = Vec3.ZERO;
    public SimpleOBJModel model = new SimpleOBJModel();
    public final Identifier resourceLocation;
    private final List<int[]> wireframeEdges = new ArrayList<>();

    public ObjModelShape(Consumer<DefaultTransformer> transform,
                         Identifier resourceLocation,
                         Vec3 center,
                         Color color) {
        this(transform, resourceLocation, center, color, false);
    }

    public ObjModelShape(Consumer<DefaultTransformer> transform,
                         Identifier resourceLocation,
                         Vec3 center,
                         Color color,
                         boolean seeThrough) {
        super(transform, color, center, seeThrough);
        this.resourceLocation = resourceLocation;
        this.transformer.setShapeWorldPivot(center);
        if (resourceLocation != null) {
            try {
                loadOBJ(resourceLocation);
            } catch (IOException | RuntimeException exception) {
                LOGGER.error("Failed to load OBJ model {}", resourceLocation, exception);
            }
        }
        syncLastToTarget();
        generateRawGeometry(false);
    }

    public static class SimpleOBJModel {
        public final ArrayList<Vec3> vertices = new ArrayList<>();
        public final ArrayList<int[]> faces = new ArrayList<>();
    }

    public void loadOBJ(Identifier location) throws IOException {
        String path = "assets/" + location.getNamespace() + "/" + location.getPath();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader != null ? loader.getResourceAsStream(path) : null;
        if (stream == null) stream = ObjModelShape.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) throw new IOException("OBJ resource not found: " + location);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            model = parseOBJ(reader);
        }
        calculateShapeCenterPos();
        generateRawGeometry(false);
    }

    static SimpleOBJModel parseOBJ(BufferedReader reader) throws IOException {
        SimpleOBJModel result = new SimpleOBJModel();
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            int comment = line.indexOf('#');
            if (comment >= 0) line = line.substring(0, comment);
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            if (parts[0].equals("v") && parts.length >= 4) {
                result.vertices.add(new Vec3(
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3])));
            } else if (parts[0].equals("f") && parts.length >= 4) {
                int[] face = new int[parts.length - 1];
                for (int i = 1; i < parts.length; i++) {
                    String vertexToken = parts[i].split("/", -1)[0];
                    int rawIndex = Integer.parseInt(vertexToken);
                    int index = rawIndex > 0 ? rawIndex - 1 : result.vertices.size() + rawIndex;
                    if (rawIndex == 0 || index < 0 || index >= result.vertices.size()) {
                        throw new IOException("Invalid vertex index at OBJ line " + lineNumber);
                    }
                    face[i - 1] = index;
                }
                result.faces.add(face);
            }
        }
        return result;
    }

    public Vec3 calculateShapeCenterPos() {
        if (model.vertices.isEmpty()) return Vec3.ZERO;
        Vec3 min = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Vec3 max = new Vec3(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
        for (Vec3 vertex : model.vertices) {
            min = new Vec3(Math.min(min.x, vertex.x), Math.min(min.y, vertex.y), Math.min(min.z, vertex.z));
            max = new Vec3(Math.max(max.x, vertex.x), Math.max(max.y, vertex.y), Math.max(max.z, vertex.z));
        }
        modelCenter = min.add(max).scale(0.5D);
        return modelCenter;
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();
        modelVertexes.addAll(model.vertices);

        List<Integer> triangles = new ArrayList<>();
        Set<Long> uniqueEdges = new LinkedHashSet<>();
        wireframeEdges.clear();
        for (int[] face : model.faces) {
            // ponytail: OBJ faces are fan-triangulated; add ear clipping only if concave polygons are needed.
            for (int i = 1; i < face.length - 1; i++) {
                triangles.add(face[0]);
                triangles.add(face[i]);
                triangles.add(face[i + 1]);
            }
            for (int i = 0; i < face.length; i++) {
                int a = face[i];
                int b = face[(i + 1) % face.length];
                int min = Math.min(a, b);
                int max = Math.max(a, b);
                long key = ((long) min << 32) | (max & 0xffffffffL);
                if (uniqueEdges.add(key)) wireframeEdges.add(new int[]{a, b});
            }
        }
        indexBuffer = triangles.stream().mapToInt(Integer::intValue).toArray();
    }

    private Matrix4f relativeModelMatrix(boolean lerp, Vec3 spawnPosition) {
        return new Matrix4f()
                .translate((float) -spawnPosition.x, (float) -spawnPosition.y, (float) -spawnPosition.z)
                .mul(transformer.buildCombinedMatrix(lerp));
    }

    private List<Transformation> getFacePanels(boolean lerp, Vec3 spawnPosition) {
        Matrix4f modelMatrix = relativeModelMatrix(lerp, spawnPosition);
        List<Transformation> panels = new ArrayList<>(indexBuffer.length);
        for (int i = 0; i < indexBuffer.length; i += 3) {
            panels.addAll(DisplayTransformHelper.localTextTriangle(
                    vector(modelVertexes.get(indexBuffer[i])),
                    vector(modelVertexes.get(indexBuffer[i + 1])),
                    vector(modelVertexes.get(indexBuffer[i + 2])), modelMatrix));
        }
        return panels;
    }

    private List<Transformation> getWireframeSegments(boolean lerp, Vec3 spawnPosition) {
        Matrix4f modelMatrix = relativeModelMatrix(lerp, spawnPosition);
        List<Transformation> segments = new ArrayList<>(wireframeEdges.size());
        for (int[] edge : wireframeEdges) {
            Vector3f a = vector(modelVertexes.get(edge[0])).mulPosition(modelMatrix);
            Vector3f b = vector(modelVertexes.get(edge[1])).mulPosition(modelMatrix);
            Transformation segment = DisplayTransformHelper.segment(
                    new Vec3(a.x, a.y, a.z), new Vec3(b.x, b.y, b.z), wireframeWidth);
            if (segment != null) segments.add(segment);
        }
        return segments;
    }

    private static Vector3f vector(Vec3 value) {
        return new Vector3f((float) value.x, (float) value.y, (float) value.z);
    }

    @Override
    public void initDisplays(ServerLevel level) {
        Vec3 spawnPosition = transformer.getShapeWorldPivot(false);
        if (renderFace) {
            for (Transformation panel : getFacePanels(false, spawnPosition)) {
                displays.add(VirtualDisplay.solidTextPanel(level, spawnPosition.x, spawnPosition.y,
                                spawnPosition.z, baseColor.getRGB())
                        .bright().seeThrough(seeThrough, baseColor.getRGB()).transform(panel));
            }
        }
        if (renderWireframe) {
            for (Transformation segment : getWireframeSegments(false, spawnPosition)) {
                displays.add(VirtualDisplay.block(level, spawnPosition.x, spawnPosition.y,
                                spawnPosition.z, getBlockState())
                        .bright().seeThrough(seeThrough, baseColor.getRGB()).transform(segment));
            }
        }
    }

    @Override
    public void updateDisplays() {
        Vec3 spawnPosition = displays.isEmpty() ? transformer.getWorldPivot() : new Vec3(
                displays.getFirst().getEntity().getX(), displays.getFirst().getEntity().getY(),
                displays.getFirst().getEntity().getZ());
        List<Transformation> panels = renderFace ? getFacePanels(true, spawnPosition) : List.of();
        List<Transformation> segments = renderWireframe ? getWireframeSegments(true, spawnPosition) : List.of();
        if (displays.size() != panels.size() + segments.size()) {
            ServerLevel targetLevel = displays.isEmpty() ? level : displays.getFirst().getLevel();
            removeDisplays();
            if (targetLevel != null) initDisplays(targetLevel);
            return;
        }

        int displayIndex = 0;
        for (Transformation panel : panels) {
            displays.get(displayIndex++).backgroundColor(baseColor.getRGB())
                    .seeThrough(seeThrough, baseColor.getRGB()).transform(panel);
        }
        for (Transformation segment : segments) {
            displays.get(displayIndex++).blockState(getBlockState())
                    .seeThrough(seeThrough, baseColor.getRGB()).transform(segment);
        }
    }
}
