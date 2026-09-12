package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.minecraftBuiltIn.BlockShape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockShapeBuilder extends BaseBuilder<BlockShapeBuilder, DefaultTransformer> {

    private BlockState targetBlockState = Blocks.AIR.defaultBlockState();
    private int light = 0x00F000F0;

    @Override
    public BlockShapeBuilder block(BlockState blockState) {
        this.targetBlockState = blockState;
        super.block(blockState);
        return this;
    }

    public BlockShapeBuilder light(int light) {
        this.light = light;
        return this;
    }

    @Override
    public BlockShape build() {
        BlockShape shape = new BlockShape(
                getTransformer(),
                center,
                targetBlockState,
                light);
        return applyCommon(shape);
    }
}