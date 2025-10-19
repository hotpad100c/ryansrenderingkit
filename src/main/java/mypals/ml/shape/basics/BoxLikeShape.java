package mypals.ml.shape.basics;

import net.minecraft.util.math.Vec3d;

public interface BoxLikeShape {
    Vec3d getMin();
    Vec3d getMax();
    default Vec3d getCenter() {
        double centerX = (getMin().x + getMax().x) / 2.0;
        double centerY = (getMin().y + getMax().y) / 2.0;
        double centerZ = (getMin().z + getMax().z) / 2.0;
        return new Vec3d(centerX, centerY, centerZ);
    }
}
