package mypals.ml.shape.text;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

public class NonDrawVertexConsumerProvider implements VertexConsumerProvider {
    private final BufferBuilder builder;
    public NonDrawVertexConsumerProvider(BufferBuilder builder){
        this.builder = builder;
    }
    @Override
    public VertexConsumer getBuffer(RenderLayer layer) {
        return builder;
    }
}
