package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.minecraftBuiltIn.BlockShape;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockShapeBuilder extends BaseBuilder<BlockShapeBuilder, DefaultTransformer> {

    private BlockState blockState = Blocks.AIR.defaultBlockState();
    private int light = LightTexture.FULL_BRIGHT;

    public BlockShapeBuilder block(BlockState blockState) {
        this.blockState = blockState;
        return this;
    }

    public BlockShapeBuilder light(int light) {
        this.light = light;
        return this;
    }

    @Override
    @Deprecated(since = "type is ignored, use build() instead")
    public BlockShape build(Shape.RenderingType type) {
        return build();
    }

    public BlockShape build() {
        return new BlockShape(
                getTransformer(),
                center,
                blockState,
                light);
    }

}