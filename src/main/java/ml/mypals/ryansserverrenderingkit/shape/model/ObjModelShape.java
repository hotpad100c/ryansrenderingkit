package ml.mypals.ryansserverrenderingkit.shape.model;

import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ObjModelShape extends Shape {

    public Vec3 modelCenter = Vec3.ZERO;
    public SimpleOBJModel model;

    public ObjModelShape(Consumer<DefaultTransformer> transform,
                         Identifier resourceLocation,
                         Vec3 center,
                         Color color) {
        this(transform, resourceLocation, center, color, false);
    }

    public ObjModelShape(Consumer<DefaultTransformer> transform,
                         Identifier resourceLocation,
                         Vec3 center,
                         Color color,
                         boolean seeThrough) {
        super(transform, color, center, seeThrough);
        this.transformer.setShapeWorldPivot(center);
        syncLastToTarget();
    }

    @Deprecated
    public ObjModelShape(Object ignored,
                         Consumer<DefaultTransformer> transform,
                         Identifier resourceLocation,
                         Vec3 center,
                         Color color) {
        this(transform, resourceLocation, center, color, false);
    }

    @Deprecated
    public ObjModelShape(Object ignored,
                         Consumer<DefaultTransformer> transform,
                         Identifier resourceLocation,
                         Vec3 center,
                         Color color,
                         boolean seeThrough) {
        this(transform, resourceLocation, center, color, seeThrough);
    }

    public static class SimpleOBJModel {
        public final ArrayList<Vec3> vertices = new ArrayList<>();
        public final ArrayList<int[]> faces = new ArrayList<>();
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        // Stubbed
    }

    @Override
    public void initDisplays(ServerLevel level) {
        // OBJ models are temporarily stubbed in server-side display mode
    }

    @Override
    public void updateDisplays() {
        // OBJ models are temporarily stubbed in server-side display mode
    }
}
