package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.minecraftBuiltIn.ItemShape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemShapeBuilder extends BaseBuilder<ItemShapeBuilder, DefaultTransformer> {

    private ItemStack itemStack = ItemStack.EMPTY;
    private ItemDisplayContext displayContext = ItemDisplayContext.FIXED;
    private int light = 0x00F000F0;

    public ItemShapeBuilder itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public ItemShapeBuilder item(ItemStack itemStack) {
        return itemStack(itemStack);
    }

    public ItemShapeBuilder displayMode(ItemDisplayContext context) {
        this.displayContext = context;
        return this;
    }

    public ItemShapeBuilder itemDisplayContext(ItemDisplayContext context) {
        return displayMode(context);
    }

    public ItemShapeBuilder light(int light) {
        this.light = light;
        return this;
    }

    @Override
    public ItemShape build() {
        ItemShape shape = new ItemShape(
                getTransformer(),
                center,
                itemStack,
                displayContext,
                light);
        return applyCommon(shape);
    }
}