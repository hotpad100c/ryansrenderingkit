package ml.mypals.ryansserverrenderingkit.shapeManagers;

import ml.mypals.ryansserverrenderingkit.shape.Shape;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ShapeManager {
    public final String id;
    public final ConcurrentHashMap<Identifier, Shape> shapeMap = new ConcurrentHashMap<>();

    public ShapeManager(String id) {
        this.id = id;
    }

    public void addShape(Identifier identifier, Shape shape) {
        shape.setId(identifier);
        shapeMap.put(identifier, shape);
    }

    @Nullable
    public Shape getShape(Identifier identifier) {
        return shapeMap.get(identifier);
    }

    public Collection<Shape> getAllShapes() {
        return shapeMap.values();
    }

    public int getShapeCount() {
        return shapeMap.size();
    }

    public boolean removeShape(@NotNull Identifier identifier) {
        Shape shape = shapeMap.remove(identifier);
        if (shape != null) {
            shape.removeDisplays();
            return true;
        }
        return false;
    }

    public boolean removeShapes(@NotNull Identifier identifier) {
        String namespace = identifier.getNamespace();
        String path = identifier.getPath();

        List<Identifier> toRemove = new ArrayList<>();
        for (Identifier key : shapeMap.keySet()) {
            if (key.getNamespace().equals(namespace) && key.getPath().startsWith(path)) {
                toRemove.add(key);
            }
        }

        boolean removed = false;
        for (Identifier key : toRemove) {
            Shape shape = shapeMap.remove(key);
            if (shape != null) {
                shape.removeDisplays();
                removed = true;
            }
        }
        return removed;
    }

    public void syncShapeTransform() {
        shapeMap.values().forEach(Shape::syncLastToTarget);
    }

    public void tick(ServerLevel level) {
        for (Shape shape : shapeMap.values()) {
            shape.tick(level);
        }
    }

    public void clear() {
        for (Shape shape : shapeMap.values()) {
            shape.removeDisplays();
        }
        shapeMap.clear();
    }
}

