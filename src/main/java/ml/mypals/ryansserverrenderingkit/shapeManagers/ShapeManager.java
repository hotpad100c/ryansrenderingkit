package ml.mypals.ryansserverrenderingkit.shapeManagers;

import ml.mypals.ryansserverrenderingkit.shape.Shape;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

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
        Predicate<Map.Entry<Identifier, Shape>> pred = entry ->
                entry.getKey().getNamespace().equals(namespace)
                        && entry.getKey().getPath().startsWith(path);

        boolean removed = false;
        for (Map.Entry<Identifier, Shape> entry : shapeMap.entrySet()) {
            if (pred.test(entry)) {
                if (shapeMap.remove(entry.getKey()) != null) {
                    entry.getValue().removeDisplays();
                    removed = true;
                }
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
