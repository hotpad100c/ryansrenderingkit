package mypals.ml.render;

import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.jetbrains.annotations.NotNull;


public record RenderMethod(
        @NotNull ShaderProgramKey shader,
        @NotNull VertexFormat.DrawMode mode,
        @NotNull VertexFormat format,
        boolean cullFace
)  {
    public static final RenderMethod QUADS = new RenderMethod(
            ShaderProgramKeys.POSITION_COLOR,
            VertexFormat.DrawMode.QUADS,
            VertexFormats.POSITION_COLOR,
            true
    );

    public static final RenderMethod LINES = new RenderMethod(
            ShaderProgramKeys.RENDERTYPE_LINES,
            VertexFormat.DrawMode.LINES,
            VertexFormats.LINES,
            false
    );

    public static final RenderMethod LINE_STRIP = new RenderMethod(
            ShaderProgramKeys.RENDERTYPE_LINES,
            VertexFormat.DrawMode.LINE_STRIP,
            VertexFormats.LINES,
            false
    );

    public static final RenderMethod TRIANGLES = new RenderMethod(
            ShaderProgramKeys.POSITION_COLOR,
            VertexFormat.DrawMode.TRIANGLES,
            VertexFormats.POSITION_COLOR,
            true
    );
}
