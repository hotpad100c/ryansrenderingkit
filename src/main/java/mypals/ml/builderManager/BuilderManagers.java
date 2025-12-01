package mypals.ml.builderManager;

import mypals.ml.render.RenderMethod;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class BuilderManagers {
    public static BuilderManager LINES_BUILDER_MANAGER = null;
    public static BuilderManager LINE_STRIP_BUILDER_MANAGER = null;
    public static BuilderManager TRIANGLES_BUILDER_MANAGER = null;
    public static EmptyBuilderManager NON_SHAPE_OBJECTS = null;
    public static List<BuilderManager> builders = new ArrayList<>();
    public static List<EmptyBuilderManager> emptyBuilders = new ArrayList<>();

    public static void init() {
        Matrix4f matrix4f = new Matrix4f();
        LINES_BUILDER_MANAGER = register(matrix4f, RenderMethod.LINES, "lines_builder_manager");
        LINE_STRIP_BUILDER_MANAGER = register(matrix4f, RenderMethod.LINE_STRIP, "line_strip_builder_manager");
        TRIANGLES_BUILDER_MANAGER = register(matrix4f, RenderMethod.TRIANGLES, "triangles_builder_manager");
        NON_SHAPE_OBJECTS = registerEmpty(matrix4f, "empty");
    }

    public static void updateMatrix(Matrix4f matrix4f) {
        for (BuilderManager builderManager : builders) {
            builderManager.updateMatrix(matrix4f);
        }
        for (EmptyBuilderManager builderManager : emptyBuilders) {
            builderManager.updateMatrix(matrix4f);
        }
    }

    public static BuilderManager register(Matrix4f matrix4f, RenderMethod renderMethod, String id) {
        BuilderManager builderManager = new BuilderManager(matrix4f, renderMethod, id);
        builders.add(builderManager);
        return builderManager;
    }

    public static EmptyBuilderManager registerEmpty(Matrix4f matrix4f, String id) {
        EmptyBuilderManager builderManager = new EmptyBuilderManager(matrix4f, id);
        emptyBuilders.add(builderManager);
        return builderManager;
    }
}
