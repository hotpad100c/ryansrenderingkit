package mypals.ml.shape.minecraftBuiltIn;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.EmptyMesh;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemShape extends Shape implements EmptyMesh {


    public int light;
    public ItemStack item;
    public ItemDisplayContext itemDisplayContext = ItemDisplayContext.FIXED;
    private final PoseStack poseStack = new PoseStack();
    public ItemShape(
            Consumer<DefaultTransformer> transform,
            Vec3 center, ItemStack item,ItemDisplayContext itemDisplayContext,int light) {
        super(RenderingType.BATCH, transform, Color.white, center,false);
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
        float w = ItemEntity.DEFAULT_BB_HEIGHT/32;
        float h = ItemEntity.DEFAULT_BB_HEIGHT/32;

        model_vertexes.add(new Vec3(- w, - h, - w));
        model_vertexes.add(new Vec3(+ w, - h, - w));
        model_vertexes.add(new Vec3(+ w, + h, - w));
        model_vertexes.add(new Vec3(- w, + h, - w));
        model_vertexes.add(new Vec3(- w, - h, + w));
        model_vertexes.add(new Vec3(+ w, - h, + w));
        model_vertexes.add(new Vec3(+ w, + h, + w));
        model_vertexes.add(new Vec3(- w, + h, + w));

        indexBuffer = new int[] {
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

        ItemRenderer itemRenderer = mc.getItemRenderer();
        MultiBufferSource multiBufferSource = mc.renderBuffers().bufferSource();

        RenderSystem.setShaderColor((float) this.baseColor.getRed() /255,
                (float) this.baseColor.getGreen() /255,
                (float) this.baseColor.getBlue() /255,
                (float) this.baseColor.getAlpha() /255);
        poseStack.pushPose();
        poseStack.mulPose(builder.getPositionMatrix());
        poseStack.translate(0,-(ItemEntity.DEFAULT_BB_HEIGHT/16),-0);
        itemRenderer.renderStatic(item,itemDisplayContext,light,OverlayTexture.NO_OVERLAY,poseStack,multiBufferSource,mc.level,mc.level.random.nextInt());
        poseStack.popPose();
        RenderSystem.setShaderColor(1,1,1,1);
    }

}