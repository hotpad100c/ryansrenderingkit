package mypals.ml.builderManager;

import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builders.vertexBuilders.*;
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static mypals.ml.shapeManagers.ShapeManager.SHAPE_ORDER_COMPARATOR;

public class EmptyBuilderManager{
    public String id;
    public EmptyVertexBuilder emptyVertexBuilder;;
    public EmptyBuilderManager(Matrix4f matrix4f, String id){
        this.id = id;
        emptyVertexBuilder = new EmptyVertexBuilder(matrix4f);
    }

    public void draw(Consumer<VertexBuilder> builder){
        emptyVertexBuilder.draw(builder);
    }
    public void updateMatrix(Matrix4f modelViewMatrix){
        emptyVertexBuilder.setPositionMatrix(modelViewMatrix);
    }
}

