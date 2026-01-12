package ml.mypals.ryansrenderingkit.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
//? if >1.18.2 {
import com.mojang.math.Axis;
//?}
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;


//? if >1.21.1 {

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShapeRenderer;
//?} else {
/*import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
*///?}

//? if >=1.21.11 {
/*import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.SimpleGizmoCollector;
*///?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static ml.mypals.ryansrenderingkit.RyansRenderingKit.MOD_ID;

public class Helpers {
    public static ResourceLocation generateUniqueId(String prefix) {
        long timestamp = System.currentTimeMillis();
        int randomNum = ThreadLocalRandom.current().nextInt(10000);
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, prefix.toLowerCase() + "_" + timestamp + "_" + randomNum);
    }

    public static Vec3 max(Vec3 a, Vec3 b) {
        return new Vec3(
                Math.max(a.x, b.x),
                Math.max(a.y, b.y),
                Math.max(a.z, b.z)
        );
    }

    public static Vec3 min(Vec3 a, Vec3 b) {
        return new Vec3(
                Math.min(a.x, b.x),
                Math.min(a.y, b.y),
                Math.min(a.z, b.z)
        );
    }

    public static Vec3 calculateCentroid(List<Vec3> vertices) {
        double sumX = 0, sumY = 0, sumZ = 0;
        for (Vec3 v : vertices) {
            sumX += v.x;
            sumY += v.y;
            sumZ += v.z;
        }
        double n = vertices.size();
        return new Vec3(sumX / n, sumY / n, sumZ / n);
    }


    public static int multiplyRGB(int color, float shade) {
        int alpha = color >>> 24 & 255;
        int red = (int) ((float) (color >>> 16 & 255) * shade);
        int green = (int) ((float) (color >>> 8 & 255) * shade);
        int blue = (int) ((float) (color & 255) * shade);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static void renderLineBox(PoseStack poseStack, VertexConsumer consumer,
                                     Vec3 center, float size,
                                     float red, float green, float blue, float alpha) {

        double half = size / 2.0;

        //? if >=1.21.11 {
        /*Gizmos.cuboid(new AABB( center.x - half, center.y - half, center.z - half,
                        center.x + half, center.y + half, center.z + half
                        ), GizmoStyle.stroke(new Color(red,green,blue,alpha).getRGB()));
        *///?} else {
            //? if >1.21.1 {
            ShapeRenderer
            //?} else {
            /*LevelRenderer
            *///?}
                    .renderLineBox(
                    poseStack/*? if >=1.21.9 {*//*.last()*//*?}*/, consumer,
                    center.x - half, center.y - half, center.z - half,
                    center.x + half, center.y + half, center.z + half,
                    red, green, blue,
                    alpha, red, green,
                    blue
            );
        //?}
    }

    public static void renderBillboardFrame(
            PoseStack poseStack,
            VertexConsumer vc,
            Vec3 vec3,
            float size,
            float r, float g, float b, float a
    ) {
        poseStack.pushPose();
        poseStack.translate(
            //? if >1.21.1 {
            vec3
            //?} else
            /*vec3.x,vec3.y,vec3.z*/
        );

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        poseStack.mulPose( /*? if <=1.18.2 {*/ /*com.mojang.math.Vector3f *//*?} else {*/Axis/*?}*/.YP.rotationDegrees(-camera/*? if >=1.21.11 {*//*.yRot()*//*?} else {*/.getYRot()/*?}*/));
        poseStack.mulPose( /*? if <=1.18.2 {*/ /*com.mojang.math.Vector3f *//*?} else {*/Axis/*?}*/.XP.rotationDegrees(camera/*? if >=1.21.11 {*//*.xRot()*//*?} else {*/.getXRot()/*?}*/));

        PoseStack.Pose pose = poseStack.last();

        float s = size / 2f;

        Vec3 v1 = new Vec3(-s, -s, 0);
        Vec3 v2 = new Vec3(s, -s, 0);
        Vec3 v3 = new Vec3(s, s, 0);
        Vec3 v4 = new Vec3(-s, s, 0);

        Vec3 n = new Vec3(0, 0, -1);

        addLine(pose, vc, v1, v2, r, g, b, a, n);
        addLine(pose, vc, v2, v3, r, g, b, a, n);
        addLine(pose, vc, v3, v4, r, g, b, a, n);
        addLine(pose, vc, v4, v1, r, g, b, a, n);
        poseStack.popPose();
    }
    private static void addLine(PoseStack.Pose pose, VertexConsumer vc, Vec3 a, Vec3 b, float r, float g, float b2, float a2, Vec3 normal) {
        //? if > 1.20.6 {
        vc.addVertex(pose, (float) a.x, (float) a.y, (float) a.z).setColor(r, g, b2, a2).setNormal(pose, (float) normal.x, (float) normal.y, (float) normal.z);
        vc.addVertex(pose, (float) b.x, (float) b.y, (float) b.z).setColor(r, g, b2, a2).setNormal(pose, (float) normal.x, (float) normal.y, (float) normal.z);
        //?} else {
        /*vc.vertex(pose.pose(), (float) a.x, (float) a.y, (float) a.z).color(r, g, b2, a2).normal(pose
                /^? if < 1.20.6 {^//^.normal()^//^?}^/, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
        vc.vertex(pose.pose(), (float) b.x, (float) b.y, (float) b.z).color(r, g, b2, a2).normal(pose
                /^? if < 1.20.6 {^//^.normal()^//^?}^/, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
        *///?}
    }
    //? if >1.18.2 {

    public static Matrix4f convertToMojangIfNeeded(Matrix4f matrix4f2){
    return matrix4f2;
    }

    public static org.joml.Matrix4f convertToJomlIfNeeded(org.joml.Matrix4f  matrix4f){
        return matrix4f;
    }
    //?} else {

    /*public static org.joml.Matrix4f convertToJomlIfNeeded(com.mojang.math.Matrix4f matrix4f){
            return  new Matrix4f(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m03,
                    matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m13,
                    matrix4f.m20, matrix4f.m21, matrix4f.m22, matrix4f.m23,
                    matrix4f.m30, matrix4f.m31,  matrix4f.m32, matrix4f.m33);
    }
    public static com.mojang.math.Matrix4f convertToMojangIfNeeded(Matrix4f matrix4f2){
        com.mojang.math.Matrix4f matrix4f = new com.mojang.math.Matrix4f();

        matrix4f.m00 = matrix4f2.m00();
        matrix4f.m01 = matrix4f2.m01();
        matrix4f.m02 = matrix4f2.m02();
        matrix4f.m03 = matrix4f2.m03();
        matrix4f.m10 = matrix4f2.m10();
        matrix4f.m11 = matrix4f2.m11();
        matrix4f.m12 = matrix4f2.m12();
        matrix4f.m13 = matrix4f2.m13();
        matrix4f.m20 = matrix4f2.m20();
        matrix4f.m21 = matrix4f2.m21();
        matrix4f.m22 = matrix4f2.m22();
        matrix4f.m23 = matrix4f2.m23();
        matrix4f.m30 = matrix4f2.m30();
        matrix4f.m31 = matrix4f2.m31();
        matrix4f.m32 = matrix4f2.m32();
        matrix4f.m33 = matrix4f2.m33();

        return matrix4f;
    }
    *///?}
}
