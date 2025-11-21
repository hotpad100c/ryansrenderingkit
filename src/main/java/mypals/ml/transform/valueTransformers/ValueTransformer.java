package mypals.ml.transform.valueTransformers;

public abstract class ValueTransformer<T> {

    protected T last;      // value at the start of the current tick
    protected T target;    // value we are moving toward
    protected T current;   // interpolated value (updated each frame)

    /** Update tick-delta */
    public abstract void update(float delta);

    /** Set last → target (instant change) */
    public abstract void syncLastToTarget();

    /** Current interpolated value */
    public T getValue(boolean lerp) { return lerp?current:target; }

    public boolean async() {
        return !current.equals(target);
    }
    /** Set a new target*/
    protected abstract void setTarget(T value);
}