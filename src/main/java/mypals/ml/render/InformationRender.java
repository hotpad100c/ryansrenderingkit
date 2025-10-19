package mypals.ml.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builderManager.BuilderManagers;
import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shape.BoxShape;
import mypals.ml.shape.Shape;
import mypals.ml.shapeManagers.ShapeManagers;
import mypals.ml.test.Tester;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class InformationRender {
    public static void render(MatrixStack matrixStack, Camera camera){
        if(MinecraftClient.getInstance().player == null || !camera.isReady()) return;
        try {
            matrixStack.push();
            matrixStack.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);

            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            RenderSystem.enablePolygonOffset();
            RenderSystem.polygonOffset(-1.0f, -1.0f);

            Matrix4f pose = matrixStack.peek().getPositionMatrix();
            BuilderManagers.updateMatrix(pose);

            Tester.renderTick(matrixStack);

            ShapeManagers.renderAll(matrixStack);

            RenderSystem.disablePolygonOffset();
            RenderSystem.enableCull();

            matrixStack.pop();

        }catch (Exception e){
            throw e;
        }
    }

}
