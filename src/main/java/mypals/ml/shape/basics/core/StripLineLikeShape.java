package mypals.ml.shape.basics.core;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.drawTypes.DrawableLine;
import mypals.ml.transform.Vec3dTransformer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public interface StripLineLikeShape extends LineLikeShape {
    void setVertexes(List<Vec3d> vertexes);
    List<Vec3d> getVertexes();
}
