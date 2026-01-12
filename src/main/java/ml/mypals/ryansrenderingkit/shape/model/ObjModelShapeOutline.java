package ml.mypals.ryansrenderingkit.shape.model;

import com.mojang.blaze3d.systems.RenderSystem;
import ml.mypals.ryansrenderingkit.builders.vertexBuilders.VertexBuilder;
import ml.mypals.ryansrenderingkit.shape.basics.core.LineLikeShape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.function.Consumer;

public class ObjModelShapeOutline extends ObjModelShape implements LineLikeShape {

    public float lineWidth;

    public ObjModelShapeOutline(RenderingType type,
                                Consumer<SimpleLineTransformer> transform,
                                ResourceLocation resourceLocation,
                                Vec3 center,
                                float lineWidth,
                                Color color,
                                boolean seeThrough) {
        super(type, (d) -> {
        }, resourceLocation, center, color, seeThrough);

        this.transformer = new SimpleLineTransformer(this, lineWidth, center);
        this.transformFunction = (t) -> transform.accept((SimpleLineTransformer) this.transformer);

        this.lineWidth = lineWidth;
        this.baseColor = color;

        ((SimpleLineTransformer) this.transformer).setWidth(this.lineWidth);
        this.transformer.setShapeWorldPivot(center);

        syncLastToTarget();
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
    protected void generateRawGeometry(boolean lerp) {
        super.generateRawGeometry(lerp);
    }

    @Override
    protected void drawInternal(VertexBuilder builder) {
        if (model_vertexes.isEmpty() || indexBuffer == null || indexBuffer.length < 3)
            return;
        //? if <1.21.11 {
        RenderSystem.lineWidth(this.lineWidth);
        //?}
        builder.putColor(this.baseColor);

        for (int[] face : model.faces) {
            int n = face.length;
            if (n < 2) continue;

            for (int i = 0; i < n; i++) {
                Vec3 v0 = model_vertexes.get(face[i]);
                Vec3 v1 = model_vertexes.get(face[(i + 1) % n]);

                addLineSegment(builder, v0, v1,getLineWidth(true));
            }
        }

    }
}

