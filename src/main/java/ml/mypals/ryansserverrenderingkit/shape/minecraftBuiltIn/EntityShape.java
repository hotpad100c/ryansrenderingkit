package ml.mypals.ryansserverrenderingkit.shape.minecraftBuiltIn;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.function.Consumer;

public class EntityShape extends Shape {

    public Entity entity;
    public int light;

    public EntityShape(Consumer<DefaultTransformer> transform,
                       Vec3 center, Entity entity, int light) {
        super(transform, Color.WHITE, center, false);
        this.entity = entity;
        this.light = light;
        this.transformer.setShapeWorldPivot(center);
        generateRawGeometry(false);
        syncLastToTarget();
    }

    @Deprecated
    public EntityShape(Object ignored,
                       Consumer<DefaultTransformer> transform,
                       Vec3 center, Entity entity, int light) {
        this(transform, center, entity, light);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();
        if (entity != null) {
            float w = entity.getBbWidth() / 2;
            float h = entity.getBbHeight();
            modelVertexes.add(new Vec3(-w, 0, -w));
            modelVertexes.add(new Vec3(+w, +h, +w));
        }
    }

    @Override
    public void initDisplays(ServerLevel level) {
        if (entity != null) {
            AABB box = entity.getBoundingBox();
            Transformation t = DisplayTransformHelper.entity(entity, box, 0.02);
            VirtualDisplay display = VirtualDisplay.block(level, entity.getX(), entity.getY(), entity.getZ(), getBlockState())
                    .bright()
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t)
                    .ride(entity);
            this.displays.add(display);
        } else {
            Vec3 pos = transformer.getWorldPivot();
            VirtualDisplay display = VirtualDisplay.block(level, pos.x, pos.y, pos.z, getBlockState())
                    .bright()
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(transformer.toTransformation(false));
            this.displays.add(display);
        }
    }

    @Override
    public void updateDisplays() {
        if (this.displays.isEmpty()) return;
        VirtualDisplay display = this.displays.getFirst();
        if (entity != null && !entity.isRemoved()) {
            AABB box = entity.getBoundingBox();
            Transformation t = DisplayTransformHelper.entity(entity, box, 0.02);
            display.pos(entity.getX(), entity.getY(), entity.getZ())
                    .blockState(getBlockState())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t)
                    .ride(entity);
        } else {
            Vec3 pos = transformer.getWorldPivot();
            display.pos(pos.x, pos.y, pos.z)
                    .blockState(getBlockState())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(transformer.toTransformation(true));
        }
    }
}