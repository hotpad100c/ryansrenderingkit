package mypals.ml.shapeManagers;

import mypals.ml.builderManager.BuilderManager;
import mypals.ml.shape.Shape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static mypals.ml.RyansRenderingKit.RENDER_PROFILER;
import static mypals.ml.shapeManagers.ShapeManagers.TEMP_HEADER;

import com.mojang.blaze3d.vertex.PoseStack;

public class ShapeManager {
    public String id;
    public ShapeGroup immediateShapeGroup;
    public ShapeGroup batchShapeGroup;
    public ShapeGroup bufferedShapeGroup;
    public BuilderManager builderManager;
    public static Comparator<Shape> SHAPE_ORDER_COMPARATOR = (s1, s2) -> {
        Vec3 shape1Pos = s1.transformer.getWorldPivot();
        Vec3 shape2Pos = s2.transformer.getWorldPivot();
        double distance1 = shape1Pos.lengthSqr(); // Square of distance for efficiency
        double distance2 = shape2Pos.lengthSqr();
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
    public void addShape(ResourceLocation identifier,Shape shape){
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
    public void removeShape(ResourceLocation identifier){
        immediateShapeGroup.removeShape(identifier);
        batchShapeGroup.removeShape(identifier);
        if(bufferedShapeGroup.removeShape(identifier)){
            builderManager.rebuildVBO(bufferedShapeGroup.seeThroughShapeMap.values(),true);
            builderManager.rebuildVBO(bufferedShapeGroup.normalShapeMap.values(),false);
        };
    }
    public void removeShapes(ResourceLocation root){
        immediateShapeGroup.removeShapes(root);
        batchShapeGroup.removeShapes(root);
        if(bufferedShapeGroup.removeShapes(root)){
            builderManager.rebuildVBO(bufferedShapeGroup.seeThroughShapeMap.values(),true);
            builderManager.rebuildVBO(bufferedShapeGroup.normalShapeMap.values(),false);
        };
    }
    public void draw(PoseStack matrixStack,float tickDelta){
        RENDER_PROFILER.push("batchDraw");
        batchShapeGroup.drawBatched(this.builderManager,matrixStack,tickDelta);
        RENDER_PROFILER.pop();
        RENDER_PROFILER.push("singleDraw");
        immediateShapeGroup.drawImmediate(this.builderManager,matrixStack,tickDelta);
        RENDER_PROFILER.pop();
        RENDER_PROFILER.push("bufferedDraw");
        bufferedShapeGroup.drawBuffered(this.builderManager);
        RENDER_PROFILER.pop();
    }
    public static class ShapeGroup{
        public ConcurrentHashMap<ResourceLocation, Shape> normalShapeMap = new ConcurrentHashMap<>(){};
        public ConcurrentHashMap<ResourceLocation, Shape> seeThroughShapeMap = new ConcurrentHashMap<>();
        public void addShape(ResourceLocation id,Shape shape){
            if(shape.seeThrough){
                seeThroughShapeMap.put(id,shape);

            }else{
                normalShapeMap.put(id,shape);
            }
        }
        public boolean removeShape(@NotNull ResourceLocation identifier) {
            boolean removed1 = seeThroughShapeMap.remove(identifier) != null;
            boolean removed2 = normalShapeMap.remove(identifier) != null;
            return removed1 || removed2;
        }
        public boolean removeShapes(@NotNull ResourceLocation identifier) {
            String namespace = identifier.getNamespace();
            String path = identifier.getPath();

            boolean removed1 = seeThroughShapeMap.entrySet()
                    .removeIf(entry -> entry.getKey().getNamespace().equals(namespace)
                            && entry.getKey().getPath().startsWith(path));

            boolean removed2 = normalShapeMap.entrySet()
                    .removeIf(entry -> entry.getKey().getNamespace().equals(namespace)
                            && entry.getKey().getPath().startsWith(path));

            return removed1 || removed2;
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
        public void drawImmediate(BuilderManager builderManager, PoseStack matrixStack,float tickDelta){
            if(!normalShapeMap.isEmpty()) {
                List<Shape> sortedShapes = new ArrayList<>(normalShapeMap.values());
                sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                for (Shape shape : sortedShapes) {
                    builderManager.drawImmediate(shape, builder -> {
                        shape.draw(true, builder, matrixStack,tickDelta);
                    });
                }
            }
            if(!seeThroughShapeMap.isEmpty()) {
                List<Shape> sortedShapes = new ArrayList<>(seeThroughShapeMap.values());
                sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                for (Shape shape : sortedShapes) {
                    builderManager.drawImmediate(shape, builder -> {
                        shape.draw(true, builder, matrixStack,tickDelta);
                    });
                }
            }
        }
        public void drawBatched(BuilderManager builderManager, PoseStack matrixStack,float tickDelta){
            if(!normalShapeMap.isEmpty()) {
                builderManager.drawBatch(builder -> {
                    List<Shape> sortedShapes = new ArrayList<>(normalShapeMap.values());
                    sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                    for (Shape shape : sortedShapes) {
                        shape.draw(true,builder, matrixStack,tickDelta);
                    }
                }, false);
            }
            if(!seeThroughShapeMap.isEmpty()) {
                builderManager.drawBatch(builder -> {
                    List<Shape> sortedShapes = new ArrayList<>(seeThroughShapeMap.values());
                    sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

                    for (Shape shape : sortedShapes) {
                        shape.draw(true,builder, matrixStack,tickDelta);
                    }
                }, true);
            }
        }
        public void drawBuffered(BuilderManager builderManager){
            builderManager.drawVBO();
        }
    }
}
