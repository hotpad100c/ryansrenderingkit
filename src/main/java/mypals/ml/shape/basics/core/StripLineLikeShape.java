package mypals.ml.shape.basics.core;

import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface StripLineLikeShape extends LineLikeShape {
    void setVertexes(List<Vec3> vertexes);

    List<Vec3> getVertexes();
}
