package ml.mypals.ryansrenderingkit.shapeManagers;

import com.mojang.blaze3d.vertex.PoseStack;
import ml.mypals.ryansrenderingkit.builderManager.BuilderManager;
import ml.mypals.ryansrenderingkit.shape.Shape;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static ml.mypals.ryansrenderingkit.RyansRenderingKit.RENDER_PROFILER;
import static ml.mypals.ryansrenderingkit.shapeManagers.ShapeManagers.TEMP_HEADER;

public class ShapeManager {
    public String id;
    public ShapeGroup immediateShapeGroup;
    public ShapeGroup batchShapeGroup;
    public ShapeGroup bufferedShapeGroup;
    public BuilderManager builderManager;

    public static Comparator<Shape> SHAPE_ORDER_COMPARATOR = (s1, s2) -> Double.compare(
            s2.transformer.getWorldPivot().lengthSqr(),
            s1.transformer.getWorldPivot().lengthSqr()
    );

    public ShapeManager(BuilderManager builderManager, String id) {
        this.id = id;
        this.builderManager = builderManager;
        immediateShapeGroup = new ShapeGroup();
        batchShapeGroup = new ShapeGroup();
        bufferedShapeGroup = new ShapeGroup();
    }

    public void syncShapeTransform() {
        immediateShapeGroup.syncShapeTransform();
        batchShapeGroup.syncShapeTransform();
    }

    public void addShape(Identifier identifier, Shape shape) {
        switch (shape.type) {
            case IMMEDIATE -> immediateShapeGroup.addShape(identifier, shape);
            case BATCH     -> batchShapeGroup.addShape(identifier, shape);
            case BUFFERED  -> {
                if (identifier.getPath().startsWith(TEMP_HEADER))
                    throw new UnsupportedOperationException("Buffered shapes cant be temporary, use IMMEDIATE or BATCH types for temporary shapes.");
                bufferedShapeGroup.addShape(identifier, shape);
                builderManager.rebuildVBO(
                        shape.seeThrough
                                ? bufferedShapeGroup.seeThroughShapeMap.values()
                                : bufferedShapeGroup.normalShapeMap.values(),
                        shape.seeThrough);
            }
        }
    }

    public void removeShape(Identifier identifier) {
        immediateShapeGroup.removeShape(identifier);
        batchShapeGroup.removeShape(identifier);
        int removed = bufferedShapeGroup.removeShapeTracked(identifier);
        if ((removed & ShapeGroup.REMOVED_NORMAL)      != 0) builderManager.rebuildVBO(bufferedShapeGroup.normalShapeMap.values(),      false);
        if ((removed & ShapeGroup.REMOVED_SEE_THROUGH) != 0) builderManager.rebuildVBO(bufferedShapeGroup.seeThroughShapeMap.values(), true);
    }

    public void removeShapes(Identifier root) {
        immediateShapeGroup.removeShapes(root);
        batchShapeGroup.removeShapes(root);
        int removed = bufferedShapeGroup.removeShapesTracked(root);
        if ((removed & ShapeGroup.REMOVED_NORMAL)      != 0) builderManager.rebuildVBO(bufferedShapeGroup.normalShapeMap.values(),      false);
        if ((removed & ShapeGroup.REMOVED_SEE_THROUGH) != 0) builderManager.rebuildVBO(bufferedShapeGroup.seeThroughShapeMap.values(), true);
    }

    public void draw(PoseStack matrixStack, float tickDelta) {
        RENDER_PROFILER.push("batchDraw");
        batchShapeGroup.drawBatched(builderManager, matrixStack, tickDelta);
        RENDER_PROFILER.pop();
        RENDER_PROFILER.push("singleDraw");
        immediateShapeGroup.drawImmediate(builderManager, matrixStack, tickDelta);
        RENDER_PROFILER.pop();
        RENDER_PROFILER.push("bufferedDraw");
        bufferedShapeGroup.drawBuffered(builderManager);
        RENDER_PROFILER.pop();
    }

    public static class ShapeGroup {
        public static final int REMOVED_NORMAL      = 1;
        public static final int REMOVED_SEE_THROUGH = 2;

        public ConcurrentHashMap<Identifier, Shape> normalShapeMap     = new ConcurrentHashMap<>();
        public ConcurrentHashMap<Identifier, Shape> seeThroughShapeMap = new ConcurrentHashMap<>();

        private void forBothMaps(Consumer<ConcurrentHashMap<Identifier, Shape>> action) {
            action.accept(normalShapeMap);
            action.accept(seeThroughShapeMap);
        }

        public void addShape(Identifier id, Shape shape) {
            (shape.seeThrough ? seeThroughShapeMap : normalShapeMap).put(id, shape);
        }

        public boolean removeShape(@NotNull Identifier identifier) {
            return removeShapeTracked(identifier) != 0;
        }

        public int removeShapeTracked(@NotNull Identifier identifier) {
            int flags = 0;
            if (normalShapeMap.remove(identifier)      != null) flags |= REMOVED_NORMAL;
            if (seeThroughShapeMap.remove(identifier)  != null) flags |= REMOVED_SEE_THROUGH;
            return flags;
        }

        public boolean removeShapes(@NotNull Identifier identifier) {
            return removeShapesTracked(identifier) != 0;
        }

        public int removeShapesTracked(@NotNull Identifier identifier) {
            String namespace = identifier.getNamespace();
            String path = identifier.getPath();
            Predicate<Map.Entry<Identifier, Shape>> pred = entry ->
                    entry.getKey().getNamespace().equals(namespace)
                            && entry.getKey().getPath().startsWith(path);

            int flags = 0;
            if (normalShapeMap.entrySet().removeIf(pred))      flags |= REMOVED_NORMAL;
            if (seeThroughShapeMap.entrySet().removeIf(pred))  flags |= REMOVED_SEE_THROUGH;
            return flags;
        }

        public void clear()     { forBothMaps(Map::clear); }

        public void clearTemp() {
            Predicate<Map.Entry<Identifier, Shape>> isTemp =
                    entry -> entry.getKey().getPath().startsWith(TEMP_HEADER);
            forBothMaps(map -> map.entrySet().removeIf(isTemp));
        }

        public void syncShapeTransform() {
            forBothMaps(map -> map.values().forEach(Shape::syncLastToTarget));
        }

        public void drawImmediate(BuilderManager builderManager, PoseStack matrixStack, float tickDelta) {
            for (Shape shape : normalShapeMap.values()) {
                builderManager.drawImmediate(shape, builder -> shape.draw(true, builder, matrixStack, tickDelta));
            }
            for (Shape shape : seeThroughShapeMap.values()) {
                builderManager.drawImmediate(shape, builder -> shape.draw(true, builder, matrixStack, tickDelta));
            }
        }

        public void drawBatched(BuilderManager builderManager, PoseStack matrixStack, float tickDelta) {
            if (!normalShapeMap.isEmpty()) {
                builderManager.drawBatch(builder -> {
                    for (Shape shape : normalShapeMap.values()) shape.draw(true, builder, matrixStack, tickDelta);
                }, false);
            }
            if (!seeThroughShapeMap.isEmpty()) {
                builderManager.drawBatch(builder -> {
                    for (Shape shape : seeThroughShapeMap.values()) shape.draw(true, builder, matrixStack, tickDelta);
                }, true);
            }
        }

        public void drawBuffered(BuilderManager builderManager) {
            builderManager.drawVBO();
        }
    }
}
