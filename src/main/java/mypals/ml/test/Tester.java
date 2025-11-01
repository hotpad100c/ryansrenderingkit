package mypals.ml.test;

import mypals.ml.shape.basics.BoxLikeShape;
import mypals.ml.shape.box.BoxFaceShape;
import mypals.ml.shape.box.BoxShape;
import mypals.ml.shape.Shape;
import mypals.ml.shape.round.FaceCircleShape;
import mypals.ml.shape.round.LineCircleShape;
import mypals.ml.shape.line.LineShape;
import mypals.ml.shape.round.SphereShape;
import mypals.ml.shapeManagers.ShapeManagers;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;

import static mypals.ml.RyansRenderingKit.MOD_ID;

public class Tester {
    public static boolean added =false;
    public static void init(){
        ShapeManagers.removeShapes(Identifier.of(MOD_ID,"test_shape_circle"));
        ShapeManagers.removeShapes(Identifier.of(MOD_ID,"test_shape_ball"));
        ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> {
            commandDispatcher.register(
                    ClientCommandManager.literal("reload")
                            .executes(context -> {
                                added = false;
                                return 0;
                            })
            );
        });
        ClientTickEvents.START_WORLD_TICK.register((client)->{
            if(added)return;
            ShapeManagers.addShape(Identifier.of(MOD_ID,"test_shape1"),new BoxFaceShape(
                    BoxFaceShape.RenderingType.IMMEDIATE,
                            Tester::rotate,
                            new Vec3d(-1,-1,-1),
                            new Vec3d(1,1,1),
                            new Color(134, 99, 205, 164),
                            true,
                            BoxShape.BoxConstructionType.CORNERS
                    )
            );

            ArrayList<Vec3d> testVertexes = new ArrayList<>();
            Vec3d center = Vec3d.ZERO;
            float segments = 180;
            float radius = 0.5f;
            double angleStep = 2 * Math.PI / segments;
            for (int i = 0; i < segments; i++) {
                double theta = i * angleStep;
                double x = center.x + radius * Math.cos(theta);
                double z = center.z + radius * Math.sin(theta);
                testVertexes.add(new Vec3d(x, center.y, z));
            }

            ShapeManagers.addShape(
                    Identifier.of(MOD_ID, "test_shape_circle"),//标识符
                    new FaceCircleShape(
                            BoxFaceShape.RenderingType.BATCH,//绘制模式
                            //-- 变换逻辑（每帧）--------------------
                            (transformer, shape) -> {
                                MinecraftClient c = MinecraftClient.getInstance();
                                if (c.world == null || c.player == null) return;
                                PlayerEntity player = c.player;
                                float gameTime = c.world.getTime();
                                float rotationAngle = (gameTime % 3600) * 5f;
                                transformer.setShapeCenterPos(player.getEyePos().add(0, 0.5, 0));
                                transformer.setMatrixRotation(new Vec3d(0, 0, 0));
                                transformer.setSegment(10);
                                transformer.setRadius(0.5f);
                            },
                            //-------------------------------------
                            LineCircleShape.CircleAxis.Y, //圆的轴方向
                            Vec3d.ZERO,     // 初始中心
                            180,            // 初始角度（度）
                            0.5f,           // 初始半径
                            new Color(255, 0, 255, 100), // 初始颜色
                            true // 透视
                    )
            );
            ShapeManagers.addShape(
                    Identifier.of(MOD_ID, "test_shape_ball"),//标识符
                    new SphereShape(
                            Shape.RenderingType.BATCH,
                            //-- 变换逻辑（每帧）--------------------
                            (transformer, shape) -> {
                                MinecraftClient c = MinecraftClient.getInstance();
                                if (c.world == null || c.player == null) return;
                                PlayerEntity player = c.player;
                                float gameTime = c.world.getTime();
                                float rotationAngle = (gameTime % 3600) * 5f;
                                transformer.setShapeCenterPos(player.getPos().add(0,player.getHeight()/2,0));
                                transformer.setMatrixRotation(new Vec3d(0, rotationAngle, 0));
                                transformer.setRadius(3);
                                transformer.setSegment(2);
                            },
                            //-------------------------------------
                            SphereShape.SphereMode.Ico, //圆的轴方向
                            Vec3d.ZERO,     // 初始中心
                            8,            // 初始角度（度）
                            0.5f,           // 初始半径
                            new Color(255, 255, 255, 20), // 初始颜色
                            false // 透视
                    )
            );




            /*ArrayList<String> texts = new ArrayList<>();
            texts.add("Hello");
            ArrayList<Color> colors = new ArrayList<>();
            colors.add(Color.RED);
            ShapeManagers.addShape(
                    Identifier.of(MOD_ID, "test_text"),
                    new TextShape(
                            Shape.RenderingType.IMMEDIATE, (a, b)->{},
                            TextShape.BillBoardMode.ALL, center, texts, colors,true
                    )
            );*/
            /*
            ShapeManagers.addShape(Identifier.of(MOD_ID,"test_shape2"),
                    new LineShape(Shape.RenderingType.IMMEDIATE,
                    (transformer,shape)->{
                        if(transformer instanceof LineLikeShape.LineTransformer lineTransformer
                                && MinecraftClient.getInstance().world != null
                                && MinecraftClient.getInstance().player != null){
                            PlayerEntity player = MinecraftClient.getInstance().player;

                            lineTransformer.setEnd(player.getPos());

                        }
                    },
                    Vec3d.ZERO,Vec3d.ZERO,Color.ORANGE,3,false));*/
            ShapeManagers.removeShapes(Identifier.of(MOD_ID,"test_shape2"));

            //ShapeManagers.removeShape(Identifier.of(MOD_ID,"test_shape1"));
           added = true;
        });
    }
    public static void renderTick(MatrixStack matrixStack){
    }
    public static void rotate(BoxLikeShape.BoxTransformer boxTransformer, Shape shape){

        float gameTime = MinecraftClient.getInstance().world.getTime();
        float rotationAngle = (gameTime % 3600) * 2f;
        boxTransformer.setMatrixRotation(new Vec3d(rotationAngle, rotationAngle, rotationAngle));
    }
    public static void addEntity(Entity entity) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || entity == null || entity == player) return;

        int entityId = entity.getId();
        EntityDimensions dimensions = entity.getDimensions(entity.getPose());
        ShapeManagers.addShape(
                Identifier.of(MOD_ID, "entity_tracker_" + entityId + "/bounding_box"),
                new BoxFaceShape(
                        Shape.RenderingType.BATCH,
                        (transformer, shape) -> {
                            Vec3d entityCenter = entity.getPos().add(0, dimensions.height() / 2, 0);
                            if (entity.isRemoved()) {
                                shape.discard();
                                return;
                            }
                            transformer.setShapeCenterPos(entityCenter);
                        },
                        entity.getPos().add(0, dimensions.height() / 2, 0),
                        new Vec3d(dimensions.width(), dimensions.height(), dimensions.width()),
                        new Color(191, 87, 0, 164),
                        true,
                        BoxShape.BoxConstructionType.CENTER_AND_DIMENSIONS
                )
        );

        ShapeManagers.addShape(
                Identifier.of(MOD_ID, "entity_tracker_" + entityId + "/line"),
                new LineShape(
                        Shape.RenderingType.BATCH,
                        (transformer, shape) -> {
                            if (entity.isRemoved()) {
                                shape.discard();
                                return;
                            }
                            if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
                                transformer.setStart(player.getEyePos().add(player.getRotationVector().multiply(2)));
                                transformer.setEnd(entity.getPos());
                            }
                        },
                        player.getEyePos().add(player.getRotationVector().multiply(2)),
                        entity.getPos(),
                        new Color(255, 0, 0, 50),
                        3,
                        true
                )
        );

        added = true;
    }

    public static void removeEntity(int entityId) {
        ShapeManagers.removeShapes(Identifier.of(MOD_ID,"entity_tracker_" + entityId));
    }
}
