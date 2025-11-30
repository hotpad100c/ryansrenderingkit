package mypals.ml.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static mypals.ml.RyansRenderingKit.MOD_ID;

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

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f view = new Matrix4f();

        Quaternionf camRot = camera.rotation();
        Quaternionf camRotInv = new Quaternionf(camRot).conjugate();
        view.rotation(camRotInv);

        Vec3 camPos = camera.getPosition();
        view.translate(new Vector3f(
                (float) -camPos.x,
                (float) -camPos.y,
                (float) -camPos.z
        ));

        return view;
    }

    public static boolean isVertexInFrustum(Vec3 v, Matrix4f mvp) {
        Vector4f clip = new Vector4f((float) v.x, (float) v.y, (float) v.z, 1f);
        clip.mul(mvp);
        if (clip.w <= 0) return false;
        float ndcX = clip.x / clip.w;
        float ndcY = clip.y / clip.w;
        float ndcZ = clip.z / clip.w;

        return ndcX >= -1 && ndcX <= 1 &&
                ndcY >= -1 && ndcY <= 1 &&
                ndcZ >= -1 && ndcZ <= 1;
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

        ShapeRenderer.renderLineBox(
                poseStack, consumer,
                center.x - half, center.y - half, center.z - half,
                center.x + half, center.y + half, center.z + half,
                red, green, blue,
                alpha, red, green,
                blue
        );
    }

    public static void renderBillboardFrame(
            PoseStack poseStack,
            VertexConsumer vc,
            Vec3 vec3,
            float size,
            float r, float g, float b, float a
    ) {
        poseStack.pushPose();
        poseStack.translate(vec3);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));

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
        vc.addVertex(pose, (float) a.x, (float) a.y, (float) a.z).setColor(r, g, b2, a2).setNormal(pose, (float) normal.x, (float) normal.y, (float) normal.z);
        vc.addVertex(pose, (float) b.x, (float) b.y, (float) b.z).setColor(r, g, b2, a2).setNormal(pose, (float) normal.x, (float) normal.y, (float) normal.z);
    }
}
