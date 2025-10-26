package mypals.ml.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builderManager.BuilderManagers;
import mypals.ml.shapeManagers.ShapeManagers;
import mypals.ml.test.Tester;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class InformationRender {
    public static void render(MatrixStack matrixStack, Camera camera, float tickDelta) {
        if(MinecraftClient.getInstance().player == null || !camera.isReady()) return;
        try {
            matrixStack.push();
            matrixStack.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);

            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();

            Matrix4f pose = matrixStack.peek().getPositionMatrix();
            BuilderManagers.updateMatrix(pose);

            Tester.renderTick(matrixStack);//------

            ShapeManagers.renderAll(matrixStack, tickDelta);

            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();

            matrixStack.pop();

        }catch (Exception e){
            System.out.println("Error during InformationRender.render:");
            System.err.println(e.getMessage());
        }
    }

}
