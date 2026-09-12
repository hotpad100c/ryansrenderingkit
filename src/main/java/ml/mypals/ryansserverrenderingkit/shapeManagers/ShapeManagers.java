package ml.mypals.ryansserverrenderingkit.shapeManagers;

import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.tags.ExtractableShape;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;

import static ml.mypals.ryansserverrenderingkit.utils.Helpers.generateUniqueId;

public class ShapeManagers {
    public static final String TEMP_HEADER = "temp_shape";

    public static ShapeManager DEFAULT_SHAPE_MANAGER;
    public static ShapeManager LINES_SHAPE_MANAGER;
    public static ShapeManager LINE_STRIP_SHAPE_MANAGER;
    public static ShapeManager TRIANGLES_SHAPE_MANAGER;
    public static ShapeManager NON_SHAPE_OBJECTS;

    public static final List<ShapeManager> managers = new ArrayList<>();

    public static void init() {
        DEFAULT_SHAPE_MANAGER = register("default_shape_manager");
        LINES_SHAPE_MANAGER = register("lines_shape_manager");
        LINE_STRIP_SHAPE_MANAGER = register("line_strip_shape_manager");
        TRIANGLES_SHAPE_MANAGER = register("triangles_shape_manager");
        NON_SHAPE_OBJECTS = register("empty");

        ServerTickEvents.END_SERVER_TICK.register(ShapeManagers::tick);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ShapeManagers.clear());
    }

    public static ShapeManager register(String id) {
        ShapeManager shapeManager = new ShapeManager(id);
        managers.add(shapeManager);
        return shapeManager;
    }

    public static void tick(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        for (ShapeManager manager : managers) {
            manager.tick(overworld);
        }
    }

    public static void removeShape(Identifier identifier) {
        managers.forEach(shapeManager -> shapeManager.removeShape(identifier));
    }

    public static void removeShapes(Identifier root) {
        managers.forEach(shapeManager -> shapeManager.removeShapes(root));
    }

    public static void addShape(Identifier identifier, Shape shape) {
        shape.setId(identifier);
        if (shape instanceof ExtractableShape exts) {
            exts.addGroup(identifier);
            return;
        }
        DEFAULT_SHAPE_MANAGER.addShape(identifier, shape);
    }

    public static void addShape(Shape shape) {
        addShape(generateUniqueId(TEMP_HEADER), shape);
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
