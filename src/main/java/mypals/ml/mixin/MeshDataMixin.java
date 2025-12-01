package mypals.ml.mixin;

import com.mojang.blaze3d.vertex.*;
import mypals.ml.interfaces.MeshDataExt;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.function.IntConsumer;

import static mypals.ml.RyansRenderingKit.RENDER_PROFILER;

@Mixin(MeshData.class)
public abstract class MeshDataMixin implements MeshDataExt {
    @Shadow
    @Nullable
    private ByteBufferBuilder.Result indexBuffer;

    @Shadow
    @Final
    private ByteBufferBuilder.Result vertexBuffer;

    @Shadow
    @Final
    private MeshData.DrawState drawState;

    @Unique
    private static Vector3f[] unpackTriangleCentroids(ByteBuffer byteBuffer, int vertexCount, VertexFormat vertexFormat) {
        int posOffset = vertexFormat.getOffset(VertexFormatElement.POSITION);
        if (posOffset == -1) {
            throw new IllegalArgumentException("Cannot identify triangle centers with no position element");
        }

        FloatBuffer fb = byteBuffer.asFloatBuffer();
        int floatsPerVertex = vertexFormat.getVertexSize() / 4;
        int triangles = vertexCount / 3;
        Vector3f[] centroids = new Vector3f[triangles];

        for (int t = 0; t < triangles; t++) {
            int base = t * floatsPerVertex * 3 + posOffset;
            float x0 = fb.get(base);
            float y0 = fb.get(base + 1);
            float z0 = fb.get(base + 2);

            int b1 = base + floatsPerVertex;
            float x1 = fb.get(b1);
            float y1 = fb.get(b1 + 1);
            float z1 = fb.get(b1 + 2);

            int b2 = base + floatsPerVertex * 2;
            float x2 = fb.get(b2);
            float y2 = fb.get(b2 + 1);
            float z2 = fb.get(b2 + 2);

            centroids[t] = new Vector3f((x0 + x1 + x2) / 3f, (y0 + y1 + y2) / 3f, (z0 + z1 + z2) / 3f);
        }

        return centroids;
    }


    @Unique
    public void ryansrenderingkit$sortTriangles(ByteBufferBuilder byteBufferBuilder, VertexSorting vertexSorting) {
        RENDER_PROFILER.push("sortMesh");
        Vector3f[] compactVectorArray = unpackTriangleCentroids(this.vertexBuffer.byteBuffer(), this.drawState.vertexCount(), this.drawState.format());
        MeshData.SortState sortState = new MeshData.SortState(compactVectorArray, this.drawState.indexType());
        this.indexBuffer = ((MeshDataSortableExt) (Object) sortState).ryansrenderingkit$buildSortedIndexBufferTriangles(byteBufferBuilder, vertexSorting);
        RENDER_PROFILER.pop();
    }
}
