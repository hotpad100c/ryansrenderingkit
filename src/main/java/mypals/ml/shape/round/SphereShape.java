package mypals.ml.shape.round;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.basics.tags.DrawableTriangle;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class SphereShape extends Shape implements CircleLikeShape, DrawableTriangle {
    @Override
    protected void generateRawGeometry(boolean lerp) {
        generateSphereShape(lerp);
    }



    public SphereShape(RenderingType type, Consumer<FaceCircleShape.FaceCircleTransformer> transform, Vec3 center, int segments, float radius, Color color, boolean seeThrough) {
        super(type,color, seeThrough);
        this.transformer = new FaceCircleShape.FaceCircleTransformer(this, segments, radius, center);
        this.transformFunction = (defaultTransformer) -> transform.accept((FaceCircleShape.FaceCircleTransformer) this.transformer);
        syncLastToTarget();
        generateSphereShape(true);
    }

    public void generateSphereShape(boolean lerp) {
        ArrayList<Vec3> vs = new ArrayList<>();
        Vec3 center = Vec3.ZERO;
        double radius = getRadius(lerp);
        int segments = getSegments(lerp);

        int lonSegments = segments * 2;

        Vec3[][] vertexGrid = new Vec3[segments + 1][lonSegments + 1];

        for (int i = 0; i <= segments; i++) {
            double theta = i * Math.PI / segments;
            for (int j = 0; j <= lonSegments; j++) {
                double phi = j * 2 * Math.PI / lonSegments;
                vertexGrid[i][j] = sphericalToCartesian(center, radius, theta, phi);
                vs.add(vertexGrid[i][j]);
            }
        }

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < segments; i++) {
            for (int j = 0; j < lonSegments; j++) {
                int v0 = i * (lonSegments + 1) + j;
                int v1 = (i + 1) * (lonSegments + 1) + j;
                int v2 = i * (lonSegments + 1) + (j + 1);
                int v3 = (i + 1) * (lonSegments + 1) + (j + 1);

                indices.add(v0); indices.add(v1); indices.add(v2);
                indices.add(v2); indices.add(v1); indices.add(v3);
            }
        }

        indexBuffer = indices.stream().mapToInt(Integer::intValue).toArray();

        model_vertexes = vs;
    }

    private Vec3 sphericalToCartesian(Vec3 center, double r, double theta, double phi) {
        double x = center.x + r * Math.sin(theta) * Math.cos(phi);
        double y = center.y + r * Math.cos(theta);
        double z = center.z + r * Math.sin(theta) * Math.sin(phi);
        return new Vec3(x, y, z);
    }

    @Override
    public void setRadius(float radius) {
        ((FaceCircleShape.FaceCircleTransformer)this.transformer).setRadius(radius);
    }

    @Override
    public void setSegments(int segments) {
        ((FaceCircleShape.FaceCircleTransformer)this.transformer).setSegment(segments);
    }

    @Override
    public float getRadius(boolean lerp) {
        return ((FaceCircleShape.FaceCircleTransformer)this.transformer).getRadius(lerp);
    }

    @Override
    public int getSegments(boolean lerp) {
        return ((FaceCircleShape.FaceCircleTransformer)this.transformer).getSegment(lerp);
    }
}