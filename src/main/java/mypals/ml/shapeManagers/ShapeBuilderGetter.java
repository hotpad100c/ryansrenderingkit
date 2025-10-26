package mypals.ml.shapeManagers;

import mypals.ml.shape.box.BoxFaceShape;
import mypals.ml.shape.box.BoxWireframeShape;
import mypals.ml.shape.Shape;
import mypals.ml.shape.line.CircleShape;
import mypals.ml.shape.line.LineShape;
import mypals.ml.shape.line.StripLineShape;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ShapeBuilderGetter {
    public static Map<Class<? extends Shape>, ShapeManager> shapeManagerMap = new HashMap<>();
    public static void registerShapeBuilder(Class<? extends Shape> shapeClass, ShapeManager manager){
        shapeManagerMap.put(shapeClass,manager);
    }
    public static void init(){
        registerShapeBuilder(BoxWireframeShape.class, ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(BoxFaceShape.class, ShapeManagers.QUADS_SHAPE_MANAGER);
        registerShapeBuilder(LineShape.class,ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(StripLineShape.class,ShapeManagers.LINE_STRIP_SHAPE_MANAGER);
        registerShapeBuilder(CircleShape.class,ShapeManagers.LINE_STRIP_SHAPE_MANAGER);
    }
    public static ShapeManager getBuilderManager(Shape shape){
        return shapeManagerMap.getOrDefault(shape.getClass(),null);
    }

    public void removeShapes(Identifier root){

    }
}
