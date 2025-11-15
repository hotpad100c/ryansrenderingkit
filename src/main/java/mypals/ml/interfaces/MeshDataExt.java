package mypals.ml.interfaces;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexSorting;

public interface MeshDataExt {
    ByteBufferBuilder ryansrenderingkit$sortTriangles(VertexSorting vertexSorting);
    public interface MeshDataSortableExt {
        ByteBufferBuilder.Result ryansrenderingkit$buildSortedIndexBufferTriangles(ByteBufferBuilder byteBufferBuilder, VertexSorting vertexSorting);
    }
}
