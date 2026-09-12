package ml.mypals.ryansserverrenderingkit.shape.minecraftBuiltIn;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.mixin.TextDisplayAccessor;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static ml.mypals.ryansserverrenderingkit.display.VirtualDisplay.FLAG_SEE_THROUGH;
import static ml.mypals.ryansserverrenderingkit.display.VirtualDisplay.FLAG_SHADOW;

public class TextShape extends Shape {

    public ArrayList<String> contents = new ArrayList<>();
    public ArrayList<Color> colors = new ArrayList<>();
    public boolean shadow;
    public boolean outline;
    public Color backgroundColor = new Color(0, 0, 0, 0);
    public BillBoardMode billBoardMode = BillBoardMode.ALL;

    public enum BillBoardMode {
        FIXED, VERTICAL, HORIZONTAL, ALL
    }

    public TextShape(Consumer<DefaultTransformer> transform,
                     Vec3 center, List<String> texts, List<Color> textColors,
                     Color backgroundColor,
                     BillBoardMode mode, boolean seeThrough,
                     boolean shadow, boolean outline) {
        super(transform, Color.WHITE, center, seeThrough);
        this.contents.addAll(texts);
        if (textColors != null) {
            this.colors.addAll(textColors);
        }
        this.billBoardMode = mode != null ? mode : BillBoardMode.ALL;
        this.shadow = shadow;
        this.outline = outline;
        this.backgroundColor = backgroundColor != null ? backgroundColor : new Color(0, 0, 0, 0);

        this.transformer.setShapeWorldPivot(center);
        syncLastToTarget();
    }

    public TextShape(Consumer<DefaultTransformer> transform,
                     Vec3 center, List<String> texts, List<Color> textColors,
                     BillBoardMode mode, boolean seeThrough, boolean shadow, boolean outline) {
        this(transform, center, texts, textColors, new Color(0, 0, 0, 0), mode, seeThrough, shadow, outline);
    }

    @Deprecated
    public TextShape(Object ignored,
                     Consumer<DefaultTransformer> transform,
                     Vec3 center, List<String> texts, List<Color> textColors,
                     BillBoardMode mode, boolean seeThrough, boolean shadow, boolean outline) {
        this(transform, center, texts, textColors, mode, seeThrough, shadow, outline);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();
    }

    public Component buildTextComponent() {
        MutableComponent root = Component.empty();
        for (int i = 0; i < contents.size(); i++) {
            String line = contents.get(i);
            Color color = i < colors.size() ? colors.get(i) : baseColor;
            MutableComponent lineComp = Component.literal(line).withStyle(s -> s.withColor(TextColor.fromRgb(color.getRGB())));
            if (i > 0) {
                root.append(Component.literal("\n"));
            }
            root.append(lineComp);
        }
        return root;
    }

    public Display.BillboardConstraints toBillboardConstraints() {
        return switch (billBoardMode) {
            case FIXED -> Display.BillboardConstraints.FIXED;
            case VERTICAL -> Display.BillboardConstraints.VERTICAL;
            case HORIZONTAL -> Display.BillboardConstraints.HORIZONTAL;
            case ALL -> Display.BillboardConstraints.CENTER;
        };
    }

    @Override
    public void initDisplays(ServerLevel level) {
        Vec3 pos = transformer.getWorldPivot();
        VirtualDisplay display = VirtualDisplay.text(level, pos.x, pos.y, pos.z, buildTextComponent())
                .billboard(toBillboardConstraints())
                .seeThrough(this.seeThrough, this.baseColor.getRGB())
                .transform(transformer.toTransformation(false));

        if (display.getEntity() instanceof Display.TextDisplay textDisplay) {
            TextDisplayAccessor accessor = (TextDisplayAccessor) textDisplay;
            accessor.rrk$setBackgroundColor(backgroundColor.getRGB());
            byte flags = 0;
            if (shadow) flags |= FLAG_SHADOW;
            if (seeThrough) flags |= FLAG_SEE_THROUGH;
            accessor.rrk$setFlags(flags);
        }
        this.displays.add(display);
    }

    @Override
    public void updateDisplays() {
        if (this.displays.isEmpty()) return;
        Vec3 pos = transformer.getWorldPivot();
        Transformation t = transformer.toTransformation(true);
        VirtualDisplay display = this.displays.getFirst();
        display.pos(pos.x, pos.y, pos.z)
                .textComponent(buildTextComponent())
                .billboard(toBillboardConstraints())
                .seeThrough(this.seeThrough, this.baseColor.getRGB())
                .transform(t);

        if (display.getEntity() instanceof Display.TextDisplay textDisplay) {
            TextDisplayAccessor accessor = (TextDisplayAccessor) textDisplay;
            accessor.rrk$setBackgroundColor(backgroundColor.getRGB());
            byte flags = 0;
            if (shadow) flags |= FLAG_SHADOW;
            if (seeThrough) flags |= FLAG_SEE_THROUGH;
            accessor.rrk$setFlags(flags);
        }
    }

    public void setText(int line, String text) {
        line--;
        if (line >= 0 && line < contents.size()) {
            contents.set(line, text);
        }
    }

    public void setColor(int line, Color color) {
        while (colors.size() <= line) colors.add(Color.WHITE);
        colors.set(line, color);
    }

    public void setBillboardMode(BillBoardMode mode) {
        this.billBoardMode = mode;
    }
}