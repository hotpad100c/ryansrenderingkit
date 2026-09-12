package ml.mypals.ryansserverrenderingkit.shape.model;

import ml.mypals.ryansserverrenderingkit.shape.basics.core.LineLikeShape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.function.Consumer;

public class ObjModelShapeOutline extends ObjModelShape implements LineLikeShape {

    public float lineWidth;

    public ObjModelShapeOutline(Consumer<SimpleLineTransformer> transform,
                                Identifier resourceLocation,
                                Vec3 center,
                                float lineWidth,
                                Color color,
                                boolean seeThrough) {
        super((Consumer<DefaultTransformer>) (d) -> {}, resourceLocation, center, color, seeThrough);

        this.transformer = new SimpleLineTransformer(this, lineWidth, center);
        this.transformFunction = (t) -> transform.accept((SimpleLineTransformer) this.transformer);

        this.lineWidth = lineWidth;
        this.baseColor = color;

        ((SimpleLineTransformer) this.transformer).setWidth(this.lineWidth);
        this.transformer.setShapeWorldPivot(center);

        syncLastToTarget();
    }

    @Deprecated
    public ObjModelShapeOutline(Object ignored,
                                Consumer<SimpleLineTransformer> transform,
                                Identifier resourceLocation,
                                Vec3 center,
                                float lineWidth,
                                Color color,
                                boolean seeThrough) {
        this(transform, resourceLocation, center, lineWidth, color, seeThrough);
    }

    @Override
    public void setLineWidth(float width) {
        this.lineWidth = width;
        if (this.transformer instanceof SimpleLineTransformer slt) {
            slt.setWidth(width);
        }
    }

    @Override
    public float getLineWidth(boolean lerp) {
        return ((SimpleLineTransformer) this.transformer).getWidth(lerp);
    }

    @Override
    public void initDisplays(ServerLevel level) {
        // OBJ model outlines are temporarily stubbed in server-side display mode
    }

    @Override
    public void updateDisplays() {
        // OBJ model outlines are temporarily stubbed in server-side display mode
    }
}
