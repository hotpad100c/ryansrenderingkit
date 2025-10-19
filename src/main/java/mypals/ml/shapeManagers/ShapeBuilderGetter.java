package mypals.ml.shapeManagers;

import mypals.ml.shape.box.BoxShape;
import mypals.ml.shape.box.BoxWireframeShape;
import mypals.ml.shape.Shape;

import java.util.HashMap;
import java.util.Map;

public class ShapeBuilderGetter {
    public static Map<Class<? extends Shape>, ShapeManager> shapeManagerMap = new HashMap<>();
    public static void registerShapeBuilder(Class<? extends Shape> shapeClass, ShapeManager manager){
        shapeManagerMap.put(shapeClass,manager);
    }
    public static void init(){
        registerShapeBuilder(BoxWireframeShape.class, ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(BoxShape.class, ShapeManagers.QUADS_SHAPE_MANAGER);
    }
    public static ShapeManager getBuilderManager(Shape shape){
        return shapeManagerMap.getOrDefault(shape.getClass(),null);
    }
}
