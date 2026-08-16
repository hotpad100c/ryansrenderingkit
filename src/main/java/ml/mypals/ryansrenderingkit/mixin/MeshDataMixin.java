package ml.mypals.ryansrenderingkit.mixin;

import com.google.common.primitives.Floats;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.IntArrays;
import ml.mypals.ryansrenderingkit.interfaces.MeshDataExt;
import org.jetbrains.annotations.Nullable;
//? if >1.18.2 {
import org.joml.Vector3f;
//?} else {
/*import com.mojang.math.Vector3f;
*///?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

//? if <=1.20.6 {
/*//? if >1.19.4 {
import com.mojang.blaze3d.systems.RenderSystem;
//?}
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.IntConsumer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.*;
import ml.mypals.ryansrenderingkit.interfaces.MeshDataExt;
*///?}
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static ml.mypals.ryansrenderingkit.RyansRenderingKit.RENDER_PROFILER;

@Mixin(MeshData.class)
public abstract class MeshDataMixin implements MeshDataExt {

    //? if >1.20.6 {
    @Shadow
    @Nullable
    private ByteBufferBuilder.Result indexBuffer;

    @Shadow
    @Final
    private ByteBufferBuilder.Result vertexBuffer;

    @Shadow
    @Final
    private MeshData.DrawState drawState;

    //?} else {


    /*@Shadow @Final private VertexFormat.Mode mode;

    @Shadow public int vertices;

    @Shadow private boolean indexOnly;

    @Shadow @Final private VertexFormat format;

    @Shadow protected abstract void ensureCapacity(int par1);

    @Shadow public int nextElementByte;

    //? if >1.18.2 {
    @Shadow private int renderedBufferPointer;

    @Shadow private int renderedBufferCount;
    //?} else {
    /^@Shadow private int totalRenderedBytes;
    @Shadow public abstract it.unimi.dsi.fastutil.ints.IntConsumer intConsumer(VertexFormat.IndexType par1);
    ^///?}
    //? if >1.19.4 {
    @Shadow private VertexSorting sorting;
    //?}

    @Shadow private ByteBuffer buffer;
    *///?}

    //? if <=1.19.4 {
    /*@Shadow private float sortX;

    @Shadow private float sortY;

    @Shadow private float sortZ;
    *///?}

    //? if > 1.20.6 {
    @Unique
    private static

        //? if > 1.20.6 && <1.21.9 {
        /*Vector3f[]
        *///?} else if >= 1.21.9 {
        CompactVectorArray
        //?}
    unpackTriangleCentroids(ByteBuffer byteBuffer, int vertexCount, VertexFormat vertexFormat) {
        VertexFormatElement positionElement = vertexFormat.getElement("Position");
        if (positionElement == null) {
            throw new IllegalArgumentException("Cannot identify triangle centers with no position element");
        }
        int posOffset = byteBuffer.position() + positionElement.offset();

        FloatBuffer fb = byteBuffer.asFloatBuffer();
        int floatsPerVertex = vertexFormat.getVertexSize() / 4;
        int triangles = vertexCount / 3;
        /*? if < 1.21.9 {*//*Vector3f[]*//*?} else {*/CompactVectorArray/*?}*/centroids = new
        /*? if < 1.21.9 {*//*Vector3f[triangles]*//*?} else {*/CompactVectorArray(triangles)/*?}*/;

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
            centroids.set(t, (x0 + x1 + x2) / 3f, (y0 + y1 + y2) / 3f, (z0 + z1 + z2) / 3f);
            //?} else {
            /*centroids[t] = new Vector3f((x0 + x1 + x2) / 3f, (y0 + y1 + y2) / 3f, (z0 + z1 + z2) / 3f);
            *///?}
        }

        return centroids;
    }


    @Unique
    public void ryansrenderingkit$sortTriangles(
            ByteBufferBuilder byteBufferBuilder,
            VertexSorting vertexSorting) {
        RENDER_PROFILER.push("sortMesh");
        //? if < 1.21.9 {
        /*Vector3f[]
         *///?} else {
        CompactVectorArray
        //?}

                compactVectorArray = unpackTriangleCentroids(this.vertexBuffer.byteBuffer(), this.drawState.vertexCount(), this.drawState.format());

        MeshData.SortState sortState = new MeshData.SortState(compactVectorArray, this.drawState.indexType());
        this.indexBuffer = ((BufferBuilderSortableExt) (Object) sortState).ryansrenderingkit$buildSortedIndexBufferTriangles(byteBufferBuilder, vertexSorting);
        RENDER_PROFILER.pop();
    }
    //?} else {

    /*@Shadow private Vector3f[] sortingPoints;

    //? if <=1.18.2 {
    /^@Inject(
            method = "setQuadSortOrigin",
            at = @At("TAIL")
    )
    private void storeRenderedBuffer2(CallbackInfo ci) {
        if(this.mode == VertexFormat.Mode.TRIANGLES) {
            if(this.sortingPoints == null) {
                this.sortingPoints = this.makeTriangleSortingPoints();
            }
        }
    }
    ^///?}


    @Inject(
            //? if >1.18.2 {
            method = "storeRenderedBuffer",
            //?} else {
            /^method = {"end"},
            ^///?}
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;least(I)Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;"
            )
    )
    //? if >1.18.2 {
    private void storeRenderedBuffer(CallbackInfoReturnable<BufferBuilder.RenderedBuffer> cir) {
    //?} else {
    /^private void storeRenderedBuffer(CallbackInfo ci) {
    ^///?}
        if(this.mode == VertexFormat.Mode.TRIANGLES) {

            if(this.sortingPoints == null) {
                this.sortingPoints = this.makeTriangleSortingPoints();
            }
            //? if >1.19.4 {
            if(this.sorting == null) {
                this.sorting = RenderSystem.getVertexSorting();
            }
            //?}
        }
    }
    @WrapOperation(
        //? if >1.18.2 {
        method = "storeRenderedBuffer",
        //?} else {
        /^method = "end",
        ^///?}
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
        //? if >1.18.2 {
        int base = this.renderedBufferCount / 4;
        //?} else {
        /^int base = this.totalRenderedBytes / 4;
        ^///?}

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
        if (this.sortingPoints != null/^? if > 1.19.4 {^/ && this.sorting != null/^?}^/) {
            //? if >1.19.4 {
            int[] sortedTriangleIndices = this.sorting.sort(this.sortingPoints);
            //?} else {
                /^float[] fs = new float[this.sortingPoints.length];
                int[] sortedTriangleIndices = new int[this.sortingPoints.length];

                for(int i = 0; i < this.sortingPoints.length; sortedTriangleIndices[i] = i++) {
                    float f = this.sortingPoints[i].x() - this.sortX;
                    float g = this.sortingPoints[i].y() - this.sortY;
                    float h = this.sortingPoints[i].z() - this.sortZ;
                    fs[i] = f * f + g * g + h * h;
                }
                IntArrays.mergeSort(sortedTriangleIndices, (i, jx) -> Floats.compare(fs[jx], fs[i]));
            ^///?}
            BufferBuilder builder = (BufferBuilder)(Object)this;
            IntConsumer intConsumer = /^? if >1.18.2 {^/builder./^?} else {^//^this.^//^?}^/
                    intConsumer(/^? if >1.18.2 {^/builder.nextElementByte,/^?}^/ indexType);

            //? if <=1.18.2 {
            /^this.buffer.position(this.nextElementByte);
            ^///?}

            for (int i : sortedTriangleIndices) {
                int baseVertex = i * 3;
                intConsumer.accept(baseVertex);
                intConsumer.accept(baseVertex + 1);
                intConsumer.accept(baseVertex + 2);
            }
        }
    }
    *///?}
}
