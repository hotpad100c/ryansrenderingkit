package mypals.ml.interfaces;

//? if > 1.20.1
/*import com.mojang.blaze3d.vertex.ByteBufferBuilder;*/
import com.mojang.blaze3d.vertex.VertexSorting;

public interface MeshDataExt {
    void ryansrenderingkit$sortTriangles(/*? if >1.20.1 {*/ /*ByteBufferBuilder byteBufferBuilder,  *//*?}*/VertexSorting vertexSorting);

    interface BufferBuilderSortableExt {
        //? if >1.20.1 {
        /*ByteBufferBuilder
        .Result
        *///?} else
        void
        ryansrenderingkit$buildSortedIndexBufferTriangles(/*? if >1.20.1 {*/ /*ByteBufferBuilder byteBufferBuilder,  *//*?}*/ VertexSorting vertexSorting);
    }
}
