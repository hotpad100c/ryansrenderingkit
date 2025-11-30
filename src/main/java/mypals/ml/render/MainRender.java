package mypals.ml.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builderManager.BuilderManagers;
import mypals.ml.shapeManagers.ShapeManagers;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;

import static mypals.ml.RyansRenderingKit.RENDER_PROFILER;

public class MainRender {
    public static void render(PoseStack matrixStack, Camera camera, float tickDelta) {
        RENDER_PROFILER.reset();

        RENDER_PROFILER.push("renderAll");

        if(Minecraft.getInstance().player == null || !camera.isInitialized()) return;
        try {

            matrixStack.pushPose();

            matrixStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
            Matrix4f pose = matrixStack.last().pose();

            RENDER_PROFILER.push("updateMatrix");
            BuilderManagers.updateMatrix(pose);
            RENDER_PROFILER.pop();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            RENDER_PROFILER.push("renderShapes");
            ShapeManagers.renderAll(matrixStack, tickDelta);
            RENDER_PROFILER.pop();
            RenderSystem.disableBlend();

            matrixStack.popPose();


        }catch (Exception e){
            System.out.println("Error during InformationRender.render:");
            System.err.println(e.getMessage());
        }
        RENDER_PROFILER.pop();
    }

}
