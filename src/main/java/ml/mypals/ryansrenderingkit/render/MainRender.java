package ml.mypals.ryansrenderingkit.render;

//? >= 1.21.5 && < 26.2{
import com.mojang.blaze3d.opengl.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.pipeline.RenderPipeline;
//?} else if < 26.2{
/*import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
*///?}
import com.mojang.blaze3d.vertex.PoseStack;
import ml.mypals.ryansrenderingkit.shapeManagers.ShapeManagers;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
//? if >=1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderSetup;
//?}

import static ml.mypals.ryansrenderingkit.RyansRenderingKit.RENDER_PROFILER;

public class MainRender {

    public static void render(PoseStack matrixStack, Camera camera, float tickDelta) {
        RENDER_PROFILER.reset();

        RENDER_PROFILER.push("ryansRenderingKit");

        if (Minecraft.getInstance().player == null || !camera.isInitialized()) return;
        try {

            matrixStack.pushPose();

            RENDER_PROFILER.push("renderShapes");

            //? if < 26.1{
                //?if >=1.21.5{
                GlStateManager._enableBlend();
                GlStateManager._blendFuncSeparate(
                        GL11.GL_SRC_ALPHA,
                        GL11.GL_ONE_MINUS_SRC_ALPHA,
                        GL11.GL_ONE,
                        GL11.GL_ZERO
                );
                //?} else {
                /*RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                *///?}
            //?}

            //?if<=1.20.1{
            /*ShapeManagers.renderAll(matrixStack, tickDelta);
            *///?}else{
            ShapeManagers.renderAll(matrixStack, tickDelta);
            //?}

            //?if < 26.1{
                //?if >=1.21.5{
                GlStateManager._disableBlend();
                //?} else {
                /*RenderSystem.disableBlend();
                *///?}
            //?}


            RENDER_PROFILER.pop();

            matrixStack.popPose();


        } catch (Exception e) {
            System.out.println("Error during InformationRender.render:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        RENDER_PROFILER.pop();
    }

}
