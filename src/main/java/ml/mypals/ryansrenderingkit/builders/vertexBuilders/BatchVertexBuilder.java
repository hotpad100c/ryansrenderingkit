package ml.mypals.ryansrenderingkit.builders.vertexBuilders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
//? if >1.20.6 {
import ml.mypals.ryansrenderingkit.interfaces.MeshDataExt;
//?}
import ml.mypals.ryansrenderingkit.render.RenderMethod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;

//? >= 26.2 {
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
//?}

import java.util.function.Consumer;

public class BatchVertexBuilder extends VertexBuilder {
    public BatchVertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix, seeThrough);
    }

    //? >= 26.2 {
    public void submitCustom(PoseStack poseStack, RenderMethod renderMethod, boolean seeThrough,
                             SubmitNodeStorage storage, Consumer<BatchVertexBuilder> builder) {
        RenderType renderType = seeThrough ? renderMethod.seeThroughType() : renderMethod.normalRenderType();
        storage.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) -> {
            setVertexConsumer(vertexConsumer);
            builder.accept(this);
            setVertexConsumer(null);
        });
    }
    //?}
}