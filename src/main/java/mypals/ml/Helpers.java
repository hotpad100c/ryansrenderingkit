package mypals.ml;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Camera;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static mypals.ml.RyansRenderingKit.MOD_ID;

public class Helpers {
    public static ResourceLocation generateUniqueId(String prefix) {
        long timestamp = System.currentTimeMillis();
        int randomNum = ThreadLocalRandom.current().nextInt(10000);
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,prefix.toLowerCase() +"_"+ timestamp + "_" + randomNum);
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
        Vector4f clip = new Vector4f((float)v.x, (float)v.y, (float)v.z, 1f);
        clip.mul(mvp);
        if (clip.w <= 0) return false;
        float ndcX = clip.x / clip.w;
        float ndcY = clip.y / clip.w;
        float ndcZ = clip.z / clip.w;

        return ndcX >= -1 && ndcX <= 1 &&
                ndcY >= -1 && ndcY <= 1 &&
                ndcZ >= -1  && ndcZ <= 1;
    }
}
