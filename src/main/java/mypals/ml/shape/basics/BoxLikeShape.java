package mypals.ml.shape.basics;

import net.minecraft.util.math.Vec3d;

public interface BoxLikeShape {
    Vec3d getMin();
    Vec3d getMax();
    Vec3d getCenter();
}
