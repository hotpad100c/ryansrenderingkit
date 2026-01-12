package ml.mypals.ryansrenderingkit.mixin;

import com.mojang.blaze3d.vertex.*;
import org.spongepowered.asm.mixin.Mixin;
import ml.mypals.ryansrenderingkit.interfaces.MeshDataExt;
//? if >1.20.6 {

//? if >1.18.2 {
import org.joml.Vector3f;

//?} else {
/*import com.mojang.math.Vector3f;
*/
//?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.IntConsumer;
//?}
@Mixin(MeshData.SortState.class)
public abstract class SortStateMixin implements MeshDataExt.BufferBuilderSortableExt {
    //? if >1.20.6 {

    @Shadow
    protected abstract it.unimi.dsi.fastutil.ints.IntConsumer indexWriter(long l, VertexFormat.IndexType indexType);

    @Shadow
    @Final
    private VertexFormat.IndexType indexType;

    @Shadow
    @Final
    private /*? if <1.21.9 {*/Vector3f[]/*?} else {*/ /*CompactVectorArray*//*?}*/centroids;

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