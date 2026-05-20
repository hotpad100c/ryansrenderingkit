package ml.mypals.ryansrenderingkit.builderManager;

import com.mojang.blaze3d.vertex.PoseStack;
import ml.mypals.ryansrenderingkit.builders.vertexBuilders.BatchVertexBuilder;
import ml.mypals.ryansrenderingkit.builders.vertexBuilders.BufferedVertexBuilder;
import ml.mypals.ryansrenderingkit.builders.vertexBuilders.ImmediateVertexBuilder;
import ml.mypals.ryansrenderingkit.builders.vertexBuilders.VertexBuilder;
import ml.mypals.ryansrenderingkit.render.RenderMethod;
import ml.mypals.ryansrenderingkit.shape.Shape;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

//? >= 26.2 {
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
//?}

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static ml.mypals.ryansrenderingkit.shapeManagers.ShapeManager.SHAPE_ORDER_COMPARATOR;

public class BuilderManager {
    public String id;
    public BuilderGroup seeThroughBuilderGroup;
    public BuilderGroup normalBuilderGroup;
    public RenderMethod renderMethod = null;
    //? >= 26.2 {
    private StagedVertexBuffer stagedBuffer;
    private SubmitNodeStorage submitNodeStorage;

    private void ensureSubmitStorage() {
        if (submitNodeStorage == null) {
            submitNodeStorage = new SubmitNodeStorage();
        }
    }
    //?}

    public BuilderManager(Matrix4f matrix4f, RenderMethod renderMethod, String id) {
        this.id = id;
        seeThroughBuilderGroup = new BuilderGroup(matrix4f, true, renderMethod);
        normalBuilderGroup = new BuilderGroup(matrix4f, false, renderMethod);
        this.renderMethod = renderMethod;
    }

    public static class BuilderGroup {
        public ImmediateVertexBuilder immediateShapeBuilder;
        public BatchVertexBuilder batchVertexBuilder;
        public BufferedVertexBuilder bufferedVertexBuilder;

        public BuilderGroup(Matrix4f matrix, boolean seeThrough, RenderMethod renderMethodForBufferedShapeBuilder) {
            this.immediateShapeBuilder = new ImmediateVertexBuilder(matrix, seeThrough);
            this.batchVertexBuilder = new BatchVertexBuilder(matrix, seeThrough);
            this.bufferedVertexBuilder = new BufferedVertexBuilder(matrix, seeThrough, renderMethodForBufferedShapeBuilder);
        }

        //? < 26.2 {
        /*public void drawBatch(Consumer<BatchVertexBuilder> builder, RenderMethod renderMethod) {
            batchVertexBuilder.draw(builder, renderMethod);
        }

        public void drawImmediate(Shape shape, Consumer<VertexBuilder> builder, RenderMethod renderMethod) {
            immediateShapeBuilder.draw(shape, builder, renderMethod);
        }*///?}

        public void drawVBO(Vec3 cameraPos) {
            bufferedVertexBuilder.draw(cameraPos);
        }

        public void updateMatrix(Matrix4f positionMatrix) {
            immediateShapeBuilder.setPositionMatrix(positionMatrix);
            batchVertexBuilder.setPositionMatrix(positionMatrix);
            bufferedVertexBuilder.setPositionMatrix(positionMatrix);
        }
    }

    public void drawBatch(PoseStack poseStack, Consumer<BatchVertexBuilder> builder, boolean seeThrough) {
        //? >= 26.2 {
        ensureSubmitStorage();
        BatchVertexBuilder bvb = seeThrough ? seeThroughBuilderGroup.batchVertexBuilder : normalBuilderGroup.batchVertexBuilder;
        bvb.submitCustom(poseStack, renderMethod, seeThrough, submitNodeStorage, builder);
        //?} else {
        /*if (seeThrough) seeThroughBuilderGroup.drawBatch(poseStack, builder, this.renderMethod);
        else normalBuilderGroup.drawBatch(poseStack, builder, this.renderMethod);
        *///?}
    }

    public void drawImmediate(Shape shape, Consumer<VertexBuilder> builder) {
        //? >= 26.2 {
        ensureSubmitStorage();
        ImmediateVertexBuilder ivb = shape.seeThrough ? seeThroughBuilderGroup.immediateShapeBuilder : normalBuilderGroup.immediateShapeBuilder;
        ivb.submitCustom(shape, renderMethod, submitNodeStorage, builder);
        //?} else {
        /*if (shape.seeThrough) seeThroughBuilderGroup.drawImmediate(shape, builder, renderMethod);
        else normalBuilderGroup.drawImmediate(shape, builder, renderMethod);
        *///?}
    }

    public void drawVBO() {
        Camera camera = net.minecraft.client.Minecraft.getInstance().gameRenderer.mainCamera();
        Vec3 cameraPos = camera/*? if >=1.21.11 {*/.position()/*?} else {*//*.getPosition()*//*?}*/;
        seeThroughBuilderGroup.drawVBO(cameraPos);
        normalBuilderGroup.drawVBO(cameraPos);
    }

    public void rebuildVBO(Collection<Shape> shapeList, boolean seeThrough) {
        if (seeThrough) seeThroughBuilderGroup.bufferedVertexBuilder.rebuild(renderMethod, builder -> {

            List<Shape> sortedShapes = new ArrayList<>(shapeList);
            sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

            for (Shape shape : sortedShapes) {
                shape.draw(false, builder, new PoseStack(), 1);
            }
        });
        else normalBuilderGroup.bufferedVertexBuilder.rebuild(renderMethod, builder -> {

            List<Shape> sortedShapes = new ArrayList<>(shapeList);
            sortedShapes.sort(SHAPE_ORDER_COMPARATOR);

            for (Shape shape : sortedShapes) {
                shape.draw(false, builder, new PoseStack(), 1);
            }
        });
    }

    //? >= 26.2 {
    public void flushDraws() {
        if (submitNodeStorage == null) return;

        if (stagedBuffer == null) {
            stagedBuffer = new StagedVertexBuffer(() -> "RyanRenderKit_" + id, 1536);
        }

        List<StagedVertexBuffer.Draw> draws = new ArrayList<>();
        List<RenderType> renderTypes = new ArrayList<>();

        submitNodeStorage.drainPhases(phase -> {
            phase.sortInto((submit, strictlyOrdered) -> {
                if (submit instanceof CustomFeatureRenderer.Submit(
                        PoseStack.Pose pose, RenderType rt,
                        net.minecraft.client.renderer.SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer
                )) {
                    StagedVertexBuffer.Draw draw = stagedBuffer.appendDraw(
                            rt.format(), rt.primitiveTopology(), null);
                    VertexConsumer vc = stagedBuffer.getVertexBuilder(draw);
                    customGeometryRenderer.render(pose, vc);
                    draws.add(draw);
                    renderTypes.add(rt);
                }
            });
        });

        if (!draws.isEmpty()) {
            stagedBuffer.upload();
            for (int i = 0; i < draws.size(); i++) {
                StagedVertexBuffer.ExecuteInfo info = stagedBuffer.getExecuteInfo(draws.get(i));
                if (info != null) {
                    renderTypes.get(i).prepare().drawFromBuffer(info);
                }
            }
            stagedBuffer.endDraw();
        }

        submitNodeStorage = null;
    }

    public void endFrame() {
        if (stagedBuffer != null) {
            stagedBuffer.endFrame();
        }
    }
    //?}

    public void updateMatrix(Matrix4f modelViewMatrix) {
        seeThroughBuilderGroup.updateMatrix(modelViewMatrix);
        normalBuilderGroup.updateMatrix(modelViewMatrix);
    }
}

