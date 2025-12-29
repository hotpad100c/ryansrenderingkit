package ml.mypals.ryansrenderingkit.shapeManagers;

import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.box.BoxFaceShape;
import ml.mypals.ryansrenderingkit.shape.box.BoxWireframeShape;
import ml.mypals.ryansrenderingkit.shape.cylinder.ConeShape;
import ml.mypals.ryansrenderingkit.shape.cylinder.ConeWireframeShape;
import ml.mypals.ryansrenderingkit.shape.cylinder.CylinderShape;
import ml.mypals.ryansrenderingkit.shape.cylinder.CylinderWireframeShape;
import ml.mypals.ryansrenderingkit.shape.line.LineShape;
import ml.mypals.ryansrenderingkit.shape.line.StripLineShape;
import ml.mypals.ryansrenderingkit.shape.minecraftBuiltIn.BlockShape;
import ml.mypals.ryansrenderingkit.shape.minecraftBuiltIn.EntityShape;
import ml.mypals.ryansrenderingkit.shape.minecraftBuiltIn.ItemShape;
import ml.mypals.ryansrenderingkit.shape.minecraftBuiltIn.TextShape;
import ml.mypals.ryansrenderingkit.shape.model.ObjModelShape;
import ml.mypals.ryansrenderingkit.shape.model.ObjModelShapeOutline;
import ml.mypals.ryansrenderingkit.shape.round.FaceCircleShape;
import ml.mypals.ryansrenderingkit.shape.round.LineCircleShape;
import ml.mypals.ryansrenderingkit.shape.round.SphereShape;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class VertexBuilderGetter {
    public static Map<Class<? extends Shape>, ShapeManager> shapeManagerMap = new HashMap<>();
    public static Map<Class<? extends Shape>, EmptyShapeManager> emptyShapeManagerMap = new HashMap<>();

    public static void registerShapeBuilder(Class<? extends Shape> shapeClass, ShapeManager manager) {
        shapeManagerMap.put(shapeClass, manager);
    }

    public static void registerEmptyShapeBuilder(Class<? extends Shape> shapeClass, EmptyShapeManager manager) {
        emptyShapeManagerMap.put(shapeClass, manager);
    }

    public static void init() {
        registerShapeBuilder(BoxWireframeShape.class, ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(BoxFaceShape.class, ShapeManagers.TRIANGLES_SHAPE_MANAGER);
        registerShapeBuilder(LineShape.class, ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(StripLineShape.class, ShapeManagers.LINE_STRIP_SHAPE_MANAGER);
        registerShapeBuilder(LineCircleShape.class, ShapeManagers.LINE_STRIP_SHAPE_MANAGER);
        registerShapeBuilder(FaceCircleShape.class, ShapeManagers.TRIANGLES_SHAPE_MANAGER);
        registerShapeBuilder(SphereShape.class, ShapeManagers.TRIANGLES_SHAPE_MANAGER);
        registerShapeBuilder(ObjModelShape.class, ShapeManagers.TRIANGLES_SHAPE_MANAGER);
        registerShapeBuilder(ObjModelShapeOutline.class, ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(CylinderWireframeShape.class, ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(ConeWireframeShape.class, ShapeManagers.LINES_SHAPE_MANAGER);
        registerShapeBuilder(CylinderShape.class, ShapeManagers.TRIANGLES_SHAPE_MANAGER);
        registerShapeBuilder(ConeShape.class, ShapeManagers.TRIANGLES_SHAPE_MANAGER);
        registerEmptyShapeBuilder(TextShape.class, ShapeManagers.NON_SHAPE_OBJECTS);
        registerEmptyShapeBuilder(BlockShape.class, ShapeManagers.NON_SHAPE_OBJECTS);
        registerEmptyShapeBuilder(ItemShape.class, ShapeManagers.NON_SHAPE_OBJECTS);
        registerEmptyShapeBuilder(EntityShape.class, ShapeManagers.NON_SHAPE_OBJECTS);
    }

    public static ShapeManager getBuilderManager(Shape shape) {
        return shapeManagerMap.getOrDefault(shape.getClass(), null);
    }

    public static EmptyShapeManager getEmptyBuilderManager(Shape shape) {
        return emptyShapeManagerMap.getOrDefault(shape.getClass(), null);
    }

    public void removeShapes(ResourceLocation root) {

    }
}
