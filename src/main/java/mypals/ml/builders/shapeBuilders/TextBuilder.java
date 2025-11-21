package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.text.TextShape;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TextBuilder extends BaseBuilder<TextBuilder, DefaultTransformer> {

    private Shape.RenderingType type = Shape.RenderingType.IMMEDIATE;
    private List<String> texts = new ArrayList<>();
    private List<Color> textColors = new ArrayList<>();
    private TextShape.BillBoardMode billBoardMode = TextShape.BillBoardMode.FIXED;
    private boolean shadow = true;
    private boolean outline = false;


    public TextBuilder type(Shape.RenderingType type) {
        this.type = type;
        return this;
    }

    public TextBuilder texts(List<String> texts) {
        this.texts = new ArrayList<>(texts);
        return this;
    }

    public TextBuilder texts(String... texts) {
        this.texts = Arrays.asList(texts);
        return this;
    }

    public TextBuilder addText(String text) {
        this.texts.add(text);
        return this;
    }

    public TextBuilder addText(String text, Color color) {
        this.texts.add(text);
        this.textColors.add(color);
        return this;
    }

    public TextBuilder textColors(List<Color> colors) {
        this.textColors = new ArrayList<>(colors);
        return this;
    }

    public TextBuilder textColors(Color... colors) {
        this.textColors = Arrays.asList(colors);
        return this;
    }

    public TextBuilder billBoardMode(TextShape.BillBoardMode mode) {
        this.billBoardMode = mode != null ? mode : TextShape.BillBoardMode.FIXED;
        return this;
    }

    public TextBuilder shadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public TextBuilder outline(boolean outline) {
        this.outline = outline;
        return this;
    }

    @Override
    public TextShape build(Shape.RenderingType type) {
        this.type = type;
        if (textColors.isEmpty() || textColors.size() < texts.size()) {
            List<Color> colors = new ArrayList<>(texts.size());
            colors.addAll(textColors);
            while (colors.size() < texts.size()) {
                colors.add(Color.WHITE);
            }
            textColors = colors;
        }

        return new TextShape(
                this.type,
                getTransformer(),
                center,
                Collections.unmodifiableList(texts),
                Collections.unmodifiableList(textColors),
                billBoardMode,
                seeThrough,
                shadow,
                outline
        );
    }
    
}