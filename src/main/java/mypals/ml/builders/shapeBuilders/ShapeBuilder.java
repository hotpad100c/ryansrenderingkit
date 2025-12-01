package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public interface ShapeBuilder<T extends ShapeBuilder<T>> {
    T pos(Vec3 center);

    T color(Color color);

    T seeThrough(boolean seeThrough);

    Shape build(Shape.RenderingType type);
}