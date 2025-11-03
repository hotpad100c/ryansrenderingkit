package mypals.ml.shapeManagers;
import mypals.ml.shape.basics.tags.ExtractableShape;
import net.minecraft.resources.ResourceLocation;
import mypals.ml.builderManager.BuilderManager;
import mypals.ml.builderManager.BuilderManagers;
import mypals.ml.shape.Shape;
import java.util.ArrayList;
import java.util.List;

import static mypals.ml.Helpers.generateUniqueId;

import com.mojang.blaze3d.vertex.PoseStack;

public class ShapeManagers {
    public static final String TEMP_HEADER = "temp_shape";
    public static ShapeManager QUADS_SHAPE_MANAGER;
    public static ShapeManager LINES_SHAPE_MANAGER;
    public static ShapeManager LINE_STRIP_SHAPE_MANAGER;
    public static ShapeManager TEXT;
    public static ShapeManager TRIANGLES_SHAPE_MANAGER;
    public static ShapeManager TRIANGLES_STRIP_SHAPE_MANAGER;
    public static ShapeManager TRIANGLES_FAN_SHAPE_MANAGER;
    public static List<ShapeManager> managers = new ArrayList<>();
    public static void init(){
        QUADS_SHAPE_MANAGER = register(BuilderManagers.QUADS_BUILDER_MANAGER,"quads_shape_manager");
        LINE_STRIP_SHAPE_MANAGER = register(BuilderManagers.LINE_STRIP_BUILDER_MANAGER,"line_strip_shape_manager");
        LINES_SHAPE_MANAGER = register(BuilderManagers.LINES_BUILDER_MANAGER,"lines_shape_manager");
        TRIANGLES_SHAPE_MANAGER = register(BuilderManagers.TRIANGLES_BUILDER_MANAGER,"triangles_shape_manager");
        TRIANGLES_STRIP_SHAPE_MANAGER = register(BuilderManagers.TRIANGLES_STRIP_BUILDER_MANAGER,"triangles_strip_shape_manager");
        TRIANGLES_FAN_SHAPE_MANAGER = register(BuilderManagers.TRIANGLES_FAN_BUILDER_MANAGER,"triangles_fan_shape_manager");
        TEXT = register(BuilderManagers.TEXT,"text_manager");
    }
    public static void renderAll(PoseStack matrixStack, float tickDelta){
        for(ShapeManager manager : managers
        ){
            manager.draw(matrixStack, tickDelta);
            manager.clearTempAfterRender();
        }
    }
    public static ShapeManager register(BuilderManager builderManager,String id){
        ShapeManager shapeManager = new ShapeManager(builderManager,id);
        managers.add(shapeManager);
        return shapeManager;
    }
    public static void removeShape(ResourceLocation identifier){
        managers.forEach(
                (shapeManager) ->   {
                    shapeManager.removeShape(identifier);
                }
        );
    }
    public static void removeShapes(ResourceLocation root){
        managers.forEach(
                (shapeManager) ->{
                    shapeManager.removeShapes(root);
                }
        );
    }

    public static void addShape(ResourceLocation identifier, Shape shape){
        shape.setId(identifier);
        if(shape instanceof ExtractableShape exts){
            exts.addGroup(identifier);
            return;
        }
        VertexBuilderGetter.getBuilderManager(shape).addShape(identifier,shape);
    }
    public static void syncShapeTransform(){
        for(ShapeManager manager : managers){
            manager.syncShapeTransform();
        }
    }
    public static void addShape(Shape shape){
        addShape(generateUniqueId(TEMP_HEADER),shape);
    }
}
