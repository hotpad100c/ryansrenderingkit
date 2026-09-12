package ml.mypals.ryansserverrenderingkit.shape.minecraftBuiltIn;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Brightness;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.function.Consumer;

public class BlockShape extends Shape {

    public BlockState blockState;
    public int light;

    public BlockShape(Consumer<DefaultTransformer> transform,
                      Vec3 center, BlockState block, int light) {
        super(transform, Color.WHITE, center, false);
        this.blockState = block;
        this.light = light;
        this.transformer.setShapeLocalPivot(new Vec3(-0.5, -0.5, -0.5));
        this.transformer.setShapeWorldPivot(center);
        generateRawGeometry(false);
        syncLastToTarget();
    }

    @Deprecated
    public BlockShape(Object ignored,
                      Consumer<DefaultTransformer> transform,
                      Vec3 center, BlockState block, int light) {
        this(transform, center, block, light);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();
        modelVertexes.add(new Vec3(-0.5, -0.5, -0.5));
        modelVertexes.add(new Vec3(0.5, 0.5, 0.5));
    }

    @Override
    public void initDisplays(ServerLevel level) {
        Vec3 pos = transformer.getWorldPivot();
        VirtualDisplay display = VirtualDisplay.block(level, pos.x, pos.y, pos.z, this.blockState)
                .seeThrough(this.seeThrough, this.baseColor.getRGB())
                .transform(transformer.toTransformation(false, pos));
        if (this.light > 0) {
            display.brightness(new Brightness(light & 15, (light >> 4) & 15));
        }
        this.displays.add(display);
    }

    @Override
    public void updateDisplays() {
        if (this.displays.isEmpty()) return;
        VirtualDisplay display = this.displays.getFirst();
        Vec3 spawnPos = new Vec3(display.getEntity().getX(), display.getEntity().getY(), display.getEntity().getZ());
        Transformation t = transformer.toTransformation(true, spawnPos);
        display.blockState(this.blockState)
                .seeThrough(this.seeThrough, this.baseColor.getRGB())
                .transform(t);
        if (this.light > 0) {
            display.brightness(new Brightness(light & 15, (light >> 4) & 15));
        }
    }
}