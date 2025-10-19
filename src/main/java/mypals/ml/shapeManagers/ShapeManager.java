package mypals.ml.shapeManagers;

import mypals.ml.Helpers;
import mypals.ml.builderManager.BuilderManager;
import mypals.ml.shape.Shape;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.*;

import static mypals.ml.Helpers.generateUniqueId;
import static mypals.ml.shapeManagers.ShapeManagers.TEMP_HEADER;

public class ShapeManager {
    public String id;
    public ShapeGroup immediateShapeGroup;
    public ShapeGroup batchShapeGroup;
    public ShapeGroup bufferedShapeGroup;
    public BuilderManager builderManager;
    public static Comparator<Shape> SHAPE_ORDER_COMPARATOR = (s1, s2) -> {
        Vec3d shape1Pos = s1.centerPoint;
        Vec3d shape2Pos = s2.centerPoint;
        double distance1 = shape1Pos.lengthSquared(); // Square of distance for efficiency
        double distance2 = shape2Pos.lengthSquared();
        return Double.compare(distance2, distance1);
    };
    public ShapeManager(BuilderManager builderManager, String id){
        this.id = id;
        immediateShapeGroup = new ShapeGroup();
        batchShapeGroup = new ShapeGroup();
        bufferedShapeGroup = new ShapeGroup();
        this.builderManager = builderManager;
    }
    public void addShape(Identifier identifier,Shape shape){
        switch (shape.type){
            case IMMEDIATE -> immediateShapeGroup.addShape(identifier,shape);
            case BATCH -> batchShapeGroup.addShape(identifier,shape);
            case BUFFERED -> {
                if(identifier.getPath().startsWith(TEMP_HEADER)) throw new UnsupportedOperationException("Buffered shapes cant be temporary, use IMMEDIATE or BATCH types for temporary shapes.");
                bufferedShapeGroup.addShape(identifier,shape);
                //System.out.println("Added" + (shape.seeThrough?"seeThrough":"nonSeeThrough") + " shape {" + identifier + "} to ShapeManager: " + this.id);
                builderManager.rebuildVBO(
                        shape.seeThrough?
                                bufferedShapeGroup.seeThroughShapeMap.values()
                                :
                                bufferedShapeGroup.normalShapeMap.values()
                        , shape.seeThrough);
                }
        }
    }
    public void draw(MatrixStack matrixStack){
        batchShapeGroup.drawBatched(this.builderManager,matrixStack);
        immediateShapeGroup.drawImmediate(this.builderManager,matrixStack);
        bufferedShapeGroup.drawBuffered(this.builderManager);
    }
    public void clearTempAfterRender(){
        immediateShapeGroup.clearTemp();
        batchShapeGroup.clearTemp();
        bufferedShapeGroup.clearTemp();
    }
    public static class ShapeGroup{
        public LinkedHashMap<Identifier, Shape> normalShapeMap = new LinkedHashMap<>(){};
        public LinkedHashMap<Identifier, Shape> seeThroughShapeMap = new LinkedHashMap<>();
        public void addShape(Identifier id,Shape shape){
            if(shape.seeThrough){
                seeThroughShapeMap.put(id,shape);

            }else{
                normalShapeMap.put(id,shape);
            }

        }
        public void clear(){
            normalShapeMap.clear();
            seeThroughShapeMap.clear();
        }
        public void clearTemp(){
            normalShapeMap.entrySet().removeIf(entry -> entry.getKey().getPath().startsWith(TEMP_HEADER));
            seeThroughShapeMap.entrySet().removeIf(entry -> entry.getKey().getPath().startsWith(TEMP_HEADER));
        }
        public void drawImmediate(BuilderManager builderManager, MatrixStack matrixStack){
            if(!normalShapeMap.isEmpty()) {
                List<Shape> sortedShapes = new ArrayList<>(normalShapeMap.values());
                sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                for (Shape shape : sortedShapes) {
                    builderManager.drawImmediate(shape, builder -> {
                        shape.draw(builder, matrixStack);
                    });
                }
            }
            if(!seeThroughShapeMap.isEmpty()) {
                List<Shape> sortedShapes = new ArrayList<>(seeThroughShapeMap.values());
                sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                for (Shape shape : sortedShapes) {
                    builderManager.drawImmediate(shape, builder -> {
                        shape.draw(builder, matrixStack);
                    });
                }
            }
        }
        public void drawBatched(BuilderManager builderManager, MatrixStack matrixStack){
            if(!normalShapeMap.isEmpty()) {
                builderManager.drawBatch(builder -> {
                    List<Shape> sortedShapes = new ArrayList<>(normalShapeMap.values());
                    sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                    for (Shape shape : sortedShapes) {
                        shape.draw(builder, matrixStack);
                    }
                }, false);
            }
            if(!seeThroughShapeMap.isEmpty()) {
                builderManager.drawBatch(builder -> {
                    List<Shape> sortedShapes = new ArrayList<>(seeThroughShapeMap.values());
                    sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                    for (Shape shape : sortedShapes) {
                        shape.draw(builder, matrixStack);
                    }
                }, true);
            }
        }
        public void drawBuffered(BuilderManager builderManager){
            builderManager.drawVBO();
        }
    }
}
