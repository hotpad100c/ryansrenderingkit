package mypals.ml.shape.minecraftBuiltIn;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.EmptyMesh;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static mypals.ml.utils.Helpers.multiplyRGB;

public class TextShape extends Shape implements EmptyMesh {

    public ArrayList<String> contents = new ArrayList<>();
    public ArrayList<Color> colors = new ArrayList<>();
    public boolean shadow;
    public boolean outline;

    public Color backgroundColor = new Color(0,0,0,0);
    public BillBoardMode billBoardMode = BillBoardMode.ALL;
    @Nullable
    private FormattedCharSequence[] renderMessages;


    public enum BillBoardMode {
        FIXED, VERTICAL, HORIZONTAL, ALL
    }

    public TextShape(RenderingType type,
                     Consumer<DefaultTransformer> transform,
                     Vec3 center, List<String> texts, List<Color> textColors,
                     Color backgroundColor,
                     BillBoardMode mode, boolean seeThrough,
                     boolean shadow,boolean outline) {
        super(type, transform, Color.white, center, seeThrough);
        this.seeThrough = seeThrough;
        this.contents.addAll(texts);
        this.colors.addAll(textColors);
        this.billBoardMode = mode;
        this.shadow = shadow;
        this.outline = outline;
        this.backgroundColor = backgroundColor;

        this.transformer.setShapeWorldPivot(center);

        syncLastToTarget();
    }
    public TextShape(RenderingType type,
                     Consumer<DefaultTransformer> transform,
                     Vec3 center, List<String> texts, List<Color> textColors,
                     BillBoardMode mode, boolean seeThrough,boolean shadow,boolean outline) {

    this(type,transform,center,texts,textColors,new Color(0,0,0,0),mode,seeThrough,shadow,outline);
    }
    @Override
    protected void generateRawGeometry(boolean lerp) {
        model_vertexes.clear();
    }
    public FormattedCharSequence[] getRenderMessages() {
        if (this.renderMessages == null) {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            this.renderMessages = new FormattedCharSequence[contents.size()];
            MutableComponent[] components = this.getMessages();
            for (int i = 0; i < components.length; i++) {
                this.renderMessages[i] = font.split(components[i],Integer.MAX_VALUE).getFirst();
            }
        }

        return this.renderMessages;
    }

    public MutableComponent[] getMessages() {
        MutableComponent[] components = new MutableComponent[contents.size()];
        for (String string : contents){
            components[contents.indexOf(string)] = Component.literal(string);
        }
        return components;
    }
    @Override
    protected void drawInternal(VertexBuilder builder) {


        Minecraft mc = Minecraft.getInstance();

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Font font = mc.font;
        float totalHeight = 0f;
        float[] lineHeights = new float[contents.size()];

        for (int i = 0; i < contents.size(); i++) {
            String line = contents.get(i);
            int wrappedHeight = font.wordWrapHeight(line, Integer.MAX_VALUE);
            lineHeights[i] = wrappedHeight * 1.25f;
            totalHeight += lineHeights[i];
        }

        float yOffset = -totalHeight / 2f;

        FormattedCharSequence[] renderMessages = getRenderMessages();
        for (int i = 0; i < contents.size(); i++) {
            String text = contents.get(i);
            Color color = i < colors.size() ? colors.get(i) : baseColor;

            float x = -font.width(text) / 2f;
            float y = yOffset;
            if(outline) {
                font.drawInBatch8xOutline(renderMessages[i],x, y,
                        color.getRGB(),
                        multiplyRGB(color.getRGB(),0.8f),
                        builder.getPositionMatrix(),
                        bufferSource, LightTexture.FULL_BRIGHT);
            }else {
                font.drawInBatch(
                        text,
                        x, y,
                        outline ? multiplyRGB(color.getRGB(), 0.9f) : color.getRGB(),
                        shadow,
                        builder.getPositionMatrix(),
                        bufferSource,
                        seeThrough ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET,
                        backgroundColor.getRGB(),
                        LightTexture.FULL_BRIGHT
                );
            }

            yOffset += lineHeights[i];
        }
        RenderSystem.setShaderColor(1,1,1,1);
    }
    @Override
    public void beforeDraw(PoseStack poseStack, float deltaTime) {
        super.beforeDraw(poseStack, deltaTime);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Quaternionf camRot = camera.rotation();

        switch (billBoardMode) {
            case ALL -> {
                poseStack.mulPose(camRot);
            }
            case VERTICAL -> {
                float yaw = (float) Math.toRadians(camera.getYRot() + 180);
                poseStack.mulPose(new Quaternionf().rotateY(yaw));
            }
            case HORIZONTAL -> {
                float pitch = (float) Math.toRadians(camera.getXRot());
                poseStack.mulPose(new Quaternionf().rotateX(pitch));
            }
        }
        poseStack.scale(0.015625F, -0.015625F, 0.015625F);
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
    @Override
    public boolean shouldDraw() {
        return true;
    }
}