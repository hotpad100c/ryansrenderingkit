package mypals.ml.shape.text;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class TextShape {
    /*public ArrayList<String> contents = new ArrayList<>();
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
    public TextShape(RenderingType type, BiConsumer<DefaultTransformer, Shape> transform, BillBoardMode bill, Vec3 center,ArrayList<String> texts,ArrayList<Color> colors, boolean seeThrough) {
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
    public void beforeDraw(PoseStack matrixStack, float deltaTime) {
        applyBillboard(matrixStack,this.bill);
        super.beforeDraw(matrixStack,deltaTime);
    }
    public Vec3 calculateShapeCenterPos(){
        return centerPoint;
    }

    @Override
    public void setMatrixScale(Vector3f scale, PoseStack matrixStack){
        matrixStack.scale(scale.x,-scale.y,1);
    }
    @Override
    public void draw(VertexBuilder builder) {
        Minecraft client = Minecraft.getInstance();
        Font textRenderer = client.font;

        float[] lineHeights = new float[this.contents.size()];
        float totalHeight = 0.0f;
        for (int i = 0; i < this.contents.size(); i++) {
            lineHeights[i] = textRenderer.wordWrapHeight(this.contents.get(i), Integer.MAX_VALUE) * 1.25f;
            totalHeight += lineHeights[i];
        }
        float renderYBase = -totalHeight / 2.0f;
        for (int i = 0; i < this.contents.size(); i++) {
            String text = this.contents.get(i);
            float renderX = -textRenderer.width(text) * 0.5f;
            float renderY = renderYBase + (i > 0 ? lineHeights[i - 1] : 0);
            int colorValue = (this.color.size() > i && this.color.get(i) != null) ? this.color.get(i).getRGB() : Color.WHITE.getRGB();

            textRenderer.drawInBatch(
                    text, renderX, renderY, colorValue, true,
                    builder.getPositionMatrix(), new NonDrawVertexConsumerProvider(builder.getBufferBuilder()),
                    seeThrough ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL,
                    0, 0xF000F0
            );

            if (i > 0) renderYBase += lineHeights[i - 1];
        }
    }
    public void applyBillboard(PoseStack matrices, BillBoardMode mode) {
        float yaw = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
        float pitch = Minecraft.getInstance().gameRenderer.getMainCamera().getXRot();

        switch (mode) {
            case VERTICAL:
                matrices.mulPose(new Quaternionf().rotateY((float)Math.toRadians(yaw)));
                break;
            case HORIZONTAL:
                matrices.mulPose(new Quaternionf().rotateX((float)Math.toRadians(pitch)));
                break;
            case ALL:
                matrices.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
                break;
        }
    }*/

}