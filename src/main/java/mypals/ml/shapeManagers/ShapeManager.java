package mypals.ml.shapeManagers;

import mypals.ml.builderManager.BuilderManager;
import mypals.ml.shape.Shape;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    public void syncShapeTransform(){
        immediateShapeGroup.syncShapeTransform();
        batchShapeGroup.syncShapeTransform();
    }
    public void addShape(Identifier identifier,Shape shape){
        switch (shape.type){
            case IMMEDIATE -> immediateShapeGroup.addShape(identifier,shape);
            case BATCH -> batchShapeGroup.addShape(identifier,shape);
            case BUFFERED -> {
                if(identifier.getPath().startsWith(TEMP_HEADER)) throw new UnsupportedOperationException("Buffered shapes cant be temporary, use IMMEDIATE or BATCH types for temporary shapes.");
                bufferedShapeGroup.addShape(identifier,shape);
                builderManager.rebuildVBO(
                        shape.seeThrough?
                                bufferedShapeGroup.seeThroughShapeMap.values()
                                :
                                bufferedShapeGroup.normalShapeMap.values()
                        , shape.seeThrough);
                }
        }
    }
    public void removeShape(Identifier identifier){
        immediateShapeGroup.removeShape(identifier);
        batchShapeGroup.removeShape(identifier);
        bufferedShapeGroup.removeShape(identifier);
    }
    public void removeShapes(Identifier root){
        immediateShapeGroup.removeShape(root);
        batchShapeGroup.removeShape(root);
        bufferedShapeGroup.removeShape(root);
    }
    public void draw(MatrixStack matrixStack,float tickDelta){
        batchShapeGroup.drawBatched(this.builderManager,matrixStack,tickDelta);
        immediateShapeGroup.drawImmediate(this.builderManager,matrixStack,tickDelta);
        bufferedShapeGroup.drawBuffered(this.builderManager);
    }
    public void clearTempAfterRender(){
        immediateShapeGroup.clearTemp();
        batchShapeGroup.clearTemp();
        bufferedShapeGroup.clearTemp();
    }
    public static class ShapeGroup{
        public ConcurrentHashMap<Identifier, Shape> normalShapeMap = new ConcurrentHashMap<>(){};
        public ConcurrentHashMap<Identifier, Shape> seeThroughShapeMap = new ConcurrentHashMap<>();
        public void addShape(Identifier id,Shape shape){
            if(shape.seeThrough){
                seeThroughShapeMap.put(id,shape);

            }else{
                normalShapeMap.put(id,shape);
            }
        }
        public void removeShape(@NotNull Identifier identifier){
            seeThroughShapeMap.remove(identifier);
            normalShapeMap.remove(identifier);
        }
        public void removeShapes(@NotNull Identifier identifier) {
            seeThroughShapeMap.entrySet().removeIf(entry -> entry.getKey().getPath().startsWith(identifier.getPath()));
            normalShapeMap.entrySet().removeIf(entry -> entry.getKey().getPath().startsWith(identifier.getPath()));
        }
        public void clear(){
            normalShapeMap.clear();
            seeThroughShapeMap.clear();
        }
        public void clearTemp(){
            normalShapeMap.entrySet().removeIf(entry -> entry.getKey().getPath().startsWith(TEMP_HEADER));
            seeThroughShapeMap.entrySet().removeIf(entry -> entry.getKey().getPath().startsWith(TEMP_HEADER));
        }
        public void syncShapeTransform(){
            for (Shape shape : normalShapeMap.values()) {
                shape.syncLastToTarget();
            }
            for (Shape shape : seeThroughShapeMap.values()) {
                shape.syncLastToTarget();
            }
        }
        public void drawImmediate(BuilderManager builderManager, MatrixStack matrixStack,float tickDelta){
            if(!normalShapeMap.isEmpty()) {
                List<Shape> sortedShapes = new ArrayList<>(normalShapeMap.values());
                sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                for (Shape shape : sortedShapes) {
                    builderManager.drawImmediate(shape, builder -> {
                        shape.draw(builder, matrixStack,tickDelta);
                    });
                }
            }
            if(!seeThroughShapeMap.isEmpty()) {
                List<Shape> sortedShapes = new ArrayList<>(seeThroughShapeMap.values());
                sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                for (Shape shape : sortedShapes) {
                    builderManager.drawImmediate(shape, builder -> {
                        shape.draw(builder, matrixStack,tickDelta);
                    });
                }
            }
        }
        public void drawBatched(BuilderManager builderManager, MatrixStack matrixStack,float tickDelta){
            if(!normalShapeMap.isEmpty()) {
                builderManager.drawBatch(builder -> {
                    List<Shape> sortedShapes = new ArrayList<>(normalShapeMap.values());
                    sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                    for (Shape shape : sortedShapes) {
                        shape.draw(builder, matrixStack,tickDelta);
                    }
                }, false);
            }
            if(!seeThroughShapeMap.isEmpty()) {
                builderManager.drawBatch(builder -> {
                    List<Shape> sortedShapes = new ArrayList<>(seeThroughShapeMap.values());
                    sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                    for (Shape shape : sortedShapes) {
                        shape.draw(builder, matrixStack,tickDelta);
                    }
                }, true);
            }
        }
        public void drawBuffered(BuilderManager builderManager){
            builderManager.drawVBO();
        }
    }
}
