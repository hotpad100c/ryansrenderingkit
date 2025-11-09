package mypals.ml.shapeManagers;

import mypals.ml.shape.box.BoxFaceShape;
import mypals.ml.shape.box.BoxWireframeShape;
import mypals.ml.shape.Shape;
import mypals.ml.shape.cylinder.ConeShape;
import mypals.ml.shape.cylinder.ConeWireframeShape;
import mypals.ml.shape.cylinder.CylinderShape;
import mypals.ml.shape.cylinder.CylinderWireframeShape;
import mypals.ml.shape.model.ObjModelShape;
import mypals.ml.shape.model.ObjModelShapeOutline;
import mypals.ml.shape.round.FaceCircleShape;
import mypals.ml.shape.round.LineCircleShape;
import mypals.ml.shape.line.LineShape;
import mypals.ml.shape.line.StripLineShape;
import mypals.ml.shape.round.SphereShape;
import mypals.ml.shape.text.TextShape;
import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;
import java.util.Map;

public class VertexBuilderGetter {
    public static Map<Class<? extends Shape>, ShapeManager> shapeManagerMap = new HashMap<>();
    public static void registerShapeBuilder(Class<? extends Shape> shapeClass, ShapeManager manager){
        shapeManagerMap.put(shapeClass,manager);
    }
    public static void init(){
        registerShapeBuilder(BoxWireframeShape.class, ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(BoxFaceShape.class, ShapeManagers.TRIANGLES_SHAPE_MANAGER);
        registerShapeBuilder(LineShape.class,ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(StripLineShape.class,ShapeManagers.LINE_STRIP_SHAPE_MANAGER);
        registerShapeBuilder(LineCircleShape.class,ShapeManagers.LINE_STRIP_SHAPE_MANAGER);
        //registerShapeBuilder(TextShape.class,ShapeManagers.TEXT);
        registerShapeBuilder(FaceCircleShape.class,ShapeManagers.TRIANGLES_SHAPE_MANAGER);
        registerShapeBuilder(SphereShape.class,ShapeManagers.TRIANGLES_SHAPE_MANAGER);
        registerShapeBuilder(ObjModelShape.class,ShapeManagers.TRIANGLES_SHAPE_MANAGER);
        registerShapeBuilder(ObjModelShapeOutline.class,ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(CylinderWireframeShape.class,ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(ConeWireframeShape.class,ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(CylinderShape.class,ShapeManagers.TRIANGLES_SHAPE_MANAGER);
        registerShapeBuilder(ConeShape.class,ShapeManagers.TRIANGLES_SHAPE_MANAGER);

    }
    public static ShapeManager getBuilderManager(Shape shape){
        return shapeManagerMap.getOrDefault(shape.getClass(),null);
    }

    public void removeShapes(ResourceLocation root){

    }
}
