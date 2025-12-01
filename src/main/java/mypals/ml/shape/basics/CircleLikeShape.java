package mypals.ml.shape.basics;

public interface CircleLikeShape {
    void setRadius(float radius);

    void setSegments(int segments);

    float getRadius(boolean lerp);

    int getSegments(boolean lerp);

    enum CircleAxis {
        X,
        Y,
        Z
    }
}
