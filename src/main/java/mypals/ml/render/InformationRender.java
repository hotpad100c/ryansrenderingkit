package mypals.ml.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builderManager.BuilderManagers;
import mypals.ml.shapeManagers.ShapeManagers;
import mypals.ml.test.Tester;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;

public class InformationRender {
    public static void render(PoseStack matrixStack, Camera camera, float tickDelta) {
        if(Minecraft.getInstance().player == null || !camera.isInitialized()) return;
        try {
            matrixStack.pushPose();
            matrixStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            Matrix4f pose = matrixStack.last().pose();
            BuilderManagers.updateMatrix(pose);

            Tester.renderTick(matrixStack);

            ShapeManagers.renderAll(matrixStack, tickDelta);

            RenderSystem.disableBlend();

            matrixStack.popPose();

        }catch (Exception e){
            System.out.println("Error during InformationRender.render:");
            System.err.println(e.getMessage());
            throw e;
        }
    }

}
