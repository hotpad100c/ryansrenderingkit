package mypals.ml.shape.minecraftBuiltIn;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.EmptyMesh;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EntityShape extends Shape implements EmptyMesh {

    public Entity entity;
    private final PoseStack poseStack = new PoseStack();
    public int light;

    public EntityShape(
            Consumer<DefaultTransformer> transform,
            Vec3 center, Entity entity, int light) {
        super(RenderingType.BATCH, transform, Color.white, center, false);
        this.seeThrough = false;
        this.entity = entity;
        this.light = light;
        this.transformer.setShapeWorldPivot(center);
        generateRawGeometry(false);
        syncLastToTarget();
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        float w = entity.getBbWidth() / 2;
        float h = entity.getBbHeight();

        model_vertexes.add(new Vec3(-w, 0, -w));
        model_vertexes.add(new Vec3(+w, 0, -w));
        model_vertexes.add(new Vec3(+w, +h, -w));
        model_vertexes.add(new Vec3(-w, +h, -w));
        model_vertexes.add(new Vec3(-w, 0, +w));
        model_vertexes.add(new Vec3(+w, 0, +w));
        model_vertexes.add(new Vec3(+w, +h, +w));
        model_vertexes.add(new Vec3(-w, +h, +w));

        indexBuffer = new int[]{
                0, 1, 2, 2, 3, 0,
                4, 6, 5, 6, 4, 7,
                0, 4, 1, 1, 4, 5,
                3, 2, 6, 6, 7, 3,
                0, 3, 4, 4, 3, 7,
                1, 5, 2, 2, 5, 6
        };

    }

    public static List<Vec3> decodeQuad(BakedQuad quad) {
        int[] v = quad.getVertices();
        int stride = 8;
        int count = v.length / stride;
        List<Vec3> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int base = i * stride;
            float x = Float.intBitsToFloat(v[base]);
            float y = Float.intBitsToFloat(v[base + 1]);
            float z = Float.intBitsToFloat(v[base + 2]);
            result.add(new Vec3(x, y, z));
        }
        return result;
    }

    @Override
    protected void drawInternal(VertexBuilder builder) {
        Minecraft mc = Minecraft.getInstance();

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        MultiBufferSource multiBufferSource = mc.renderBuffers().bufferSource();

        RenderSystem.setShaderColor((float) this.baseColor.getRed() / 255,
                (float) this.baseColor.getGreen() / 255,
                (float) this.baseColor.getBlue() / 255,
                (float) this.baseColor.getAlpha() / 255);
        poseStack.pushPose();
        poseStack.mulPose(builder.getPositionMatrix());
        dispatcher.render(entity, 0, 0, 0, transformer.getTickDelta(),
                poseStack, multiBufferSource, light);

        poseStack.popPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    @Override
    public boolean shouldDraw() {
        return super.shouldDraw();
    }
}