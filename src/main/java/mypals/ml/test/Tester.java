package mypals.ml.test;

import mypals.ml.shape.box.BoxShape;
import mypals.ml.shape.box.BoxWireframeShape;
import mypals.ml.shape.Shape;
import mypals.ml.shape.box.WireframedBoxShape;
import mypals.ml.shapeManagers.ShapeManagers;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.awt.*;

import static mypals.ml.RyansRenderingKit.MOD_ID;

public class Tester {
    public static boolean added =false;
    public static void init(){
        ClientTickEvents.START_WORLD_TICK.register((client)->{
            if(added)return;
            /*ShapeManagers.addShape(Identifier.of(MOD_ID,"test_shape1batch"),new WireframedBoxShape(
                            WireframedBoxShape.RenderingType.IMMEDIATE,
                            Tester::rotate,
                            new Vec3d(9,9,9),
                            new Vec3d(7,7,7),
                            new Color(81, 23, 194, 164),
                            new Color(0,0,200,200),
                            3f,
                            true,
                            false
                    )
            );*/
            float spacing = 3.5f;
            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    for (int z = 0; z < 10; z++) {
                        float hue = (float) (x + y * 10 + z * 100) / (10 * 10 * 10);
                        Color color = Color.getHSBColor(hue, 1f, 1f);
                        boolean see = x <5;
                        ShapeManagers.addShape(
                                Identifier.of(MOD_ID, "grid_cube_" + x + "_" + y + "_" + z),
                                new BoxShape(
                                        BoxShape.RenderingType.BUFFERED,
                                        new Vec3d(x * spacing + 10, y * spacing + 10, z * spacing + 10),
                                        new Vec3d(x * spacing + 11, y * spacing + 11, z * spacing + 11),
                                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 80),
                                        see
                                )
                        );
                    }
                }
            }

           /*ShapeManagers.addShape(Identifier.of(MOD_ID,"test_shape2buffered"),
                    new WireframedBoxShape(
                            WireframedBoxShape.RenderingType.BUFFERED,
                            new Vec3d(-3,-3,-3),
                            new Vec3d(-1,-1,-1),
                            Color.BLUE,
                            new Color(0,200,0,200),
                            9f,
                            false,
                            true
                    )
           );*/
           added = true;
        });
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
                Shape.RenderingType.BATCH,
                        new Vec3d(-10,-9,-10),
                        new Vec3d(-5,-3,-5),
                        new Color(23, 194, 148, 98),
                        new Color(200,0, 0,200),
                        5f,
                        false,
                        true
                )
        );*/
    }
    public static void rotate(MatrixStack matrixStack, Shape shape){
        double centerX,centerY,centerZ;

        if(shape instanceof BoxShape boxShape){
            centerX = (boxShape.min.getX() + boxShape.max.getX()) / 2.0;
            centerY = (boxShape.min.getY() + boxShape.max.getY()) / 2.0;
            centerZ = (boxShape.min.getZ() + boxShape.max.getZ()) / 2.0;
        } else if (shape instanceof BoxWireframeShape boxWireframeShape) {
            centerX = (boxWireframeShape.min.getX() + boxWireframeShape.max.getX()) / 2.0;
            centerY = (boxWireframeShape.min.getY() + boxWireframeShape.max.getY()) / 2.0;
            centerZ = (boxWireframeShape.min.getZ() + boxWireframeShape.max.getZ()) / 2.0;
        }else {
            return;
        }

        matrixStack.translate(centerX, centerY, centerZ);

        float gameTime = MinecraftClient.getInstance().world.getTime();
        float rotationAngle = (gameTime % 3600) * 0.1f;
        matrixStack.multiply((new Quaternionf()).rotationZYX(rotationAngle, rotationAngle, rotationAngle));

        matrixStack.translate(-centerX, -centerY, -centerZ);

    }
}
