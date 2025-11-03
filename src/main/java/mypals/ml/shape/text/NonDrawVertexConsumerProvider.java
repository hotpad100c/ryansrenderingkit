package mypals.ml.shape.text;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

public class NonDrawVertexConsumerProvider implements MultiBufferSource {
    private final BufferBuilder builder;
    public NonDrawVertexConsumerProvider(BufferBuilder builder){
        this.builder = builder;
    }
    @Override
    public @NotNull VertexConsumer getBuffer(RenderType layer) {
        return builder;
    }
}
