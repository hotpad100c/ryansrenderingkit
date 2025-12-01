package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.box.BoxShape;
import net.minecraft.world.phys.Vec3;

public interface BoxBuilder<T extends BoxBuilder<T>> extends ShapeBuilder<T> {
    T min(Vec3 min);

    T max(Vec3 max);

    T aabb(Vec3 min, Vec3 max);

    T size(Vec3 size);

    T construction(BoxShape.BoxConstructionType type);
}