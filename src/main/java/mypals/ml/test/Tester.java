package mypals.ml.test;

import mypals.ml.shape.BoxShape;
import mypals.ml.shape.Shape;
import mypals.ml.shape.WireframedBoxShape;
import mypals.ml.shapeManagers.ShapeManagers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.awt.*;

import static mypals.ml.RyansRenderingKit.MOD_ID;

public class Tester {
    public static void init(){
        ShapeManagers.addShape(Identifier.of(MOD_ID,"test_shape1batch"),new WireframedBoxShape(
                        WireframedBoxShape.RenderingType.BATCH,
                        Tester::rotate,
                        new Vec3d(9,9,9),
                        new Vec3d(7,7,7),
                        new Color(81, 23, 194, 164),
                        new Color(0,0,200,200),
                        3f,
                        true,
                        false
                )
        );
       /*ShapeManagers.addShape(Identifier.of(MOD_ID,"test_shape2"),
                new WireframedBoxShape(
                        WireframedBoxShape.RenderingType.IMMEDIATE,
                        new Vec3d(-3,-3,-3),
                        new Vec3d(-1,-1,-1),
                        Color.BLUE,
                        new Color(0,200,0,200),
                        0.3f,
                        false,
                        true
                )
       );*/
    }
    public static void renderTick(MatrixStack matrixStack){
        /*ShapeManagers.addShape(new WireframedBoxShape(
                WireframedBoxShape.RenderingType.BATCH,
                    Tester::rotate,
                        new Vec3d(9,9,9),
                        new Vec3d(7,7,7),
                        Color.RED,
                        new Color(0,0,200,200),
                        2f,
                        true,
                        false
                )
        );*/
        /*ShapeManagers.addShape(new WireframedBoxShape(
                        WireframedBoxShape.RenderingType.IMMEDIATE,
                        new Vec3d(-9,-9,-9),
                        new Vec3d(-7,-7,-7),
                        Color.BLUE,
                        new Color(0,0,200,200),
                        0.3f,
                        false,
                        false
                )
        );*/
    }
    public static void rotate(MatrixStack matrixStack, Shape shape){
        if(!(shape instanceof BoxShape boxShape)) return;
        double centerX = (boxShape.min.getX() + boxShape.max.getX()) / 2.0;
        double centerY = (boxShape.min.getY() + boxShape.max.getY()) / 2.0;
        double centerZ = (boxShape.min.getZ() + boxShape.max.getZ()) / 2.0;

        matrixStack.translate(centerX, centerY, centerZ);

        float gameTime = MinecraftClient.getInstance().world.getTime();
        float rotationAngle = (gameTime % 3600) * 0.1f;
        matrixStack.multiply((new Quaternionf()).rotationZYX(rotationAngle, rotationAngle, rotationAngle));

        matrixStack.translate(-centerX, -centerY, -centerZ);

        System.out.println("Modified matrix: " + matrixStack.peek().getPositionMatrix());
    }
}
