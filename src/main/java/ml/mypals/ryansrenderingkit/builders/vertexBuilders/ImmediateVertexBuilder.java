package ml.mypals.ryansrenderingkit.builders.vertexBuilders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
//? if > 1.20.6 {
import ml.mypals.ryansrenderingkit.interfaces.MeshDataExt;
//?}
import ml.mypals.ryansrenderingkit.render.RenderMethod;
import ml.mypals.ryansrenderingkit.shape.Shape;
import org.joml.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

//? >= 26.2 {
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
//?}

import java.util.function.Consumer;

public class ImmediateVertexBuilder extends VertexBuilder {

    public ImmediateVertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix, seeThrough);
    }

    //? >= 26.2 {
    public void submitCustom(Shape shape, RenderMethod renderMethod,
                             SubmitNodeStorage storage, Consumer<VertexBuilder> builder) {
        RenderType renderType = shape.seeThrough ? renderMethod.seeThroughType() : renderMethod.normalRenderType();
        PoseStack dummyStack = new PoseStack();
        storage.submitCustomGeometry(dummyStack, renderType, (pose, vertexConsumer) -> {
            setVertexConsumer(vertexConsumer);
            builder.accept(this);
            setVertexConsumer(null);
        });
    }
    //?}
}