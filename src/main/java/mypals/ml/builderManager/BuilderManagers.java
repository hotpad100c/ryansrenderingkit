package mypals.ml.builderManager;

import mypals.ml.render.RenderMethod;
import mypals.ml.shapeManagers.ShapeManager;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class BuilderManagers {
    public static BuilderManager QUADS_BUILDER_MANAGER = null;
    public static BuilderManager LINES_BUILDER_MANAGER = null;
    public static BuilderManager LINE_STRIP_BUILDER_MANAGER = null;
    public static BuilderManager TRIANGLES_BUILDER_MANAGER = null;
    public static BuilderManager TRIANGLES_STRIP_BUILDER_MANAGER = null;
    public static BuilderManager TRIANGLES_FAN_BUILDER_MANAGER = null;
    public static BuilderManager TEXT = null;
    public static List<BuilderManager> builders = new ArrayList<>();
    public static void init() {
        Matrix4f matrix4f = new Matrix4f();
        QUADS_BUILDER_MANAGER = register(matrix4f,RenderMethod.QUADS,"quads_builder_manager");
        LINES_BUILDER_MANAGER = register(matrix4f, RenderMethod.LINES,"lines_builder_manager");
        LINE_STRIP_BUILDER_MANAGER = register(matrix4f,RenderMethod.LINE_STRIP,"line_strip_builder_manager");
        TRIANGLES_BUILDER_MANAGER = register(matrix4f,RenderMethod.TRIANGLES,"triangles_builder_manager");
        TRIANGLES_STRIP_BUILDER_MANAGER = register(matrix4f,RenderMethod.TRIANGLES_STRIP,"triangles_strip_builder_manager");
        TRIANGLES_FAN_BUILDER_MANAGER = register(matrix4f,RenderMethod.TRIANGLES_FAN,"triangles_fan_builder_manager");
        TEXT = register(matrix4f,RenderMethod.TEXT,"text_manager");
    }
    public static void updateMatrix(Matrix4f matrix4f){
        for(BuilderManager builderManager : builders){
            builderManager.updateMatrix(matrix4f);
        }
    }
    public static BuilderManager register(Matrix4f matrix4f, RenderMethod renderMethod,String id){
        BuilderManager builderManager = new BuilderManager(matrix4f,renderMethod,id);
        builders.add(builderManager);
        return builderManager;
    }
}
