package mypals.ml.mixin;

//? if >1.20.1 {
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
//?}
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import mypals.ml.interfaces.MeshDataExt;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.IntConsumer;

@Mixin(MeshData.SortState.class)
public abstract class SortStateMixin implements MeshDataExt.BufferBuilderSortableExt {
    //? if >1.20.1 {

    @Shadow
    protected abstract it.unimi.dsi.fastutil.ints.IntConsumer indexWriter(long l, VertexFormat.IndexType indexType);

    @Shadow
    @Final
    private VertexFormat.IndexType indexType;

    @Shadow
    @Final
    private Vector3f[] centroids;

    @Unique
    public ByteBufferBuilder.Result
    ryansrenderingkit$buildSortedIndexBufferTriangles(
            ByteBufferBuilder byteBufferBuilder,
            VertexSorting vertexSorting) {
        int[] sortedTriangleIndices = vertexSorting.sort(this.centroids);

        long offset = byteBufferBuilder.reserve(sortedTriangleIndices.length * 3 * this.indexType.bytes);
        IntConsumer intConsumer = this.indexWriter(offset, this.indexType);
        for (int i : sortedTriangleIndices) {
            int baseVertex = i * 3;
            intConsumer.accept(baseVertex);
            intConsumer.accept(baseVertex + 1);
            intConsumer.accept(baseVertex + 2);
        }
        return byteBufferBuilder.build();
    }
    
    //?}
}