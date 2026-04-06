package ml.mypals.ryansrenderingkit.shape.minecraftBuiltIn;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import ml.mypals.ryansrenderingkit.builders.vertexBuilders.VertexBuilder;
import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.basics.tags.EmptyMesh;
import ml.mypals.ryansrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
//? <1.21.5 {
/*import net.minecraft.client.resources.model.BakedModel;
*///?} else {
//?}
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static ml.mypals.ryansrenderingkit.utils.Helpers.convertToMojangIfNeeded;


//? if >=1.21.9 {
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
//?}

public class BlockShape extends Shape implements EmptyMesh {

    public BlockState blockState;
    private final PoseStack poseStack = new PoseStack();
    public int light;

    public BlockShape(
            Consumer<DefaultTransformer> transform,
            Vec3 center, BlockState block, int light) {
        super(RenderingType.BATCH, transform, Color.white, center, false);
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
        if (mc.level == null) return;
        BlockStateModelSet dispatcher = mc.getModelManager().getBlockStateModelSet();
        //? <1.21.5 {
        /*BakedModel bakedModel = dispatcher.getBlockModel(blockState);

        for (Direction direction : Direction.values()) {
            for (BakedQuad bakedQuad : bakedModel.getQuads(blockState, direction, mc.level.getRandom())) {
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
        *///?} else {
        BlockStateModel bakedModel = dispatcher.get(blockState);
        List<BlockStateModelPart> parts = new ObjectArrayList<>();
        bakedModel.collectParts(mc.level.getRandom(), parts);
        for (BlockStateModelPart bakedQuads : parts) {
            for(Direction direction : Direction.values()) {
                for(BakedQuad bakedQuad : bakedQuads.getQuads(direction)) {
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
        }
        //?}

        indexBuffer = indices.stream().mapToInt(i -> i).toArray();

    }

    public static List<Vec3> decodeQuad(BakedQuad quad) {
        List<Vec3> result = new ArrayList<>(4);
        //? if <1.21.11 {

            /*//? if >=1.21.5 {
            int[] v = quad.vertices();
            //?} else {
            /^int[] v = quad.getVertices();
            ^///?}
            int stride = 8;
            int count = v.length / stride;
            for (int i = 0; i < count; i++) {
                int base = i * stride;
                float x = Float.intBitsToFloat(v[base]);
                float y = Float.intBitsToFloat(v[base + 1]);
                float z = Float.intBitsToFloat(v[base + 2]);
                result.add(new Vec3(x, y, z));
            }
        *///?} else {
        for (int i = 0;i<4;i++){
            float x = quad.position(i).x();
            float y = quad.position(i).y();
            float z = quad.position(i).z();
            result.add(new Vec3(x, y, z));
        }
        //?}
        return result;
    }

    @Override
    protected void drawInternal(VertexBuilder builder) {
        Minecraft mc = Minecraft.getInstance();

        boolean ambientOcclusion = mc.options.ambientOcclusion().get();
        ModelBlockRenderer blockRenderer = new ModelBlockRenderer(ambientOcclusion, false, mc.getBlockColors());
        MultiBufferSource multiBufferSource = mc.renderBuffers().bufferSource();

        //? < 1.21.6 {
        /*RenderSystem.setShaderColor((float) this.baseColor.getRed() / 255,
                (float) this.baseColor.getGreen() / 255,
                (float) this.baseColor.getBlue() / 255,
                (float) this.baseColor.getAlpha() / 255);
        *///?}
        poseStack.pushPose();
        //? > 1.20.4 {
        poseStack.mulPose(builder.getPositionMatrix());
        //?} else
        //poseStack.mulPoseMatrix(convertToMojangIfNeeded(builder.getPositionMatrix()));


        BlockStateModel model = mc.getModelManager().getBlockStateModelSet().get(blockState);
        List<BlockStateModelPart> parts = new ObjectArrayList<>();
        model.collectParts(mc.level.getRandom(), parts);
        SubmitNodeCollector submitNodeCollector = mc.gameRenderer.getSubmitNodeStorage();
        submitNodeCollector.submitBlockModel(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), parts, new int[0], light, OverlayTexture.NO_OVERLAY, 0);

        if (blockState.getBlock() instanceof EntityBlock) {
            BlockEntityRenderDispatcher blockEntityRenderDispatcher = mc.getBlockEntityRenderDispatcher();
            BlockEntity blockEntity = ((EntityBlock) blockState.getBlock()).newBlockEntity(BlockPos.ZERO, blockState);

            //? if <1.21.9 {
            /*blockEntityRenderDispatcher.render(blockEntity, transformer.getTickDelta(), poseStack, multiBufferSource);
             *///?} else {
            BlockEntityRenderState blockEntityRenderState = blockEntityRenderDispatcher.tryExtractRenderState(blockEntity, transformer.getTickDelta(),null);
            net.minecraft.client.renderer.state.level.CameraRenderState cameraRenderState = mc.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState;
            blockEntityRenderDispatcher.submit(blockEntityRenderState,poseStack,submitNodeCollector,cameraRenderState);
            //?}
        }

        poseStack.popPose();

        //? < 1.21.6 {
        /*RenderSystem.setShaderColor(1, 1, 1, 1);
        *///?}
    }

    @Override
    public boolean enabled() {
        return blockState.getRenderShape() != RenderShape.INVISIBLE && super.enabled();
    }
}