package mypals.ml.shape.basics.core;

import net.minecraft.util.math.Vec3d;

import java.util.List;

public interface StripLineLikeShape extends LineLikeShape {
    void setVertexes(List<Vec3d> vertexes);
    List<Vec3d> getVertexes();
}
