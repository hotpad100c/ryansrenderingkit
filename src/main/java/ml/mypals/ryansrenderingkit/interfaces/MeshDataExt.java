package ml.mypals.ryansrenderingkit.interfaces;

//? if > 1.20.6
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
//? if > 1.19.4
import com.mojang.blaze3d.vertex.VertexSorting;

public interface MeshDataExt {
    void ryansrenderingkit$sortTriangles(/*? if >1.20.6 {*/ ByteBufferBuilder byteBufferBuilder,  /*?}*/ /*? if >1.19.4 {*/VertexSorting vertexSorting/*?}*/);

    interface BufferBuilderSortableExt {
        //? if >1.20.6 {
        ByteBufferBuilder
        .Result
        //?} else
        //void
        ryansrenderingkit$buildSortedIndexBufferTriangles(/*? if >1.20.6 {*/ ByteBufferBuilder byteBufferBuilder,  /*?}*/ /*? if >1.19.4 {*/VertexSorting vertexSorting/*?}*/);
    }
}
