package ml.mypals.ryansrenderingkit.render;

//? >= 1.21.5 {
import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.pipeline.RenderPipeline;
//?} else {
/*import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
*///?}
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import ml.mypals.ryansrenderingkit.builderManager.BuilderManagers;
import ml.mypals.ryansrenderingkit.shapeManagers.ShapeManagers;
import ml.mypals.ryansrenderingkit.utils.Helpers;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
//? if >=1.21.11 {
/*import net.minecraft.client.renderer.rendertype.RenderSetup;
*///?}

import static ml.mypals.ryansrenderingkit.RyansRenderingKit.RENDER_PROFILER;

public class MainRender {

    //? >= 1.21.5 {
    public static final RenderType/*? if <1.21.11 {*/.CompositeRenderType/*?}*/ TRIANGLE;
    public static final RenderPipeline TRIANGLE_PIPLINE;
    static{

        TRIANGLE_PIPLINE = RenderPipelines
                .register(RenderPipeline.builder(
                        new RenderPipeline.Snippet[]{RenderPipelines.DEBUG_FILLED_SNIPPET})
                        .withLocation("pipeline/debug_triangle")
                        .withCull(false).withVertexFormat(DefaultVertexFormat.POSITION_COLOR,
                                VertexFormat.Mode.TRIANGLES)
                        .build());
        //? if >=1.21.11 {
        /*TRIANGLE = RenderType.create("r_triangle",
                RenderSetup.builder(TRIANGLE_PIPLINE).createRenderSetup());
        *///?} else {
        TRIANGLE = RenderType.create("r_triangle",
                1536,
                false,
                true,
                TRIANGLE_PIPLINE, RenderType.CompositeState.builder().createCompositeState(false));
        //?}
    }
    //?}

    public static void render(PoseStack matrixStack, Camera camera, float tickDelta) {
        RENDER_PROFILER.reset();

        RENDER_PROFILER.push("renderAll");

        if (Minecraft.getInstance().player == null || !camera.isInitialized()) return;
        try {

            matrixStack.pushPose();

            Matrix4f pose = Helpers.convertToJomlIfNeeded(matrixStack.last().pose());

            RENDER_PROFILER.push("updateMatrix");
            //BuilderManagers.updateMatrix(pose);
            RENDER_PROFILER.pop();

            RENDER_PROFILER.push("renderShapes");

            //? >=1.21.5 {
            GlStateManager._enableBlend();
            /*GlStateManager._blendFuncSeparate(
                    SourceFactor.SRC_ALPHA.ordinal(),
                    DestFactor.ONE_MINUS_SRC_ALPHA.ordinal(),
                    SourceFactor.ONE.ordinal(),
                    DestFactor.ZERO.ordinal());*/
            //?} else {
            /*RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            *///?}
            ShapeManagers.renderAll(matrixStack, tickDelta);
            //? >=1.21.5 {
            GlStateManager._disableBlend();
            //?} else {
            /*RenderSystem.disableBlend();
            *///?}


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
