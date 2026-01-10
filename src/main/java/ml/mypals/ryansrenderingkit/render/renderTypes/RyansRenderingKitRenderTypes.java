package ml.mypals.ryansrenderingkit.render.renderTypes;
//? if >= 1.21.5 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.OptionalDouble;

import static ml.mypals.ryansrenderingkit.RyansRenderingKit.MOD_ID;
import static net.minecraft.client.renderer.RenderStateShard.VIEW_OFFSET_Z_LAYERING;
*///?}
public class RyansRenderingKitRenderTypes {
    //? if >= 1.21.5 {
    /*private static final RenderPipeline noDepthTriangles = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(new ResourceLocation(MOD_ID, "no_depth_quads"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
            .build();

    public static final RenderType.CompositeRenderType SEE_THROUGH_TRIANGLES =
            RenderType.create(
                    "see_through_triangle",
                    256,
                    false,
                    true,
                    noDepthTriangles,
                    RenderType.CompositeState.builder()
                            .createCompositeState(false)
            );


    private static final RenderPipeline noDepthLines = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(new ResourceLocation(MOD_ID, "no_depth_lines"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES)
            .build();

    public static final RenderType.CompositeRenderType SEE_THROUGH_LINES =
            RenderType.create(
                    "see_through_lines",
                    256,
                    false,
                    true,
                    noDepthLines,
                    RenderType.CompositeState.builder()
                            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                            .createCompositeState(false)
            );

    private static final RenderPipeline noDepthLineStrip = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(new ResourceLocation(MOD_ID, "no_depth_line_strip"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINE_STRIP)
            .build();

    public static final RenderType.CompositeRenderType SEE_THROUGH_LINE_STRIP =
            RenderType.create(
                    "see_through_line_strip",
                    256,
                    false,
                    true,
                    noDepthLineStrip,
                    RenderType.CompositeState.builder()
                            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                            .createCompositeState(false)
            );
    *///?}
}
