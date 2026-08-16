package ml.mypals.ryansrenderingkit.shape.minecraftBuiltIn;

import com.mojang.blaze3d.vertex.PoseStack;
import ml.mypals.ryansrenderingkit.builders.vertexBuilders.VertexBuilder;
import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.basics.tags.EmptyMesh;
import ml.mypals.ryansrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.Minecraft;
//?if < 26.1{
/*import net.minecraft.client.renderer.entity.ItemRenderer;
*///?}
//? < 1.21.6 {
/*import com.mojang.blaze3d.systems.RenderSystem;
*///?}
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.item.ItemEntity;
//? if > 1.18.2 {
import net.minecraft.world.item.ItemDisplayContext;
//?} else {
/*import net.minecraft.client.renderer.block.model.ItemTransforms;
*///?}
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
//?if<=1.20.4{
/*import static ml.mypals.ryansrenderingkit.utils.Helpers.convertToMojangIfNeeded;
*///?}
//? if >=1.21.9 {
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
//?}
import java.awt.*;
import java.util.function.Consumer;

public class ItemShape extends Shape implements EmptyMesh {


    public int light;
    public ItemStack item;

    //? if >1.18.2 {
    public ItemDisplayContext itemDisplayContext = ItemDisplayContext.FIXED;
    //?} else {
    /*ItemTransforms.TransformType itemDisplayContext = ItemTransforms.TransformType.FIXED;
    *///?}
    private final PoseStack poseStack = new PoseStack();

    public ItemShape(
            Consumer<DefaultTransformer> transform,
            Vec3 center, ItemStack item,/*? if >1.18.2 {*/ ItemDisplayContext/*?} else {*//*ItemTransforms.TransformType*//*?}*/ itemDisplayContext, int light) {
        super(RenderingType.BATCH, transform, Color.white, center, false);
        this.seeThrough = false;
        this.item = item;
        this.transformer.setShapeWorldPivot(center);
        this.itemDisplayContext = itemDisplayContext;
        this.light = light;
        generateRawGeometry(false);
        syncLastToTarget();
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        float w = ItemEntity.DEFAULT_BB_HEIGHT / 32;
        float h = ItemEntity.DEFAULT_BB_HEIGHT / 32;

        modelVertexes.add(new Vec3(-w, -h, -w));
        modelVertexes.add(new Vec3(+w, -h, -w));
        modelVertexes.add(new Vec3(+w, +h, -w));
        modelVertexes.add(new Vec3(-w, +h, -w));
        modelVertexes.add(new Vec3(-w, -h, +w));
        modelVertexes.add(new Vec3(+w, -h, +w));
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
        poseStack.translate(0, -(ItemEntity.DEFAULT_BB_HEIGHT / 16), -0);

        //? if <1.21.9 {
        /*ItemRenderer itemRenderer = mc.getItemRenderer();
        itemRenderer.renderStatic(item, itemDisplayContext, light, OverlayTexture.NO_OVERLAY, poseStack,
                multiBufferSource, /^? if >1.18.2 {^/mc.level,/^?}^/ mc.level.random.nextInt());
        *///?} else {

        ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
        ItemModelResolver itemModelResolver = mc.getItemModelResolver();
        itemModelResolver.appendItemLayers(itemStackRenderState,item,itemDisplayContext,null,null,0);



        itemStackRenderState.submit(poseStack, new SubmitNodeStorage(),light,OverlayTexture.NO_OVERLAY, 0);
        //?}


        poseStack.popPose();
        //? <1.21.6 {
        /*RenderSystem.setShaderColor(1, 1, 1, 1);
        *///?}
    }

}