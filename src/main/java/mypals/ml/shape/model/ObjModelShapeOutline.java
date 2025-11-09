package mypals.ml.shape.model;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.LineLikeShape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ObjModelShapeOutline extends ObjModelShape implements LineLikeShape {

    public float lineWidth;
    public Color color = Color.WHITE;

    public ObjModelShapeOutline(RenderingType type,
                                Consumer<SimpleLineTransformer> transform,
                                ResourceLocation resourceLocation,
                                Vec3 center,
                                float lineWidth,
                                Color color) {
        this(type, transform, resourceLocation, center, lineWidth, color, false);
    }

    protected ObjModelShapeOutline(RenderingType type) {
        super(type, Color.WHITE, false);
    }

    protected ObjModelShapeOutline(RenderingType type, boolean seeThrough) {
        super(type, Color.WHITE, seeThrough);
    }

    public ObjModelShapeOutline(RenderingType type,
                                Consumer<SimpleLineTransformer> transform,
                                ResourceLocation resourceLocation,
                                Vec3 center,
                                float lineWidth,
                                Color color,
                                boolean seeThrough) {
        super(type, (d) -> {}, resourceLocation, center, color, seeThrough);

        this.transformer = new SimpleLineTransformer(this,lineWidth,center);
        this.transformFunction = (t) -> transform.accept((SimpleLineTransformer) this.transformer);

        this.lineWidth = lineWidth;
        this.color = color;

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
       return  ((SimpleLineTransformer)this.transformer).getWidth(lerp);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        super.generateRawGeometry(lerp);
    }

    @Override
    protected void drawInternal(VertexBuilder builder) {
        if (model_vertexes.isEmpty() || indexBuffer == null || indexBuffer.length < 3)
            return;

        RenderSystem.lineWidth(this.lineWidth);
        builder.putColor(this.color);

        for (int i = 0; i < indexBuffer.length; i += 3) {
            Vec3 v0 = model_vertexes.get(indexBuffer[i]);
            Vec3 v1 = model_vertexes.get(indexBuffer[i + 1]);
            Vec3 v2 = model_vertexes.get(indexBuffer[i + 2]);

            addLineSegment(builder, v0, v1);
            addLineSegment(builder, v1, v2);
            addLineSegment(builder, v2, v0);
        }
    }
}

