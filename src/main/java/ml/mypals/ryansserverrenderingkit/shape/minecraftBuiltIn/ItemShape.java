package ml.mypals.ryansserverrenderingkit.shape.minecraftBuiltIn;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Brightness;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.function.Consumer;

public class ItemShape extends Shape {

    public int light;
    public ItemStack item;
    public ItemDisplayContext itemDisplayContext = ItemDisplayContext.FIXED;

    public ItemShape(Consumer<DefaultTransformer> transform,
                     Vec3 center, ItemStack item, ItemDisplayContext itemDisplayContext, int light) {
        super(transform, Color.WHITE, center, false);
        this.item = item;
        this.transformer.setShapeWorldPivot(center);
        this.itemDisplayContext = itemDisplayContext != null ? itemDisplayContext : ItemDisplayContext.FIXED;
        this.light = light;
        generateRawGeometry(false);
        syncLastToTarget();
    }

    @Deprecated
    public ItemShape(Object ignored,
                     Consumer<DefaultTransformer> transform,
                     Vec3 center, ItemStack item, ItemDisplayContext itemDisplayContext, int light) {
        this(transform, center, item, itemDisplayContext, light);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();
        modelVertexes.add(new Vec3(-0.25, -0.25, -0.25));
        modelVertexes.add(new Vec3(0.25, 0.25, 0.25));
    }

    @Override
    public void initDisplays(ServerLevel level) {
        Vec3 pos = transformer.getWorldPivot();
        VirtualDisplay display = VirtualDisplay.item(level, pos.x, pos.y, pos.z, this.item, this.itemDisplayContext)
                .seeThrough(this.seeThrough, this.baseColor.getRGB())
                .transform(transformer.toTransformation(false));
        if (this.light > 0) {
            display.brightness(new Brightness(light & 15, (light >> 4) & 15));
        }
        this.displays.add(display);
    }

    @Override
    public void updateDisplays() {
        if (this.displays.isEmpty()) return;
        Vec3 pos = transformer.getWorldPivot();
        Transformation t = transformer.toTransformation(true);
        VirtualDisplay display = this.displays.getFirst();
        display.pos(pos.x, pos.y, pos.z)
                .itemStack(this.item)
                .seeThrough(this.seeThrough, this.baseColor.getRGB())
                .transform(t);
        if (this.light > 0) {
            display.brightness(new Brightness(light & 15, (light >> 4) & 15));
        }
    }
}