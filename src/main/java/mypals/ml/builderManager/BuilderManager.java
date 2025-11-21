package mypals.ml.builderManager;

import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builders.vertexBuilders.BatchVertexBuilder;
import mypals.ml.builders.vertexBuilders.BufferedVertexBuilder;
import mypals.ml.builders.vertexBuilders.ImmediateVertexBuilder;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
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
        public ImmediateVertexBuilder immediateShapeBuilder;
        public BatchVertexBuilder batchVertexBuilder;
        public BufferedVertexBuilder bufferedVertexBuilder;
        public BuilderGroup(Matrix4f matrix,boolean seeThrough,RenderMethod renderMethodForBufferedShapeBuilder){
            this.immediateShapeBuilder = new ImmediateVertexBuilder(matrix,seeThrough);
            this.batchVertexBuilder = new BatchVertexBuilder(matrix,seeThrough);
            this.bufferedVertexBuilder = new BufferedVertexBuilder(matrix,seeThrough, renderMethodForBufferedShapeBuilder);
        }
        public void drawBatch(Consumer<BatchVertexBuilder> builder, RenderMethod renderMethod){
            batchVertexBuilder.beginBatch(renderMethod);
            builder.accept(batchVertexBuilder);
            batchVertexBuilder.drawBatch(renderMethod);
        }
        public void drawImmediate(Shape shape, Consumer<VertexBuilder> builder, RenderMethod renderMethod){
            immediateShapeBuilder.draw(shape,builder,renderMethod);
        }
        public void drawVBO(Vec3 cameraPos){
            bufferedVertexBuilder.draw(cameraPos);
        }
        public void updateMatrix(Matrix4f positionMatrix){
            immediateShapeBuilder.setPositionMatrix(positionMatrix);
            batchVertexBuilder.setPositionMatrix(positionMatrix);
            bufferedVertexBuilder.setPositionMatrix(positionMatrix);
        }
    }
    public void drawBatch(Consumer<BatchVertexBuilder> builder, boolean seeThrough){
        if(seeThrough)seeThroughBuilderGroup.drawBatch(builder,this.renderMethod);
        else normalBuilderGroup.drawBatch(builder,this.renderMethod);
    }
    public void drawImmediate(Shape shape, Consumer<VertexBuilder> builder){
        if(shape.seeThrough)seeThroughBuilderGroup.drawImmediate(shape,builder,renderMethod);
        else normalBuilderGroup.drawImmediate(shape,builder,renderMethod);
    }
    public void drawVBO(){
        Camera camera = net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        seeThroughBuilderGroup.drawVBO(cameraPos);
        normalBuilderGroup.drawVBO(cameraPos);
    }
    public void rebuildVBO(Collection<Shape> shapeList, boolean seeThrough){
        if(seeThrough)seeThroughBuilderGroup.bufferedVertexBuilder.rebuild(renderMethod, builder->{

            List<Shape> sortedShapes = new ArrayList<>(shapeList);
            sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

            for(Shape shape: sortedShapes){
                shape.draw(false, builder,new PoseStack(),1);
            }
        });
        else normalBuilderGroup.bufferedVertexBuilder.rebuild(renderMethod, builder->{

            List<Shape> sortedShapes = new ArrayList<>(shapeList);
            sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

            for(Shape shape: sortedShapes){
                shape.draw(false, builder,new PoseStack(),1);
            }
        });
    }
    public void updateMatrix(Matrix4f modelViewMatrix){
        seeThroughBuilderGroup.updateMatrix(modelViewMatrix);
        normalBuilderGroup.updateMatrix(modelViewMatrix);
    }
}

