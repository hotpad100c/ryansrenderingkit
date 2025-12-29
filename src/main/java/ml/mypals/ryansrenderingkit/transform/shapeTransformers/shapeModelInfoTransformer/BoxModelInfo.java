package ml.mypals.ryansrenderingkit.transform.shapeTransformers.shapeModelInfoTransformer;

import ml.mypals.ryansrenderingkit.transform.shapeTransformers.ModelInfoLayer;
import ml.mypals.ryansrenderingkit.transform.valueTransformers.Vec3Transformer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

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

    public Vec3 getDimension(boolean useLerp) {

        Vector3d v = boxDimensionTransformer.getValue(useLerp);
        return new Vec3(v.x, v.y, v.z);
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
