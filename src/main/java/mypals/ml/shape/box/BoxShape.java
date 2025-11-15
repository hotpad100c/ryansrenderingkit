package mypals.ml.shape.box;

import mypals.ml.Helpers;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.BoxLikeShape;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BoxShape extends Shape implements BoxLikeShape {

    public enum BoxConstructionType { CENTER_AND_DIMENSIONS, CORNERS }

    public BoxShape(RenderingType type,
                    Consumer<BoxTransformer> transform,
                    Vec3 vec1,
                    Vec3 vec2,
                    Color color,
                    boolean seeThrough,
                    BoxConstructionType constructionType) {
        super(type, color, seeThrough);

        this.transformFunction = (t)->{transform.accept((BoxTransformer)this.transformer);};

        if (constructionType == BoxConstructionType.CENTER_AND_DIMENSIONS) {
            this.transformer = new BoxTransformer(this,new Vec3(Math.abs(vec2.x), Math.abs(vec2.y), Math.abs(vec2.z)),vec1);
        } else {
            Vec3 min = Helpers.min(vec1, vec2);
            Vec3 max = Helpers.max(vec1, vec2);
            Vec3 dims = max.subtract(min);
            Vec3 center = min.add(dims.scale(0.5));
            this.transformer = new BoxTransformer(this,dims,center);
        }

        
        syncLastToTarget();
    }



    @Override
    protected void generateRawGeometry(boolean lerp) {

    }
    @Override
    public Vec3 getMin() {
        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 half = bt.getDimension(false).scale(0.5);
        return bt.getWorldPivot().subtract(half);
    }

    @Override
    public Vec3 getMax() {
        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 half = bt.getDimension(false).scale(0.5);
        return bt.getWorldPivot().add(half);
    }

    @Override
    public void setMin(Vec3 min) {
        Vec3 max = getMax();
        Vec3 center = min.add(max).scale(0.5);
        Vec3 dims = max.subtract(min);
        BoxTransformer bt = (BoxTransformer) transformer;
        bt.setShapeWorldPivot(center);
        bt.setDimension(dims);
        
    }

    @Override
    public void setMax(Vec3 max) {
        Vec3 min = getMin();
        Vec3 center = min.add(max).scale(0.5);
        Vec3 dims = max.subtract(min);
        BoxTransformer bt = (BoxTransformer) transformer;
        bt.setShapeWorldPivot(center);
        bt.setDimension(dims);
        
    }


    public void setDimensions(Vec3 dimensions) {
        ((BoxTransformer) transformer).setDimension(
                new Vec3(Math.abs(dimensions.x), Math.abs(dimensions.y), Math.abs(dimensions.z))
        );
        
    }

    public void setCorners(Vec3 corner1, Vec3 corner2) {
        Vec3 min = Helpers.min(corner1, corner2);
        Vec3 max = Helpers.max(corner1, corner2);
        Vec3 center = min.add(max).scale(0.5);
        Vec3 dims = max.subtract(min);
        BoxTransformer bt = (BoxTransformer) transformer;
        bt.setShapeWorldPivot(center);
        bt.setDimension(dims);
        
    }

    @Override
    public void normalizeBounds() {

        Vec3 min = Helpers.min(getMin(), getMax());
        Vec3 max = Helpers.max(getMin(), getMax());
        setCorners(min, max);
    }


}
