package ml.mypals.ryansrenderingkit.backend;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ml.mypals.ryansrenderingkit.builders.vertexBuilders.VertexBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;

import java.util.function.Consumer;

/**
 * Vertex builder backed by the engine's own batcher.
 * <p>
 * Owns no buffer: {@code submitCustomGeometry} hands us a {@link VertexConsumer} when the
 * {@code FeatureRenderDispatcher} actually draws, and we write straight into it. Batching,
 * translucency ordering and pipeline state are the engine's problem, which is why there is no
 * counterpart to {@code setUpRendererSystem}/{@code drawBatch} here.
 * <p>
 * Note the geometry callback runs at <em>draw</em> time, not at submit time, so anything it reads
 * off a shape is read one step later than the legacy backend read it.
 */
public final class SubmitVertexBuilder extends VertexBuilder {

    private VertexConsumer consumer;

    public SubmitVertexBuilder(Matrix4f positionMatrix, boolean seeThrough) {
        super(positionMatrix, seeThrough);
    }

    @Override
    protected VertexConsumer target() {
        return consumer;
    }

    /**
     * Queues {@code geometry} for drawing under {@code renderType}. {@code order} maps onto the
     * existing shape ordering -- the engine sorts submissions by it.
     */
    public void submit(SubmitNodeCollector collector,
                       PoseStack poseStack,
                       RenderType renderType,
                       int order,
                       Consumer<VertexBuilder> geometry) {

        collector.order(order).submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) -> {
            Matrix4f previous = getPositionMatrix();
            this.consumer = vertexConsumer;
            setPositionMatrix(new Matrix4f(pose.pose()));
            try {
                geometry.accept(this);
            } finally {
                this.consumer = null;
                setPositionMatrix(previous);
            }
        });
    }
}
