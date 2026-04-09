package ml.mypals.ryansrenderingkit.shape.minecraftBuiltIn;

import com.mojang.blaze3d.vertex.PoseStack;
import ml.mypals.ryansrenderingkit.builders.vertexBuilders.VertexBuilder;
import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.basics.tags.EmptyMesh;
import ml.mypals.ryansrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
//? < 1.21.6 {
/*import com.mojang.blaze3d.systems.RenderSystem;
*///?}
import java.awt.*;
import java.util.function.Consumer;
//? if >=1.21.9 {
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
//?if < 26.1{
import net.minecraft.client.renderer.state.CameraRenderState;
//?}else{
/*import net.minecraft.client.renderer.state.level.CameraRenderState;
*///?}
//?}
//?if<=1.20.4{
/*import static ml.mypals.ryansrenderingkit.utils.Helpers.convertToMojangIfNeeded;
*///?}
import static ml.mypals.ryansrenderingkit.RyansRenderingKit.isEndOfWorldTick;

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

        modelVertexes.add(new Vec3(-w, 0, -w));
        modelVertexes.add(new Vec3(+w, 0, -w));
        modelVertexes.add(new Vec3(+w, +h, -w));
        modelVertexes.add(new Vec3(-w, +h, -w));
        modelVertexes.add(new Vec3(-w, 0, +w));
        modelVertexes.add(new Vec3(+w, 0, +w));
        modelVertexes.add(new Vec3(+w, +h, +w));
        modelVertexes.add(new Vec3(-w, +h, +w));

        indexBuffer = new int[]{
                0, 1, 2, 2, 3, 0,
                4, 6, 5, 6, 4, 7,
                0, 4, 1, 1, 4, 5,
                3, 2, 6, 6, 7, 3,
                0, 3, 4, 4, 3, 7,
                1, 5, 2, 2, 5, 6
        };

    }

    @Override
    protected void drawInternal(VertexBuilder builder) {
        Minecraft mc = Minecraft.getInstance();

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        MultiBufferSource multiBufferSource = mc.renderBuffers().bufferSource();
        if(isEndOfWorldTick() && entity != mc.player){
            entity.tick();
        }
        //? <1.21.6 {
        /*RenderSystem.setShaderColor((float) this.baseColor.getRed() / 255,
                (float) this.baseColor.getGreen() / 255,
                (float) this.baseColor.getBlue() / 255,
                (float) this.baseColor.getAlpha() / 255);
        *///?}
        poseStack.pushPose();
        //? > 1.20.4 {
        poseStack.mulPose(builder.getPositionMatrix());
         //?} else {
        /*poseStack.mulPoseMatrix(convertToMojangIfNeeded(builder.getPositionMatrix()));
        *///?}
        //? if <1.21.9 {
        /*dispatcher.render(entity, 0, 0, 0,
                //? <= 1.20.4 {
                /^entity.getPose().ordinal(),
                ^///?} else if <=1.21.1 {
                /^entity.getPose().id(),
                ^///?}
                transformer.getTickDelta(),
                poseStack, multiBufferSource, light);
        *///?} else {
        EntityRenderState entityRenderState = dispatcher.extractEntity(entity, transformer.getTickDelta());
        SubmitNodeCollector submitNodeCollector = mc.gameRenderer.getSubmitNodeStorage();
        //?if<26.1{
        CameraRenderState cameraRenderState = mc.gameRenderer.getLevelRenderState().cameraRenderState;
        //?}else{
        /*CameraRenderState cameraRenderState = mc.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState;
        *///?}
        dispatcher.submit(entityRenderState,cameraRenderState,0,0,0,poseStack,submitNodeCollector);
        //?}
        poseStack.popPose();
        //? <1.21.6 {
        /*RenderSystem.setShaderColor(1, 1, 1, 1);
         *///?}
    }

    @Override
    public boolean enabled() {
        return super.enabled();
    }
}