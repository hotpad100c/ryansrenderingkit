package mypals.ml.shape.minecraftBuiltIn;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.EmptyMesh;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BlockShape extends Shape implements EmptyMesh {

    public BlockState blockState;
    private final PoseStack poseStack = new PoseStack();
    public int light;
    public BlockShape(
                      Consumer<DefaultTransformer> transform,
                      Vec3 center, BlockState block,int light) {
        super(RenderingType.BATCH, transform, Color.white, center,false);
        this.seeThrough = false;
        this.blockState = block;
        this.light = light;
        this.transformer.setShapeWorldPivot(center);
        generateRawGeometry(false);
        syncLastToTarget();
    }
    @Override
    protected void generateRawGeometry(boolean lerp) {
        model_vertexes.clear();
        Minecraft mc = Minecraft.getInstance();
        List<Integer> indices = new ArrayList<>();
        if(mc.level == null) return;
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
        BakedModel bakedModel = dispatcher.getBlockModel(blockState);
        for(Direction direction : Direction.values()) {
            for (BakedQuad bakedQuad : bakedModel.getQuads(blockState,direction,mc.level.getRandom())){
                int base = model_vertexes.size();
                model_vertexes.addAll(decodeQuad(bakedQuad));
                indices.add(base);
                indices.add(base + 1);
                indices.add(base + 2);

                indices.add(base + 2);
                indices.add(base + 3);
                indices.add(base);
            }
        }

        indexBuffer = indices.stream().mapToInt(i -> i).toArray();

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

        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
        MultiBufferSource multiBufferSource = mc.renderBuffers().bufferSource();

        RenderSystem.setShaderColor((float) this.baseColor.getRed() /255,
                (float) this.baseColor.getGreen() /255,
                (float) this.baseColor.getBlue() /255,
                (float) this.baseColor.getAlpha() /255);
        poseStack.pushPose();
        poseStack.mulPose(builder.getPositionMatrix());



        dispatcher.renderSingleBlock(blockState,poseStack,multiBufferSource, light, OverlayTexture.NO_OVERLAY);

        if(blockState.getBlock() instanceof EntityBlock){
            BlockEntityRenderDispatcher blockEntityRenderDispatcher = mc.getBlockEntityRenderDispatcher();
            BlockEntity blockEntity = ((EntityBlock)blockState.getBlock()).newBlockEntity(BlockPos.ZERO,blockState);
            blockEntityRenderDispatcher.render(blockEntity,transformer.getTickDelta(),poseStack,multiBufferSource);
        }

        poseStack.popPose();
        RenderSystem.setShaderColor(1,1,1,1);
    }

    @Override
    public boolean shouldDraw() {
        return blockState.getRenderShape() != RenderShape.INVISIBLE && super.shouldDraw();
    }
}