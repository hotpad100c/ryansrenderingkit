package mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer;

import mypals.ml.transform.shapeTransformers.ModelInfoLayer;
import mypals.ml.transform.valueTransformers.Vec3Transformer;
import net.minecraft.world.phys.Vec3;

public class BoxModelInfo extends ModelInfoLayer {
    public Vec3Transformer boxDimensionTransformer;

    public BoxModelInfo(Vec3 dim) {
        boxDimensionTransformer = new Vec3Transformer(dim);
    }

    public boolean async() {
        return boxDimensionTransformer.async();
    }

    public void update(float delta) {
        boxDimensionTransformer.update(delta);
    }

    public Vec3 getDimension(boolean lerp) {
        return boxDimensionTransformer.getValue(lerp);
    }

    public void setDimension(Vec3 target) {
        boxDimensionTransformer.setTargetVector(target);
    }

    @Override
    public void syncLastToTarget() {
        this.boxDimensionTransformer.syncLastToTarget();
        super.syncLastToTarget();
    }
}
