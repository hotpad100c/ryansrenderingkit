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

public class ObjModelShapeOutline extends ObjModelShape implements LineLikeShape {

    public float lineWidth;
    public Color color = Color.white;
    public ObjModelShapeOutline(RenderingType type, BiConsumer<DefaultLineTransformer, Shape> transform, ResourceLocation resourceLocation, Vec3 center,float lineWidth, Color color) {
        this(type, transform,resourceLocation,center,lineWidth,color,false);
    }

    protected ObjModelShapeOutline(RenderingType type) {
        super(type);
    }

    protected ObjModelShapeOutline(RenderingType type, boolean seeThrough) {
        super(type, seeThrough);
    }

    public ObjModelShapeOutline(RenderingType type, BiConsumer<DefaultLineTransformer, Shape> transform, ResourceLocation resourceLocation, Vec3 center,float lineWidth, Color color, boolean seeThrough) {
        super(type, seeThrough);
        this.transformer = new DefaultLineTransformer(this);
        this.transformFunction = (tr,s)->{transform.accept((DefaultLineTransformer)this.transformer,this);};
        try {
            loadOBJ(resourceLocation);
        }catch (IOException e){
            e.printStackTrace();
        }
        this.lineWidth = lineWidth;
        ((DefaultLineTransformer)this.transformer).setWidth(this.lineWidth);
        syncLastToTarget();
        this.color = color;
        this.centerPoint = center;
        this.transformer.setShapeCenterPos(center);
    }

    @Override
    public void setLineWidth(float width) {
        this.lineWidth = width;
    }

    @Override
    public void draw(VertexBuilder builder) {
        RenderSystem.lineWidth(this.lineWidth);
        builder.putColor(this.color);

        if (model == null) return;

        for (int[] face : model.faces) {
            int n = face.length;
            if (n < 2) continue;

            for (int i = 0; i < n; i++) {
                int next = (i + 1) % n;

                Vec3 v0 = model.vertices.get(face[i]).subtract(modelCenter).add(centerPoint);
                Vec3 v1 = model.vertices.get(face[next]).subtract(modelCenter).add(centerPoint);

                addLineSegment(builder,v0,v1);
            }
        }
    }


}
