package mypals.ml.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.ShaderProgram;
import org.jetbrains.annotations.NotNull;


public record RenderMethod(
        @NotNull ShaderProgram shader,
        @NotNull VertexFormat.Mode mode,
        @NotNull VertexFormat format,
        boolean cullFace
)  {
    public static final RenderMethod LINES = new RenderMethod(
            CoreShaders.RENDERTYPE_LINES,
            VertexFormat.Mode.LINES,
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            false
    );

    public static final RenderMethod LINE_STRIP = new RenderMethod(
            CoreShaders.RENDERTYPE_LINES,
            VertexFormat.Mode.LINE_STRIP,
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            false
    );

    public static final RenderMethod TRIANGLES = new RenderMethod(
            CoreShaders.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLES,
            DefaultVertexFormat.POSITION_COLOR,
            true
    );
}
