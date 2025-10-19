package mypals.ml.mixin;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMIxin implements VertexConsumer
{

}
