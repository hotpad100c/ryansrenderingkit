package mypals.ml.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
//? if >=1.21.5 {
/*
import static mypals.ml.render.MainRender.TRIANGLE;
import static mypals.ml.render.MainRender.TRIANGLE_PIPLINE;
*/
//?} else if =1.21.1 {
/*import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
*///?} else if >1.21.1 {
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
    //? if <1.21.5 {
import net.minecraft.client.renderer.ShaderProgram;
    //?}
//?} else {
/*import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import java.util.function.Supplier;
*///?}
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public record RenderMethod(
        //? if >= 1.21.5 {
        /*@NotNull RenderType renderLayer,
        *///?} else if >1.21.1 {
        @NotNull ShaderProgram shader,
        //?} else {
        /*@NotNull Supplier<ShaderInstance> shader,
        *///?}
        @NotNull VertexFormat.Mode mode,
        @NotNull VertexFormat format,
        boolean cullFace
) {
    public static final RenderMethod LINES = new RenderMethod(
            //? if >= 1.21.5 {
            /*RenderType.LINES,
            *///?} else if >1.21.1 {
            CoreShaders.RENDERTYPE_LINES,
            //?} else
            /*GameRenderer::getRendertypeLinesShader,*/
            VertexFormat.Mode.LINES,
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            false
    );

    public static final RenderMethod LINE_STRIP = new RenderMethod(
            //? if >= 1.21.5 {
            /*RenderType.LINE_STRIP,
            *///?} else if >1.21.1 {
            CoreShaders.RENDERTYPE_LINES,
            //?} else
            /*GameRenderer::getRendertypeLinesShader,*/
            VertexFormat.Mode.LINE_STRIP,
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            false
    );

    public static final RenderMethod TRIANGLES = new RenderMethod(
            //? if >= 1.21.5 {
            /*TRIANGLE,
            *///?} else if >1.21.1 {
            CoreShaders.POSITION_COLOR,
            //?} else
            /*GameRenderer::getPositionColorShader,*/
            VertexFormat.Mode.TRIANGLES,
            DefaultVertexFormat.POSITION_COLOR,
            true
    );
}
