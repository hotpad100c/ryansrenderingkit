package ml.mypals.ryansrenderingkit.transform.shapeTransformers;

import com.mojang.blaze3d.vertex.PoseStack;
//? if <=1.18.2 {
/*import com.mojang.math.Quaternion;
import com.sun.jna.platform.win32.COM.IComEnumVariantIterator;
import com.sun.jna.platform.win32.COM.util.IComEnum;
*///?}
import ml.mypals.ryansrenderingkit.shape.Shape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import javax.swing.*;
import java.lang.Math;
import java.util.List;

import static ml.mypals.ryansrenderingkit.utils.Helpers.convertToJomlIfNeeded;

public class DefaultTransformer {
    private float delta = 0;
    public final Shape shape;

    public static final int WORLD = 1;
    public static final int LOCAL = 2;
    public static final int MATRIX = 4;
    public static final int CAMSPACE = 8;

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

    public void applyTransformations(PoseStack stack, boolean lerp, int flags) {
        if ((flags & WORLD) != 0)  applyLayer(stack, world,  lerp, (flags & CAMSPACE) != 0);
        if ((flags & LOCAL) != 0)  applyLayer(stack, local,  lerp, false);
        if ((flags & MATRIX) != 0) applyLayer(stack, matrix, lerp, false);
    }

    public void applyModelTransformations(PoseStack stack, boolean lerp) {
        applyTransformations(stack, lerp, WORLD | LOCAL);
    }

    public void applyLayer(PoseStack stack, TransformLayer layer, boolean lerp, boolean camSpace) {
        Vector3d p = layer.position.getValue(lerp);
        Quaternionf r = layer.rotation.getValue(lerp);
        Vector3d s = layer.scale.getValue(lerp);

        double tx = p.x, ty = p.y, tz = p.z;

        if (camSpace && this.shape.parent == null) {
            Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
            //? if >=1.21.11 {
            Vec3 cameraPos = camera.position();
             //?} else {
            /*Vec3 cameraPos = camera.getPosition();
            *///?}
            tx -= cameraPos.x;
            ty -= cameraPos.y;
            tz -= cameraPos.z;
        }

        stack.translate(tx, ty, tz);
        //? if >1.18.2 {
        stack.mulPose(r);
        //?} else {
        /*stack.mulPose(new Quaternion(r.x, r.y, r.z, r.w));
         *///?}
        stack.scale((float) s.x, (float) s.y, (float) s.z);
    }

    public TransformLayer local()  { return local; }
    public TransformLayer world()  { return world; }
    public TransformLayer matrix() { return matrix; }

    public void applyHierarchy(PoseStack poseStack, boolean lerp, int flags) {
        List<Shape> hierarchy = this.shape.getHierarchy();
        for (int i = hierarchy.size() - 1; i >= 1; i--) {
            Shape n = hierarchy.get(i);
            if (n.transformer != null) {
                n.transformer.applyTransformations(poseStack, lerp, flags);
            }
        }
    }

    // 抽取公共父级矩阵构建，避免三个 getShapeWorld* 方法各自重复
    private Matrix4f buildParentMatrix(boolean lerp) {
        PoseStack poseStack = new PoseStack();
        applyHierarchy(poseStack, lerp, WORLD | LOCAL);
        return convertToJomlIfNeeded(poseStack.last().pose());
    }

    public Vec3 getShapeWorldPivot(boolean lerp) {
        Matrix4f mat = buildParentMatrix(lerp);
        Vec3 localPivot = this.world.getPosition(lerp);
        Vector3f v = new Vector3f((float) localPivot.x, (float) localPivot.y, (float) localPivot.z);
        //? if >1.18.2 {
        v.mulPosition(mat);
        //?} else {
        /*float x = v.x(), y = v.y(), z = v.z();
        v.set(
            Math.fma(mat.m00(),x,Math.fma(mat.m10(),y,Math.fma(mat.m20(),z,mat.m30()))),
            Math.fma(mat.m01(),x,Math.fma(mat.m11(),y,Math.fma(mat.m21(),z,mat.m31()))),
            Math.fma(mat.m02(),x,Math.fma(mat.m12(),y,Math.fma(mat.m22(),z,mat.m32())))
        );
        *///?}
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

    public Vec3       getShapeLocalPivot(boolean lerp)    { return local.getPosition(lerp); }
    public Quaternionf getShapeLocalRotation(boolean lerp) { return local.getRotation(lerp); }
    public Vec3       getShapeLocalScale(boolean lerp)    { return local.getScale(lerp); }
    public Vec3       getShapeMatrixPivot(boolean lerp)   { return matrix.getPosition(lerp); }
    public Quaternionf getShapeMatrixRotation(boolean lerp){ return matrix.getRotation(lerp); }
    public Vec3       getShapeMatrixScale(boolean lerp)   { return matrix.getScale(lerp); }

    public Vec3        getWorldPivot()    { return getShapeWorldPivot(true); }
    public Quaternionf getWorldRotation() { return getShapeWorldRotation(true); }
    public Vec3        getWorldScale()    { return getShapeWorldScale(true); }
    public Vec3        getLocalPivot()    { return getShapeLocalPivot(true); }
    public Quaternionf getLocalRotation() { return getShapeLocalRotation(true); }
    public Vec3        getLocalScale()    { return getShapeLocalScale(true); }
    public Vec3        getMatrixPivot()   { return getShapeMatrixPivot(true); }
    public Quaternionf getMatrixRotation(){ return getShapeMatrixRotation(true); }
    public Vec3        getMatrixScale()   { return getShapeMatrixScale(true); }

    // 抽取欧拉角转四元数，三处 setShape*RotationDegrees 共用
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