package mypals.ml.builderManager;

import mypals.ml.builders.BatchShapeBuilder;
import mypals.ml.builders.BufferedShapeBuilder;
import mypals.ml.builders.ImmediateShapeBuilder;
import mypals.ml.builders.ShapeBuilder;
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.*;
import java.util.function.Consumer;

import static mypals.ml.shapeManagers.ShapeManager.SHAPE_ORDER_COMPARATOR;

public class BuilderManager {
    public String id;
    public BuilderGroup seeThroughBuilderGroup;
    public BuilderGroup normalBuilderGroup;
    public RenderMethod renderMethod = null;
    public BuilderManager(Matrix4f matrix4f,RenderMethod renderMethod,String id){
        this.id = id;
        seeThroughBuilderGroup = new BuilderGroup(matrix4f,true, renderMethod);
        normalBuilderGroup = new BuilderGroup(matrix4f,false, renderMethod);
        this.renderMethod = renderMethod;
    }
    public static class BuilderGroup{
        public ImmediateShapeBuilder immediateShapeBuilder;
        public BatchShapeBuilder batchShapeBuilder;
        public BufferedShapeBuilder bufferedShapeBuilder;
        public BuilderGroup(Matrix4f matrix,boolean seeThrough,RenderMethod renderMethodForBufferedShapeBuilder){
            this.immediateShapeBuilder = new ImmediateShapeBuilder(matrix,seeThrough);
            this.batchShapeBuilder = new BatchShapeBuilder(matrix,seeThrough);
            this.bufferedShapeBuilder = new BufferedShapeBuilder(matrix,seeThrough, renderMethodForBufferedShapeBuilder);
        }
        public void drawBatch(Consumer<BatchShapeBuilder> builder,RenderMethod renderMethod){
            batchShapeBuilder.beginBatch(renderMethod);
            builder.accept(batchShapeBuilder);
            batchShapeBuilder.drawBatch();
        }
        public void drawImmediate(Shape shape, Consumer<ShapeBuilder> builder,RenderMethod renderMethod){
            immediateShapeBuilder.draw(shape,builder,renderMethod);
        }
        public void drawVBO(Vec3d cameraPos){
            bufferedShapeBuilder.draw(cameraPos);
        }
        public void updateMatrix(Matrix4f positionMatrix){
            immediateShapeBuilder.setPositionMatrix(positionMatrix);
            batchShapeBuilder.setPositionMatrix(positionMatrix);
            bufferedShapeBuilder.setPositionMatrix(positionMatrix);
        }
    }
    public void drawBatch(Consumer<BatchShapeBuilder> builder,boolean seeThrough){
        if(seeThrough)seeThroughBuilderGroup.drawBatch(builder,this.renderMethod);
        else normalBuilderGroup.drawBatch(builder,this.renderMethod);
    }
    public void drawImmediate(Shape shape, Consumer<ShapeBuilder> builder){
        if(shape.seeThrough)seeThroughBuilderGroup.drawImmediate(shape,builder,renderMethod);
        else normalBuilderGroup.drawImmediate(shape,builder,renderMethod);
    }
    public void drawVBO(){
        Camera camera = net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        seeThroughBuilderGroup.drawVBO(cameraPos);
        normalBuilderGroup.drawVBO(cameraPos);
    }
    public void rebuildVBO(Collection<Shape> shapeList, boolean seeThrough){
        if(seeThrough)seeThroughBuilderGroup.bufferedShapeBuilder.rebuild(renderMethod,builder->{

            List<Shape> sortedShapes = new ArrayList<>(shapeList);
            sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

            for(Shape shape: sortedShapes){
                shape.draw(builder);
            }
        });
        else normalBuilderGroup.bufferedShapeBuilder.rebuild(renderMethod,builder->{

            List<Shape> sortedShapes = new ArrayList<>(shapeList);
            sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

            for(Shape shape: sortedShapes){
                shape.draw(builder);
            }
        });
    }
    public void updateMatrix(Matrix4f modelViewMatrix){
        seeThroughBuilderGroup.updateMatrix(modelViewMatrix);
        normalBuilderGroup.updateMatrix(modelViewMatrix);
    }
}

