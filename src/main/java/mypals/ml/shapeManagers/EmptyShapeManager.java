package mypals.ml.shapeManagers;

import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builderManager.EmptyBuilderManager;
import mypals.ml.shape.Shape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static mypals.ml.shapeManagers.ShapeManagers.TEMP_HEADER;

public class EmptyShapeManager {
    public String id;
    public EmptyShapeGroup shapeGroup;
    public EmptyBuilderManager builderManager;
    public static Comparator<Shape> SHAPE_ORDER_COMPARATOR = (s1, s2) -> {
        Vec3 shape1Pos = s1.transformer.getWorldPivot();
        Vec3 shape2Pos = s2.transformer.getWorldPivot();
        double distance1 = shape1Pos.lengthSqr(); // Square of distance for efficiency
        double distance2 = shape2Pos.lengthSqr();
        return Double.compare(distance2, distance1);
    };

    public EmptyShapeManager(EmptyBuilderManager builderManager, String id) {
        this.id = id;
        shapeGroup = new EmptyShapeGroup();
        this.builderManager = builderManager;
    }

    public void syncShapeTransform() {
        shapeGroup.syncShapeTransform();
    }

    public void addShape(ResourceLocation identifier, Shape shape) {
        shapeGroup.addShape(identifier, shape);
    }

    public void removeShape(ResourceLocation identifier) {
        shapeGroup.removeShape(identifier);
    }

    public void removeShapes(ResourceLocation root) {
        shapeGroup.removeShapes(root);
    }

    public void draw(PoseStack matrixStack, float tickDelta) {
        shapeGroup.drawAll(builderManager, matrixStack, tickDelta);
    }

    public void clearTempAfterRender() {
        shapeGroup.clearTemp();
    }

    public static class EmptyShapeGroup {
        public ConcurrentHashMap<ResourceLocation, Shape> shapeMap = new ConcurrentHashMap<>();

        public void addShape(ResourceLocation id, Shape shape) {
            shapeMap.put(id, shape);
        }

        public void removeShape(@NotNull ResourceLocation identifier) {
            shapeMap.remove(identifier);
        }

        public void removeShapes(@NotNull ResourceLocation identifier) {
            shapeMap.entrySet().removeIf(entry -> entry.getKey().getPath().startsWith(identifier.getPath()));
        }

        public void clear() {
            shapeMap.clear();
        }

        public void clearTemp() {
            shapeMap.entrySet().removeIf(entry -> entry.getKey().getPath().startsWith(TEMP_HEADER));
        }

        public void syncShapeTransform() {
            for (Shape shape : shapeMap.values()) {
                shape.syncLastToTarget();
            }
        }

        public void drawAll(EmptyBuilderManager builderManager, PoseStack matrixStack, float tickDelta) {
            if (!shapeMap.isEmpty()) {
                List<Shape> sortedShapes = new ArrayList<>(shapeMap.values());
                sortedShapes.sort(SHAPE_ORDER_COMPARATOR);
                for (Shape shape : sortedShapes) {
                    builderManager.draw(builder -> {
                        shape.draw(true, builder, matrixStack, tickDelta);
                    });
                }
            }
        }
    }
}
