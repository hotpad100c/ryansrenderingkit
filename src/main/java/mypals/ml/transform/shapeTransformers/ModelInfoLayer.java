package mypals.ml.transform.shapeTransformers;

public abstract class ModelInfoLayer {
    public void update(float delta) {
    }

    public boolean async() {
        return false;
    }

    public void syncLastToTarget() {
    }
}
