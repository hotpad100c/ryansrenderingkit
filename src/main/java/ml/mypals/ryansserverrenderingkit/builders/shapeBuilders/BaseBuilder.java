package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.Shape;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class BaseBuilder<T extends BaseBuilder<T, R>, R> implements ShapeBuilder<T> {

    protected Vec3 center = Vec3.ZERO;
    protected Color color = Color.WHITE;
    protected boolean seeThrough = false;

    protected ServerLevel level;
    protected boolean allDim = false;
    protected Predicate<ServerPlayer> viewerFilter = player -> true;
    protected BlockState blockState = null;

    private TransformerSupplier<R> transformerSupplier = () -> (t) -> {
    };

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    @Override
    public T pos(Vec3 center) {
        this.center = center;
        return self();
    }

    @Override
    public T color(Color color) {
        this.color = color;
        return self();
    }

    public T color(int color) {
        return this.color(new Color(color));
    }

    @Override
    public T seeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return self();
    }

    @Override
    public T level(ServerLevel level) {
        this.level = level;
        return self();
    }

    @Override
    public T allDim(boolean allDim) {
        this.allDim = allDim;
        return self();
    }

    @Override
    public T visibleTo(Predicate<ServerPlayer> filter) {
        this.viewerFilter = filter != null ? filter : player -> true;
        return self();
    }

    @Override
    public T block(BlockState state) {
        this.blockState = state;
        return self();
    }

    public T transform(Consumer<R> transformer) {
        this.transformerSupplier = () -> transformer;
        return self();
    }

    public T transform(TransformerSupplier<R> supplier) {
        this.transformerSupplier = supplier;
        return self();
    }

    protected Consumer<R> getTransformer() {
        return transformerSupplier.get();
    }

    protected <S extends Shape> S applyCommon(S shape) {
        if (this.level != null) shape.level(this.level);
        shape.allDim(this.allDim);
        shape.visibleTo(this.viewerFilter);
        if (this.blockState != null) shape.block(this.blockState);
        return shape;
    }

    @Override
    public abstract Shape build();

    @FunctionalInterface
    public interface TransformerSupplier<X> {
        Consumer<X> get();
    }
}
