package mypals.ml.shapeManagers;

import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builderManager.BuilderManager;
import mypals.ml.builderManager.BuilderManagers;
import mypals.ml.builderManager.EmptyBuilderManager;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.EmptyMesh;
import mypals.ml.shape.basics.tags.ExtractableShape;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static mypals.ml.RyansRenderingKit.RENDER_PROFILER;
import static mypals.ml.utils.Helpers.generateUniqueId;

public class ShapeManagers {
    public static final String TEMP_HEADER = "temp_shape";
    public static ShapeManager LINES_SHAPE_MANAGER;
    public static ShapeManager LINE_STRIP_SHAPE_MANAGER;
    public static ShapeManager TRIANGLES_SHAPE_MANAGER;
    public static EmptyShapeManager NON_SHAPE_OBJECTS;
    public static List<ShapeManager> managers = new ArrayList<>();
    public static List<EmptyShapeManager> emptyManagers = new ArrayList<>();

    public static void init() {
        LINE_STRIP_SHAPE_MANAGER = register(BuilderManagers.LINE_STRIP_BUILDER_MANAGER, "line_strip_shape_manager");
        LINES_SHAPE_MANAGER = register(BuilderManagers.LINES_BUILDER_MANAGER, "lines_shape_manager");
        TRIANGLES_SHAPE_MANAGER = register(BuilderManagers.TRIANGLES_BUILDER_MANAGER, "triangles_shape_manager");
        NON_SHAPE_OBJECTS = registerEmpty(BuilderManagers.NON_SHAPE_OBJECTS, "empty");
    }

    public static void renderAll(PoseStack matrixStack, float tickDelta) {
        for (ShapeManager manager : managers
        ) {


            RENDER_PROFILER.push("renderNormalShape");
            manager.draw(matrixStack, tickDelta);
            RENDER_PROFILER.pop();
        }
        for (EmptyShapeManager manager : emptyManagers
        ) {
            RENDER_PROFILER.push("renderSpecialShape");
            manager.draw(matrixStack, tickDelta);
            RENDER_PROFILER.pop();
        }
    }

    public static ShapeManager register(BuilderManager builderManager, String id) {
        ShapeManager shapeManager = new ShapeManager(builderManager, id);
        managers.add(shapeManager);
        return shapeManager;
    }

    public static EmptyShapeManager registerEmpty(EmptyBuilderManager builderManager, String id) {
        EmptyShapeManager shapeManager = new EmptyShapeManager(builderManager, id);
        emptyManagers.add(shapeManager);
        return shapeManager;
    }

    public static void removeShape(ResourceLocation identifier) {
        managers.forEach(
                (shapeManager) -> shapeManager.removeShape(identifier)
        );
        emptyManagers.forEach(
                (shapeManager) -> shapeManager.removeShape(identifier)
        );
    }

    public static void removeShapes(ResourceLocation root) {
        managers.forEach(
                (shapeManager) -> shapeManager.removeShapes(root)
        );
        emptyManagers.forEach(
                (shapeManager) -> shapeManager.removeShapes(root)
        );
    }

    public static void addShape(ResourceLocation identifier, Shape shape) {
        shape.setId(identifier);
        if (shape instanceof ExtractableShape exts) {
            exts.addGroup(identifier);
            return;
        }
        if (shape instanceof EmptyMesh)
            VertexBuilderGetter.getEmptyBuilderManager(shape).addShape(identifier, shape);
        else
            VertexBuilderGetter.getBuilderManager(shape).addShape(identifier, shape);
    }

    public static void syncShapeTransform() {
        for (ShapeManager manager : managers) {
            manager.syncShapeTransform();
        }
        for (EmptyShapeManager manager : emptyManagers) {
            manager.syncShapeTransform();
        }
    }

    public static void addShape(Shape shape) {
        addShape(generateUniqueId(TEMP_HEADER), shape);
    }
}
