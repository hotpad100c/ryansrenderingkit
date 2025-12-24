package mypals.ml.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mypals.ml.interfaces.MeshDataExt;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.function.IntConsumer;

import static mypals.ml.RyansRenderingKit.RENDER_PROFILER;

@Mixin(BufferBuilder.class)
public abstract class MeshDataMixin implements MeshDataExt {

    //? if >1.20.6 {
    /*@Shadow
    @Nullable
    private ByteBufferBuilder.Result indexBuffer;

    @Shadow
    @Final
    private ByteBufferBuilder.Result vertexBuffer;

    @Shadow
    @Final
    private BufferBuilder.DrawState drawState;

    *///?} else {


    @Shadow @Final private VertexFormat.Mode mode;

    @Shadow public int vertices;

    @Shadow private boolean indexOnly;

    @Shadow @Final private VertexFormat format;

    @Shadow private Vector3f[] sortingPoints;

    @Shadow protected abstract void ensureCapacity(int par1);

    @Shadow public int nextElementByte;

    @Shadow private int renderedBufferPointer;

    @Shadow private int renderedBufferCount;

    @Shadow private VertexSorting sorting;

    @Shadow protected abstract Vector3f[] makeQuadSortingPoints();

    @Shadow private ByteBuffer buffer;
    //?}


    //? if > 1.20.6 {
    /*@Unique
    private static

    //? if > 1.20.6 && <1.21.9 {
    /^Vector3f[]
    ^///?} else if >= 1.21.9 {
    /^CompactVectorArray
    ^///?}
    unpackTriangleCentroids(ByteBuffer byteBuffer, int vertexCount, VertexFormat vertexFormat) {
        int posOffset = vertexFormat.getOffset(VertexFormatElement.POSITION);
        if (posOffset == -1) {
            throw new IllegalArgumentException("Cannot identify triangle centers with no position element");
        }

        FloatBuffer fb = byteBuffer.asFloatBuffer();
        int floatsPerVertex = vertexFormat.getVertexSize() / 4;
        int triangles = vertexCount / 3;
        /^? if < 1.21.9 {^/Vector3f[]/^?} else {^//^CompactVectorArray^//^?}^/centroids = new
        /^? if < 1.21.9 {^/Vector3f[triangles]/^?} else {^//^CompactVectorArray(triangles)^//^?}^/;

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
            //? >=1.21.9 {
            /^centroids.set(t, (x0 + x1 + x2) / 3f, (y0 + y1 + y2) / 3f, (z0 + z1 + z2) / 3f);
            ^///?} else {
            centroids[t] = new Vector3f((x0 + x1 + x2) / 3f, (y0 + y1 + y2) / 3f, (z0 + z1 + z2) / 3f);
            //?}
        }

        return centroids;
    }


    @Unique
    public void ryansrenderingkit$sortTriangles(
            ByteBufferBuilder byteBufferBuilder,
            VertexSorting vertexSorting) {
        RENDER_PROFILER.push("sortMesh");
        //? if < 1.21.9 {
        Vector3f[]
         //?} else {
        /^CompactVectorArray
        ^///?}

                compactVectorArray = unpackTriangleCentroids(this.vertexBuffer.byteBuffer(), this.drawState.vertexCount(), this.drawState.format());

        BufferBuilder.SortState sortState = new BufferBuilder.SortState(compactVectorArray, this.drawState.indexType());
        this.indexBuffer = ((BufferBuilderSortableExt) (Object) sortState).ryansrenderingkit$buildSortedIndexBufferTriangles(byteBufferBuilder, vertexSorting);
        RENDER_PROFILER.pop();
    }
    *///?} else {
    @Inject(method = "storeRenderedBuffer",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;least(I)Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;"
            )
    )

    private void storeRenderedBuffer(CallbackInfoReturnable<BufferBuilder.RenderedBuffer> cir) {
        if(this.mode == VertexFormat.Mode.TRIANGLES) {
            if (this.sortingPoints == null) {
                this.sortingPoints = this.makeTriangleSortingPoints();
            }
            if(this.sorting == null) {
                this.sorting = RenderSystem.getVertexSorting();
            }
        }
    }
    @WrapOperation(
            method = "storeRenderedBuffer",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;putSortedQuadIndices(Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V"
            )
    )
    private void putSortedQuadIndices(BufferBuilder instance, VertexFormat.IndexType indexType, Operation<Void> original) {
        if(this.mode == VertexFormat.Mode.TRIANGLES) {
            RENDER_PROFILER.push("sortMesh");
            ryansrenderingkit$buildSortedIndexBufferTriangles(indexType);
            RENDER_PROFILER.pop();
        } else {
            original.call(instance, indexType);
        }
    }
    @Unique
    private Vector3f[] makeTriangleSortingPoints() {
        FloatBuffer floatBuffer = this.buffer.asFloatBuffer();

        int base = this.renderedBufferPointer / 4;
        int vertexSize = this.format.getIntegerSize();
        int stride = vertexSize * this.mode.primitiveStride;
        int primitiveCount = this.vertices / this.mode.primitiveStride;

        Vector3f[] centers = new Vector3f[primitiveCount];

        for (int p = 0; p < primitiveCount; p++) {

            int offset0 = base + p * stride;
            int offset1 = offset0 + vertexSize;
            int offset2 = offset0 + vertexSize * 2;

            float x0 = floatBuffer.get(offset0);
            float y0 = floatBuffer.get(offset0 + 1);
            float z0 = floatBuffer.get(offset0 + 2);

            float x1 = floatBuffer.get(offset1);
            float y1 = floatBuffer.get(offset1 + 1);
            float z1 = floatBuffer.get(offset1 + 2);

            float x2 = floatBuffer.get(offset2);
            float y2 = floatBuffer.get(offset2 + 1);
            float z2 = floatBuffer.get(offset2 + 2);

            float cx = (x0 + x1 + x2) / 3f;
            float cy = (y0 + y1 + y2) / 3f;
            float cz = (z0 + z1 + z2) / 3f;

            centers[p] = new Vector3f(cx, cy, cz);
        }

        return centers;
    }

    @Unique
    public void ryansrenderingkit$buildSortedIndexBufferTriangles(VertexFormat.IndexType indexType) {
        if (this.sortingPoints != null && this.sorting != null) {
            int[] sortedTriangleIndices = this.sorting.sort(this.sortingPoints);
            BufferBuilder builder = (BufferBuilder)(Object)this;
            IntConsumer intConsumer = builder.intConsumer(builder.nextElementByte, indexType);

            for (int i : sortedTriangleIndices) {
                int baseVertex = i * 3;
                intConsumer.accept(baseVertex);
                intConsumer.accept(baseVertex + 1);
                intConsumer.accept(baseVertex + 2);
            }

        }
    }
    //?}
}
