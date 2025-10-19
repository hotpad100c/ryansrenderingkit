package mypals.ml.shapeManagers;

import mypals.ml.builderManager.BuilderManager;
import mypals.ml.builderManager.BuilderManagers;
import mypals.ml.shape.Shape;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static mypals.ml.Helpers.generateUniqueId;

public class ShapeManagers {
    public static final String TEMP_HEADER = "temp_shape";
    public static ShapeManager QUADS_SHAPE_MANAGER;
    public static ShapeManager LINES_SHAPE_MANAGER;
    public static ShapeManager LINE_STRIP_SHAPE_MANAGER;
    public static ShapeManager TRIANGLES_SHAPE_MANAGER;
    public static List<ShapeManager> managers = new ArrayList<>();
    public static void init(){
        QUADS_SHAPE_MANAGER = register(BuilderManagers.QUADS_BUILDER_MANAGER,"quads_shape_manager");
        LINE_STRIP_SHAPE_MANAGER = register(BuilderManagers.LINE_STRIP_BUILDER_MANAGER,"line_strip_shape_manager");
        LINES_SHAPE_MANAGER = register(BuilderManagers.LINES_BUILDER_MANAGER,"lines_shape_manager");
        TRIANGLES_SHAPE_MANAGER = register(BuilderManagers.TRIANGLES_BUILDER_MANAGER,"triangles_shape_manager");
    }
    public static void renderAll(MatrixStack matrixStack){
        for(ShapeManager manager : managers
        ){
            manager.draw(matrixStack);
            manager.clearTempAfterRender();
        }
    }
    public static ShapeManager register(BuilderManager builderManager,String id){
        ShapeManager shapeManager = new ShapeManager(builderManager,id);
        managers.add(shapeManager);
        return shapeManager;
    }
    public static void addShape(Identifier identifier, Shape shape){
        if(shape.isGroupedShape){
            shape.addGroup(identifier);
            return;
        }
        ShapeBuilderGetter.getBuilderManager(shape).addShape(identifier,shape);
    }
    public static void addShape(Shape shape){
        addShape(generateUniqueId(TEMP_HEADER),shape);
    }
}
