package ml.mypals.ryansrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.minecraftBuiltIn.ItemShape;
import ml.mypals.ryansrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.ItemTransforms;
//? if >1.18.2 {
/*import net.minecraft.world.item.ItemDisplayContext;
 *///?} else {
import net.minecraft.client.renderer.block.model.ItemTransforms;
//?}
import net.minecraft.world.item.ItemStack;

public class ItemShapeBuilder extends BaseBuilder<ItemShapeBuilder, DefaultTransformer> {

    private ItemStack itemStack = ItemStack.EMPTY;
    private /*? if >1.18.2 {*/ /*ItemDisplayContext*//*?} else {*/ItemTransforms.TransformType/*?}*/  displayContext
            = /*? if >1.18.2 {*/ /*ItemDisplayContext*//*?} else {*/ItemTransforms.TransformType/*?}*/ .FIXED;
    private int light = LightTexture.FULL_BRIGHT;

    public ItemShapeBuilder itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public ItemShapeBuilder displayMode(/*? if >1.18.2 {*/ /*ItemDisplayContext*//*?} else {*/ItemTransforms.TransformType/*?}*/  context) {
        this.displayContext = context;
        return this;
    }

    public ItemShapeBuilder light(int light) {
        this.light = light;
        return this;
    }

    @Override
    @Deprecated(since = "type is ignored, use build() instead")
    public ItemShape build(Shape.RenderingType type) {
        return build();
    }

    public ItemShape build() {
        return new ItemShape(
                getTransformer(),
                center,
                itemStack,
                displayContext,
                light);
    }
}