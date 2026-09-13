package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.Shape;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.function.Predicate;

public interface ShapeBuilder<T extends ShapeBuilder<T>> {
    T pos(Vec3 center);

    T color(Color color);

    T seeThrough(boolean seeThrough);

    T renderFace(boolean renderFace);

    T renderWireframe(boolean renderWireframe);

    T level(ServerLevel level);

    T allDim(boolean allDim);

    T visibleTo(Predicate<ServerPlayer> filter);

    T block(BlockState state);

    Shape build();
}
