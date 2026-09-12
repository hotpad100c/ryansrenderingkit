package ml.mypals.ryansserverrenderingkit.shapeManagers;

import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.tags.ExtractableShape;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static ml.mypals.ryansserverrenderingkit.utils.Helpers.generateUniqueId;

public class ShapeManagers {
    public static final String TEMP_HEADER = "temp_shape";

    /**
     * Unified ShapeManager for all shapes in the server-side DisplayEntity rendering system.
     * In server-side rendering, OpenGL pipelines (lines, line_strip, triangles, etc.) do not exist,
     * so all shapes are handled cleanly by this single manager.
     */
    public static final ShapeManager INSTANCE = new ShapeManager("shapes");

    // Backwards-compatibility aliases all pointing to the single unified manager
    public static final ShapeManager DEFAULT_SHAPE_MANAGER = INSTANCE;
    @Deprecated
    public static final ShapeManager LINES_SHAPE_MANAGER = INSTANCE;
    @Deprecated
    public static final ShapeManager LINE_STRIP_SHAPE_MANAGER = INSTANCE;
    @Deprecated
    public static final ShapeManager TRIANGLES_SHAPE_MANAGER = INSTANCE;
    @Deprecated
    public static final ShapeManager NON_SHAPE_OBJECTS = INSTANCE;

    public static final List<ShapeManager> managers = new CopyOnWriteArrayList<>(Collections.singletonList(INSTANCE));

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(ShapeManagers::tick);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ShapeManagers.clear());
    }

    public static ShapeManager register(String id) {
        ShapeManager shapeManager = new ShapeManager(id);
        managers.add(shapeManager);
        return shapeManager;
    }

    public static ShapeManager getInstance() {
        return INSTANCE;
    }

    public static void tick(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        for (ShapeManager manager : managers) {
            manager.tick(overworld);
        }
    }

    public static void removeShape(Identifier identifier) {
        for (ShapeManager manager : managers) {
            manager.removeShape(identifier);
        }
    }

    public static void removeShapes(Identifier root) {
        for (ShapeManager manager : managers) {
            manager.removeShapes(root);
        }
    }

    public static void addShape(Identifier identifier, Shape shape) {
        shape.setId(identifier);
        if (shape instanceof ExtractableShape exts) {
            exts.addGroup(identifier);
            return;
        }
        INSTANCE.addShape(identifier, shape);
    }

    public static void addShape(Shape shape) {
        addShape(generateUniqueId(TEMP_HEADER), shape);
    }

    public static Shape getShape(Identifier identifier) {
        return INSTANCE.getShape(identifier);
    }

    public static Collection<Shape> getAllShapes() {
        return INSTANCE.getAllShapes();
    }

    public static int getShapeCount() {
        return INSTANCE.getShapeCount();
    }

    public static void syncShapeTransform() {
        for (ShapeManager manager : managers) {
            manager.syncShapeTransform();
        }
    }

    public static void clear() {
        for (ShapeManager manager : managers) {
            manager.clear();
        }
    }
}

