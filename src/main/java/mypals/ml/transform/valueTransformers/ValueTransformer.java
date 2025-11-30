package mypals.ml.transform.valueTransformers;

public abstract class ValueTransformer<T> {

    protected T last;
    protected T target;
    protected T current;
    public void update(float delta){
        if (last.equals(target)) return;
        updateVariables(delta);
    };

    public abstract void updateVariables(float delta);

    public abstract void syncLastToTarget();

    public T getValue(boolean lerp) { return lerp?current:target; }

    public boolean async() {
        return !current.equals(target);
    }
    protected abstract void setTarget(T value);
}