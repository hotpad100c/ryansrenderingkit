package mypals.ml.shape.basics.core;

import java.util.List;
import net.minecraft.world.phys.Vec3;

public interface StripLineLikeShape extends LineLikeShape {
    void setVertexes(List<Vec3> vertexes);
    List<Vec3> getVertexes();
}
