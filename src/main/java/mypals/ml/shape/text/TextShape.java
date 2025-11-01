package mypals.ml.shape.text;

import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shape.Shape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class TextShape extends Shape {
    public ArrayList<String> contents = new ArrayList<>();
    public ArrayList<Color> color = new ArrayList<>();
    public BillBoardMode bill = BillBoardMode.ALL;
    public enum BillBoardMode {
        VERTICAL,
        HORIZONTAL,
        ALL
    }

    protected TextShape(RenderingType type, BiConsumer<? super DefaultTransformer, Shape> transform, ArrayList<String> texts,ArrayList<Color> colors) {
        super(type, transform);
        this.color = colors;
        this.contents = texts;
    }

    protected TextShape(RenderingType type) {
        super(type);
    }

    protected TextShape(RenderingType type, boolean seeThrough) {
        super(type, seeThrough);
    }

    public TextShape(RenderingType type, BiConsumer<? super DefaultTransformer, Shape> transform, boolean seeThrough) {
        super(type, transform, seeThrough);
    }
    public TextShape(RenderingType type, BiConsumer<DefaultTransformer, Shape> transform, BillBoardMode bill, Vec3d center,ArrayList<String> texts,ArrayList<Color> colors, boolean seeThrough) {
        super(type,seeThrough);
        this.transformer = new DefaultTransformer(this);
        this.bill = bill;
        this.color = colors;
        this.contents = texts;
        this.transformFunction = (defaultTransformer,shape)->transform.accept(this.transformer, shape);
        this.centerPoint = center;
        this.transformer.setShapeCenterPos(this.calculateShapeCenterPos());
        syncLastToTarget();
    }
    @Override
    public void beforeDraw(MatrixStack matrixStack, float deltaTime) {
        applyBillboard(matrixStack,this.bill);
        super.beforeDraw(matrixStack,deltaTime);
    }
    public Vec3d calculateShapeCenterPos(){
        return centerPoint;
    }

    @Override
    public void setMatrixScale(Vector3f scale, MatrixStack matrixStack){
        matrixStack.scale(scale.x,-scale.y,1);
    }
    @Override
    public void draw(ShapeBuilder builder) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        float[] lineHeights = new float[this.contents.size()];
        float totalHeight = 0.0f;
        for (int i = 0; i < this.contents.size(); i++) {
            lineHeights[i] = textRenderer.getWrappedLinesHeight(this.contents.get(i), Integer.MAX_VALUE) * 1.25f;
            totalHeight += lineHeights[i];
        }
        float renderYBase = -totalHeight / 2.0f;
        for (int i = 0; i < this.contents.size(); i++) {
            String text = this.contents.get(i);
            float renderX = -textRenderer.getWidth(text) * 0.5f;
            float renderY = renderYBase + (i > 0 ? lineHeights[i - 1] : 0);
            int colorValue = (this.color.size() > i && this.color.get(i) != null) ? this.color.get(i).getRGB() : Color.WHITE.getRGB();

            textRenderer.draw(
                    text, renderX, renderY, colorValue, true,
                    builder.getPositionMatrix(), new NonDrawVertexConsumerProvider(builder.getBufferBuilder()),
                    seeThrough ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL,
                    0, 0xF000F0
            );

            if (i > 0) renderYBase += lineHeights[i - 1];
        }
    }
    public void applyBillboard(MatrixStack matrices, BillBoardMode mode) {
        float yaw = MinecraftClient.getInstance().gameRenderer.getCamera().getYaw();
        float pitch = MinecraftClient.getInstance().gameRenderer.getCamera().getPitch();

        switch (mode) {
            case VERTICAL:
                matrices.multiply(new Quaternionf().rotateY((float)Math.toRadians(yaw)));
                break;
            case HORIZONTAL:
                matrices.multiply(new Quaternionf().rotateX((float)Math.toRadians(pitch)));
                break;
            case ALL:
                matrices.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());
                break;
        }
    }

}