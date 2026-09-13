package ml.mypals.ryansserverrenderingkit.shape;

import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shapeManagers.ShapeManagers;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static ml.mypals.ryansserverrenderingkit.shapeManagers.ShapeManagers.TEMP_HEADER;
import static ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer.*;

public abstract class Shape {
    public boolean isTemp = false;
    public boolean renderFace = true;
    public boolean renderWireframe = false;
    public float wireframeWidth = DisplayTransformHelper.DEFAULT_LINE_WIDTH;
    public Identifier id;
    public DefaultTransformer transformer;
    public Consumer<DefaultTransformer> transformFunction;

    public Shape parent;
    public List<Shape> children = new ArrayList<>();

    public boolean enabled = true;
    public Color baseColor;
    public boolean seeThrough;

    public ServerLevel level;
    public boolean allDim = false;
    public BlockState customBlockState = null;
    public Predicate<ServerPlayer> viewerFilter = player -> true;

    public List<Vec3> modelVertexes = new ArrayList<>();
    public int[] indexBuffer = new int[0];
    public Map<String, Object> customData = new HashMap<>();

    protected final List<VirtualDisplay> displays = new ArrayList<>();
    protected final Map<ResourceKey<Level>, List<VirtualDisplay>> dimDisplays = new HashMap<>();
    protected boolean displaysInitialized = false;

    protected Shape(Consumer<DefaultTransformer> transform, Color color, Vec3 center, boolean seeThrough) {
        this(color, seeThrough);
        this.transformer = new DefaultTransformer(this, center);
        this.transformFunction = transform;
    }

    protected Shape(Color color, boolean seeThrough) {
        this.seeThrough = seeThrough;
        this.baseColor = color != null ? color : Color.WHITE;
    }

    public List<Shape> getChildren() {
        return this.children;
    }

    public void addChild(Shape shape) {
        shape.setParent(this);
        children.add(shape);
    }

    public void setParent(Shape parent) {
        if (this.parent != null) {
            this.parent.children.remove(this);
        }
        this.parent = parent;
    }

    public Shape level(ServerLevel level) {
        this.level = level;
        return this;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public Shape renderFace(boolean renderFace) {
        this.renderFace = renderFace;
        return this;
    }

    public Shape renderWireframe(boolean renderWireframe) {
        this.renderWireframe = renderWireframe;
        return this;
    }

    public Shape wireframeWidth(float width) {
        this.wireframeWidth = DisplayTransformHelper.sanitizeThickness(width);
        return this;
    }

    public Shape allDim(boolean allDim) {
        this.allDim = allDim;
        return this;
    }

    public boolean isAllDim() {
        return this.allDim;
    }

    public Shape visibleTo(Predicate<ServerPlayer> filter) {
        this.viewerFilter = filter != null ? filter : player -> true;
        for (VirtualDisplay d : this.displays) {
            d.filter(this.viewerFilter);
        }
        for (List<VirtualDisplay> list : this.dimDisplays.values()) {
            for (VirtualDisplay d : list) {
                d.filter(this.viewerFilter);
            }
        }
        return this;
    }

    public boolean isVisibleTo(ServerPlayer player) {
        return this.viewerFilter.test(player);
    }

    public Shape block(BlockState state) {
        this.customBlockState = state;
        for (VirtualDisplay d : this.displays) {
            d.blockState(state);
        }
        for (List<VirtualDisplay> list : this.dimDisplays.values()) {
            for (VirtualDisplay d : list) {
                d.blockState(state);
            }
        }
        return this;
    }

    public BlockState getBlockState() {
        return this.customBlockState != null ? this.customBlockState : VirtualDisplay.DEFAULT_BLOCK_STATE;
    }

    public void setLocalPosition(Vec3 pos)          { transformer.setShapeLocalPivot(pos); }
    public void setLocalRotation(Vector3f rot)       { transformer.setShapeLocalRotationDegrees(rot.x(), rot.y(), rot.z()); }
    public void setLocalScale(Vec3 scale)            { transformer.setShapeLocalScale(scale); }
    public void setWorldPosition(Vec3 pos)           { transformer.setShapeWorldPivot(pos); }
    public void setWorldRotation(Vector3f rot)       { transformer.setShapeWorldRotationDegrees(rot.x(), rot.y(), rot.z()); }
    public void setWorldScale(Vec3 scale)            { transformer.setShapeWorldScale(scale); }
    public void setRenderPivot(Vec3 pos)             { transformer.setShapeMatrixPivot(pos); }
    public void setRenderRotation(Vector3f rot)      { transformer.setShapeMatrixRotationDegrees(rot.x(), rot.y(), rot.z()); }
    public void setRenderScale(Vec3 scale)           { transformer.setShapeMatrixScale(scale); }

    public void forceSetLocalPosition(Vec3 pos)     { setLocalPosition(pos);   transformer.local.position.syncLastToTarget(); }
    public void forceSetLocalRotation(Vector3f rot)  { setLocalRotation(rot);   transformer.local.rotation.syncLastToTarget(); }
    public void forceSetLocalScale(Vec3 scale)       { setLocalScale(scale);    transformer.local.scale.syncLastToTarget(); }
    public void forceSetWorldPosition(Vec3 pos)      { setWorldPosition(pos);   transformer.world.position.syncLastToTarget(); }
    public void forceSetWorldRotation(Vector3f rot)  { setWorldRotation(rot);   transformer.world.rotation.syncLastToTarget(); }
    public void forceSetWorldScale(Vec3 scale)       { setWorldScale(scale);    transformer.world.scale.syncLastToTarget(); }
    public void forceSetRenderPivot(Vec3 pos)        { setRenderPivot(pos);     transformer.matrix.position.syncLastToTarget(); }
    public void forceSetRenderRotation(Vector3f rot) { setRenderRotation(rot);  transformer.matrix.rotation.syncLastToTarget(); }
    public void forceSetRenderScale(Vec3 scale)      { setRenderScale(scale);   transformer.matrix.scale.syncLastToTarget(); }

    protected abstract void generateRawGeometry(boolean lerp);

    protected void refreshGeometryIfNeeded(boolean lerp) {
        if (transformer != null && transformer.asyncModelInfo()) {
            modelVertexes.clear();
            generateRawGeometry(lerp);
        }
    }

    public List<Shape> getHierarchy() {
        List<Shape> hierarchy = new ArrayList<>();
        Shape current = this;
        while (current != null) {
            hierarchy.add(current);
            current = current.parent;
        }
        return hierarchy;
    }

    public List<Vec3> getModel(boolean applyMatrixTransformer) {
        refreshGeometryIfNeeded(false);
        Matrix4f mat = new Matrix4f();
        int flags = WORLD | LOCAL | (applyMatrixTransformer ? MATRIX : 0);
        List<Shape> hierarchy = getHierarchy();
        for (int i = hierarchy.size() - 1; i >= 0; i--) {
            Shape s = hierarchy.get(i);
            if (s.transformer != null) {
                s.transformer.applyTransformations(mat, false, flags);
            }
        }

        List<Vec3> transformed = new ArrayList<>(modelVertexes.size());
        Vector3f vec = new Vector3f();
        for (Vec3 local : modelVertexes) {
            vec.set((float) local.x, (float) local.y, (float) local.z);
            vec.mulPosition(mat);
            transformed.add(new Vec3(vec.x(), vec.y(), vec.z()));
        }
        return transformed;
    }

    public abstract void initDisplays(ServerLevel level);

    public abstract void updateDisplays();

    public void removeDisplays() {
        for (VirtualDisplay d : this.displays) {
            d.remove();
        }
        this.displays.clear();

        for (List<VirtualDisplay> list : this.dimDisplays.values()) {
            for (VirtualDisplay d : list) {
                d.remove();
            }
        }
        this.dimDisplays.clear();
        this.displaysInitialized = false;
    }

    public void tick(ServerLevel currentLevel) {
        if (!enabled) {
            if (displaysInitialized) {
                removeDisplays();
            }
            return;
        }

        ServerLevel targetLevel = this.level != null ? this.level : currentLevel;

        if (this.transformFunction != null && this.transformer != null) {
            this.transformFunction.accept(this.transformer);
        }
        refreshGeometryIfNeeded(true);

        if (!allDim) {
            if (!displaysInitialized || (this.displays.isEmpty() && targetLevel != null)) {
                this.displays.clear();
                initDisplays(targetLevel);
                for (VirtualDisplay d : this.displays) {
                    d.filter(this.viewerFilter);
                }
                displaysInitialized = true;
            }
            updateDisplays();
            for (VirtualDisplay d : this.displays) {
                d.sync();
            }
        } else {
            if (targetLevel != null) {
                for (ServerLevel lvl : targetLevel.getServer().getAllLevels()) {
                    ResourceKey<Level> dimKey = lvl.dimension();
                    List<VirtualDisplay> list = dimDisplays.computeIfAbsent(dimKey, k -> {
                        List<VirtualDisplay> created = createDisplaysForLevel(lvl);
                        for (VirtualDisplay d : created) {
                            d.filter(this.viewerFilter);
                        }
                        return created;
                    });
                    updateDisplaysForList(list);
                    for (VirtualDisplay d : list) {
                        d.sync();
                    }
                }
            }
        }

        if (this.transformer != null) {
            this.transformer.syncLastToTarget();
        }

        if (isTemp) {
            discard();
        }
    }

    protected List<VirtualDisplay> createDisplaysForLevel(ServerLevel level) {
        List<VirtualDisplay> saved = new ArrayList<>(this.displays);
        this.displays.clear();
        initDisplays(level);
        List<VirtualDisplay> created = new ArrayList<>(this.displays);
        this.displays.clear();
        this.displays.addAll(saved);
        return created;
    }

    protected void updateDisplaysForList(List<VirtualDisplay> list) {
        List<VirtualDisplay> saved = new ArrayList<>(this.displays);
        this.displays.clear();
        this.displays.addAll(list);
        updateDisplays();
        this.displays.clear();
        this.displays.addAll(saved);
    }

    public boolean enabled() {
        return enabled;
    }

    public void setBaseColor(Color color) {
        this.baseColor = color;
        for (VirtualDisplay d : this.displays) {
            d.glow(this.baseColor.getRGB());
        }
    }

    public Color getBaseColor() { return this.baseColor; }
    public void disable() { this.enabled = false; }
    public void enable() { this.enabled = true; }

    public void setId(Identifier id) {
        this.id = id;
        this.isTemp = this.id.getPath().startsWith(TEMP_HEADER);
    }

    public void discard() {
        children.forEach(Shape::discard);
        removeDisplays();
        ShapeManagers.removeShape(this.id);
    }

    public void syncLastToTarget() {
        if (transformer != null) {
            transformer.syncLastToTarget();
        }
    }

    public <T> void putCustomData(String key, T value) {
        customData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getCustomData(String key, T def) {
        return (T) customData.getOrDefault(key, def);
    }

    public void removeCustomData(String key) {
        customData.remove(key);
    }
}
