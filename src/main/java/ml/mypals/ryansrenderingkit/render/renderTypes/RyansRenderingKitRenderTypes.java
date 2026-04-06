package ml.mypals.ryansrenderingkit.render.renderTypes;
//? if >= 1.21.5 {
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
//? if <1.21.11 {
/*import net.minecraft.client.renderer.RenderStateShard;
import static net.minecraft.client.renderer.RenderStateShard.VIEW_OFFSET_Z_LAYERING;
*///?} else {
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?}
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.Optional;
import java.util.OptionalDouble;

import static ml.mypals.ryansrenderingkit.RyansRenderingKit.MOD_ID;
//?}
public class RyansRenderingKitRenderTypes {

    //? >= 1.21.5 {
    public static final RenderType/*? if <1.21.11 {*//*.CompositeRenderType*//*?}*/ TRIANGLE;
    public static final RenderPipeline TRIANGLE_PIPLINE;
    static{

        TRIANGLE_PIPLINE = RenderPipelines
                .register(RenderPipeline.builder(
                                RenderPipelines.DEBUG_FILLED_SNIPPET)
                        .withLocation("pipeline/debug_triangle")
                        .withCull(false).withVertexFormat(DefaultVertexFormat.POSITION_COLOR,
                                VertexFormat.Mode.TRIANGLES)
                        .build());
        //? if >=1.21.11 {
        TRIANGLE = RenderType.create("r_triangle",
                RenderSetup.builder(TRIANGLE_PIPLINE).createRenderSetup());
        //?} else {
        /*TRIANGLE = RenderType.create("r_triangle",
                1536,
                false,
                true,
                TRIANGLE_PIPLINE, RenderType.CompositeState.builder().createCompositeState(false));
        *///?}
    }
    //?}

    //? if >= 1.21.5 {
    private static final RenderPipeline noDepthTriangles = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "no_depth_quads"))
            .withDepthStencilState(Optional.empty())
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
            .build();

    //? if >=1.21.11 {
    
            public static final RenderType SEE_THROUGH_TRIANGLES =
            RenderType.create(
                    "see_through_triangle",
                    RenderSetup.builder(noDepthTriangles)
                            .sortOnUpload().createRenderSetup()
            );
     //?} else {
    /*public static final RenderType.CompositeRenderType SEE_THROUGH_TRIANGLES =
            RenderType.create(
                    "see_through_triangle",
                    256,
                    false,
                    true,
                    noDepthTriangles,
                    RenderType.CompositeState.builder()
                            .createCompositeState(false)
            );
    *///?}


    private static final RenderPipeline noDepthLines = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "no_depth_lines"))
            .withDepthStencilState(Optional.empty())
            .withCull(false)
            //? if <1.21.11 {
            /*.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES)
            *///?} else {
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
            //?}
            .build();

    //? if >=1.21.11 {
    public static final RenderType SEE_THROUGH_LINES =
            RenderType.create(
                    "see_through_lines",
                    RenderSetup.builder(noDepthLines).createRenderSetup()
            );
    //?} else {
    
    /*public static final RenderType.CompositeRenderType SEE_THROUGH_LINES =

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
    *///?}
    private static final RenderPipeline noDepthLineStrip = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "no_depth_line_strip"))
            .withDepthStencilState(Optional.empty())
            .withCull(false)
            //? if <1.21.11 {
            /*.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES)
             *///?} else {
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.DEBUG_LINE_STRIP)
            //?}
            .build();

    //? if >=1.21.11 {
    public static final RenderType SEE_THROUGH_LINE_STRIP =
            RenderType.create("see_through_line_strip",
                    RenderSetup.builder(noDepthLineStrip).createRenderSetup());


    private static final RenderPipeline lineStrip = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "no_depth_line_strip"))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.DEBUG_LINE_STRIP)
            .build();

    public static final RenderType LINE_STRIP =
            RenderType.create("line_strip",
                    RenderSetup.builder(lineStrip).createRenderSetup());
    //?} else {
    /*public static final RenderType.CompositeRenderType SEE_THROUGH_LINE_STRIP =
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
    //?}
}
