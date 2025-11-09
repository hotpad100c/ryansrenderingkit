package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BaseBuilder<T extends BaseBuilder<T, R>, R> implements ShapeBuilder<T> {

    protected Vec3 center = Vec3.ZERO;
    protected Color color = Color.WHITE;
    protected boolean seeThrough = false;

    private TransformerSupplier<R> transformerSupplier = () ->(t)-> {};

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    @Override public T pos(Vec3 center) { this.center = center; return self(); }
    @Override public T color(Color color) { this.color = color; return self(); }
    public T color(int color) {return this.color(new Color(color)); }
    @Override public T seeThrough(boolean seeThrough) { this.seeThrough = seeThrough; return self(); }

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

    public abstract Shape build(Shape.RenderingType type);

    @FunctionalInterface
    public interface TransformerSupplier<X> {
        Consumer<X> get();
    }
}
