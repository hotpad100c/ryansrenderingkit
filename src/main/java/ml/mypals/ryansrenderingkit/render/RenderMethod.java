package ml.mypals.ryansrenderingkit.render;

//? if >=26.2 {
import com.mojang.blaze3d.PrimitiveTopology;
//?}
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
//? if >=1.21.5 {
import ml.mypals.ryansrenderingkit.render.renderTypes.RyansRenderingKitRenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import static ml.mypals.ryansrenderingkit.render.renderTypes.RyansRenderingKitRenderTypes.*;

//?} else if <=1.21.1 {
/*import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import java.util.function.Supplier;
*///?} else > 1.21.1 {
/*import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.ShaderProgram;
*///?}
//? if >=1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?}
import org.jetbrains.annotations.NotNull;


public record RenderMethod(
        //? if >= 1.21.6 {
        RenderType/*? if <1.21.11 {*//*.CompositeRenderType*//*?}*/ seeThroughType,
        RenderType/*? if <1.21.11 {*//*.CompositeRenderType*//*?}*/ normalRenderType,
        //?} else if >= 1.21.5 {
        /*@NotNull RenderType seeThroughType,
        @NotNull RenderType normalRenderType,
        *///?} else if >1.21.1 {
        /*@NotNull ShaderProgram shader,
        *///?} else {
        /*@NotNull Supplier<ShaderInstance> shader,
        *///?}
        @NotNull /*? if >=26.2 {*/PrimitiveTopology/*?} else {*//*VertexFormat.Mode*//*?}*/ mode,
        @NotNull VertexFormat format,
        boolean cullFace
) {
    public static final RenderMethod LINES = new RenderMethod(
            //? if >= 1.21.5 {
            SEE_THROUGH_LINES,
            /*? if <1.21.11 {*//*RenderType.*//*?} else {*/RenderTypes. /*?}*/LINES,
            //?} else if >1.21.1 {
            /*CoreShaders.RENDERTYPE_LINES,
            *///?} else
            //GameRenderer::getRendertypeLinesShader,
            /*? if >=26.2 {*/PrimitiveTopology/*?} else {*//*VertexFormat.Mode*//*?}*/.LINES,
            //? if >=1.21.11 {
            DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH,
            //?} else {
            /*DefaultVertexFormat.POSITION_COLOR_NORMAL,
            *///?}
            false
    );

    public static final RenderMethod LINE_STRIP = new RenderMethod(
            //? if >= 1.21.5 {
            SEE_THROUGH_LINE_STRIP,
            /*? if <1.21.11 {*//*RenderType.LINE_STRIP*//*?} else {*/RyansRenderingKitRenderTypes.LINE_STRIP /*?}*/,
            //?} else if >1.21.1 {
            /*CoreShaders.RENDERTYPE_LINES,
            *///?} else {
            /*GameRenderer::getRendertypeLinesShader,
            *///?}
            /*? if >=26.2 {*/PrimitiveTopology/*?} else {*//*VertexFormat.Mode*//*?}*/./*? if <1.21.11 {*//*LINE_STRIP*//*?} else {*/DEBUG_LINE_STRIP/*?}*/,
            //? if >=1.21.11 {
            DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH,
            //?} else {
            /*DefaultVertexFormat.POSITION_COLOR_NORMAL,
            *///?}
            false
    );

    public static final RenderMethod TRIANGLES = new RenderMethod(
            //? if >= 1.21.5 {
            SEE_THROUGH_TRIANGLES,
            TRIANGLE,
            //?} else if >1.21.1 {
            /*CoreShaders.POSITION_COLOR,
            *///?} else
            //GameRenderer::getPositionColorShader,
            /*? if >=26.2 {*/PrimitiveTopology/*?} else {*//*VertexFormat.Mode*//*?}*/.TRIANGLES,
            DefaultVertexFormat.POSITION_COLOR,
            true
    );
}
