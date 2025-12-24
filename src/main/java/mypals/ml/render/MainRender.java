package mypals.ml.render;

//? >= 1.21.5 {
/*import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.pipeline.RenderPipeline;
*///?} else {
import com.mojang.blaze3d.platform.GlStateManager;
//?}
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import mypals.ml.builderManager.BuilderManagers;
import mypals.ml.shapeManagers.ShapeManagers;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

import static mypals.ml.RyansRenderingKit.RENDER_PROFILER;

public class MainRender {
    //? >= 1.21.5 {
    /*public static final RenderType.CompositeRenderType TRIANGLE;
    public static final RenderPipeline TRIANGLE_PIPLINE;
    static{

        TRIANGLE_PIPLINE = RenderPipelines
                .register(RenderPipeline.builder(
                        new RenderPipeline.Snippet[]{RenderPipelines.DEBUG_FILLED_SNIPPET})
                        .withLocation("pipeline/debug_triangle")
                        .withCull(false).withVertexFormat(DefaultVertexFormat.POSITION_COLOR,
                                VertexFormat.Mode.TRIANGLES)
                        .build());
        TRIANGLE = RenderType.create("r_triangle",
                1536,
                false,
                true,
                TRIANGLE_PIPLINE, RenderType.CompositeState.builder().createCompositeState(false));

    }
    *///?}

    public static void render(PoseStack matrixStack, Camera camera, float tickDelta) {
        RENDER_PROFILER.reset();

        RENDER_PROFILER.push("renderAll");

        if (Minecraft.getInstance().player == null || !camera.isInitialized()) return;
        try {

            matrixStack.pushPose();

            matrixStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
            Matrix4f pose = matrixStack.last().pose();

            RENDER_PROFILER.push("updateMatrix");
            BuilderManagers.updateMatrix(pose);
            RENDER_PROFILER.pop();

            RENDER_PROFILER.push("renderShapes");

            //? >=1.21.5 {
            /*GlStateManager._enableBlend();
            /^GlStateManager._blendFuncSeparate(
                    SourceFactor.SRC_ALPHA.ordinal(),
                    DestFactor.ONE_MINUS_SRC_ALPHA.ordinal(),
                    SourceFactor.ONE.ordinal(),
                    DestFactor.ZERO.ordinal());^/
            *///?} else {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            //?}
            ShapeManagers.renderAll(matrixStack, tickDelta);
            //? >=1.21.5 {
            /*GlStateManager._disableBlend();
            *///?} else {
            RenderSystem.disableBlend();
            //?}


            RENDER_PROFILER.pop();

            matrixStack.popPose();


        } catch (Exception e) {
            System.out.println("Error during InformationRender.render:");
            System.err.println(e.getMessage());
        }
        RENDER_PROFILER.pop();
    }

}
