package ml.mypals.ryansserverrenderingkit.transform.shapeTransformers;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;
import java.util.List;

public class DefaultTransformer {
    private float delta = 0;
    public final Shape shape;

    public static final int WORLD = 1;
    public static final int LOCAL = 2;
    public static final int MATRIX = 4;

    public final TransformLayer local = new TransformLayer();
    public final TransformLayer world = new TransformLayer();
    public final TransformLayer matrix = new TransformLayer();

    public DefaultTransformer(Shape s, Vec3 center) {
        this.shape = s;
        this.world.setPosition(center);
        this.world.syncLastToTarget();
    }

    public Shape getShape() { return shape; }

    public void updateTickDelta(float d) {
        this.delta = d;
        updateAll(d);
    }

    public float getTickDelta() { return delta; }

    public void updateAll(float t) {
        local.update(t);
        world.update(t);
        matrix.update(t);
    }

    public void syncLastToTarget() {
        local.syncLastToTarget();
        world.syncLastToTarget();
        matrix.syncLastToTarget();
    }

    public void applyTransformations(Matrix4f mat, boolean lerp, int flags) {
        if ((flags & WORLD) != 0)  applyLayer(mat, world,  lerp);
        if ((flags & LOCAL) != 0)  applyLayer(mat, local,  lerp);
        if ((flags & MATRIX) != 0) applyLayer(mat, matrix, lerp);
    }

    public void applyModelTransformations(Matrix4f mat, boolean lerp) {
        applyTransformations(mat, lerp, WORLD | LOCAL);
    }

    public void applyLayer(Matrix4f mat, TransformLayer layer, boolean lerp) {
        Vector3d p = layer.position.getValue(lerp);
        Quaternionf r = layer.rotation.getValue(lerp);
        Vector3d s = layer.scale.getValue(lerp);

        mat.translate((float) p.x, (float) p.y, (float) p.z);
        mat.rotate(r);
        mat.scale((float) s.x, (float) s.y, (float) s.z);
    }

    public TransformLayer local()  { return local; }
    public TransformLayer world()  { return world; }
    public TransformLayer matrix() { return matrix; }

    public void applyHierarchy(Matrix4f mat, boolean lerp, int flags) {
        List<Shape> hierarchy = this.shape.getHierarchy();
        for (int i = hierarchy.size() - 1; i >= 1; i--) {
            Shape n = hierarchy.get(i);
            if (n.transformer != null) {
                n.transformer.applyTransformations(mat, lerp, flags);
            }
        }
    }

    public Matrix4f buildParentMatrix(boolean lerp) {
        Matrix4f mat = new Matrix4f();
        applyHierarchy(mat, lerp, WORLD | LOCAL);
        return mat;
    }

    public Matrix4f buildCombinedMatrix(boolean lerp) {
        Matrix4f mat = buildParentMatrix(lerp);
        applyTransformations(mat, lerp, WORLD | LOCAL | MATRIX);
        return mat;
    }

    public Transformation toTransformation(boolean lerp, Vec3 spawnPos) {
        Matrix4f mat = new Matrix4f();
        if (spawnPos != null) {
            mat.translate((float) -spawnPos.x, (float) -spawnPos.y, (float) -spawnPos.z);
        } else {
            Vec3 p = getShapeWorldPivot(lerp);
            mat.translate((float) -p.x, (float) -p.y, (float) -p.z);
        }
        mat.mul(buildCombinedMatrix(lerp));
        return DisplayTransformHelper.fromMatrix(mat);
    }

    public Transformation toTransformation(boolean lerp) {
        return toTransformation(lerp, null);
    }

    public Vec3 getShapeWorldPivot(boolean lerp) {
        Matrix4f mat = buildParentMatrix(lerp);
        Vec3 localPivot = this.world.getPosition(lerp);
        Vector3f v = new Vector3f((float) localPivot.x, (float) localPivot.y, (float) localPivot.z);
        v.mulPosition(mat);
        return new Vec3(v.x(), v.y(), v.z());
    }

    public Quaternionf getShapeWorldRotation(boolean lerp) {
        Matrix4f mat = buildParentMatrix(lerp);
        mat.rotate(this.world.getRotation(lerp));
        Quaternionf result = new Quaternionf();
        mat.getNormalizedRotation(result);
        return result;
    }

    public Vec3 getShapeWorldScale(boolean lerp) {
        Matrix4f mat = buildParentMatrix(lerp);
        Vec3 localScale = this.world.getScale(lerp);
        mat.scale((float) localScale.x, (float) localScale.y, (float) localScale.z);
        return new Vec3(
                new Vector3f(mat.m00(), mat.m01(), mat.m02()).length(),
                new Vector3f(mat.m10(), mat.m11(), mat.m12()).length(),
                new Vector3f(mat.m20(), mat.m21(), mat.m22()).length()
        );
    }

    public Vec3        getShapeLocalPivot(boolean lerp)    { return local.getPosition(lerp); }
    public Quaternionf getShapeLocalRotation(boolean lerp) { return local.getRotation(lerp); }
    public Vec3        getShapeLocalScale(boolean lerp)    { return local.getScale(lerp); }
    public Vec3        getShapeMatrixPivot(boolean lerp)   { return matrix.getPosition(lerp); }
    public Quaternionf getShapeMatrixRotation(boolean lerp){ return matrix.getRotation(lerp); }
    public Vec3        getShapeMatrixScale(boolean lerp)   { return matrix.getScale(lerp); }

    public Vec3        getWorldPivot()    { return getShapeWorldPivot(true); }
    public Quaternionf getWorldRotation() { return getShapeWorldRotation(true); }
    public Vec3        getWorldScale()    { return getShapeWorldScale(true); }
    public Vec3        getLocalPivot()    { return getShapeLocalPivot(true); }
    public Quaternionf getLocalRotation() { return getShapeLocalRotation(true); }
    public Vec3        getLocalScale()    { return getShapeLocalScale(true); }
    public Vec3        getMatrixPivot()   { return getShapeMatrixPivot(true); }
    public Quaternionf getMatrixRotation(){ return getShapeMatrixRotation(true); }
    public Vec3        getMatrixScale()   { return getShapeMatrixScale(true); }

    private static Quaternionf fromEulerDegrees(float x, float y, float z) {
        return new Quaternionf().rotateXYZ(
                (float) Math.toRadians(x),
                (float) Math.toRadians(y),
                (float) Math.toRadians(z)
        );
    }

    public void setShapeWorldPivot(Vec3 v)                              { world.setPosition(v); }
    public void setShapeWorldRotation(Quaternionf q)                    { world.setRotation(q); }
    public void setShapeWorldRotationDegrees(float x, float y, float z) { world.setRotation(fromEulerDegrees(x, y, z)); }
    public void setShapeWorldScale(Vec3 s)                              { world.setScale(s); }

    public void setShapeLocalPivot(Vec3 v)                              { local.setPosition(v); }
    public void setShapeLocalRotation(Quaternionf q)                    { local.setRotation(q); }
    public void setShapeLocalRotationDegrees(float x, float y, float z) { local.setRotation(fromEulerDegrees(x, y, z)); }
    public void setShapeLocalScale(Vec3 s)                              { local.setScale(s); }

    public void setShapeMatrixPivot(Vec3 v)                               { matrix.setPosition(v); }
    public void setShapeMatrixRotation(Quaternionf q)                     { matrix.setRotation(q); }
    public void setShapeMatrixRotationDegrees(float x, float y, float z)  { matrix.setRotation(fromEulerDegrees(x, y, z)); }
    public void setShapeMatrixScale(Vec3 s)                               { matrix.setScale(s); }

    public boolean asyncModelInfo() { return false; }
}