package mypals.ml.shape.basics;

public interface CircleLikeShape {
    void setRadius(float radius);
    void setSegments(int segments);
    float getRadius();
    int getSegments();
    public enum CircleAxis{
        X,
        Y,
        Z
    }
}
