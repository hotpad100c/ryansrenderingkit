package mypals.ml.test;

import mypals.ml.builders.shapeBuilders.ShapeGenerator;
import mypals.ml.collision.RayModelIntersection;
import mypals.ml.shape.basics.BoxLikeShape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.box.BoxFaceShape;
import mypals.ml.shape.box.BoxShape;
import mypals.ml.shape.Shape;
import mypals.ml.shape.line.LineShape;
import mypals.ml.shape.line.StripLineShape;
import mypals.ml.shape.round.SphereShape;
import mypals.ml.shapeManagers.ShapeManagers;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static mypals.ml.RyansRenderingKit.MOD_ID;

import com.mojang.blaze3d.vertex.PoseStack;

public class Tester {
    public static boolean added =false;
    public static Random random = new Random();
    public static float spacing = 8.0f;
    public static int index = 0;

    static Color randomColor() {
        return new Color(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                120 + random.nextInt(136)
        );
    }

    static float xPos() {
        return (index++ * spacing);
    }

    public static void init(){
        ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> {
            commandDispatcher.register(
                    ClientCommandManager.literal("reload")
                            .executes(context -> {
                                added = false;
                                return 0;
                            })
            );
        });
        ClientTickEvents.START_WORLD_TICK.register(client -> {
            if (added) return;
            index = 0;
            List.of(
                    "demo_face_circle",
                    "demo_line_circle",
                    "demo_sphere",
                    "demo_obj_model",
                    "demo_obj_outline",
                    "demo_cone",
                    "demo_cylinder",
                    "demo_cone_wire",
                    "demo_cylinder_wire",
                    "demo_line",
                    "demo_strip_line",
                    "demo_box_face",
                    "demo_box_wire",
                    "demo_wireframed_box"
            ).forEach(name -> ShapeManagers.removeShapes(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, name)
            ));

            // 1. FaceCircle
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_face_circle"),
                    ShapeGenerator.generateFaceCircle()
                            .pos(new Vec3(xPos(), 0,0))
                            .radius(2.0f)
                            .segments(64)
                            .axis(CircleLikeShape.CircleAxis.Y)
                            .color(randomColor())
                            .seeThrough(false)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                 t.setShapeWorldRotationDegrees(0, time * 3, 0);
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            // 2. LineCircle
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_line_circle"),
                    ShapeGenerator.generateLineCircle()
                            .pos(new Vec3(xPos(), 0,0))
                            .radius(2.0f)
                            .segments(64)
                            .axis(CircleLikeShape.CircleAxis.Y)
                            .lineWidth(3.0f)
                            .color(randomColor())
                            .seeThrough(false)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                //t.setShapeWorldRotationDegrees(0, time * 4, 0);

                                 t.setShapeLocalRotationDegrees(0,time*4,0);
                                 t.setShapeWorldRotationDegrees(time*4, 0, 0);
                            })
                            .build(Shape.RenderingType.BUFFERED)
            );

            // 3. Sphere
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_sphere"),
                    ShapeGenerator.generateSphere()
                            .pos(new Vec3(xPos(), 0,0))
                            .radius(2.0f)
                            .segments(32)
                            .color(randomColor())
                            .seeThrough(false)
                            .transform((t) -> {
                                float time = client.getGameTime();
                                t.setShapeWorldRotationDegrees(time * 2, time * 3, 0);
                            })
                            .build(Shape.RenderingType.BUFFERED)
            );

            Vec3 spos = new Vec3(xPos(), 0,20);
            Shape s1 = ShapeGenerator.generateSphere()
                    .pos(spos)
                    .radius(2.0f)
                    .segments(6)
                    .color(randomColor())
                    .seeThrough(false)
                    .transform((t) -> {
                        float time = client.getGameTime();
                        t.setShapeWorldRotationDegrees(0, time * 3, 0);

                        float yOffset = (float)Math.sin(time * 0.1) * 2f;
                        Vec3 worldPivot = t.getShapeWorldPivot(false);
                        t.setShapeWorldPivot(new Vec3(worldPivot.x, 0 + yOffset, worldPivot.z));
                    })
                    .build(Shape.RenderingType.BATCH);

            Shape s2 = ShapeGenerator.generateSphere()
                    .pos(new Vec3(15,0,0))
                    .radius(1.0f)
                    .segments(3)
                    .color(randomColor())
                    .seeThrough(false)
                    .transform((t) -> {
                        float time = client.getGameTime();
                        t.setShapeWorldRotationDegrees(0, time*6, 0);
                    })
                    .build(Shape.RenderingType.BATCH);

            Shape s3 = ShapeGenerator.generateSphere()
                    .pos(new Vec3(7,0,0))
                    .radius(0.3f)
                    .segments(5)
                    .color(randomColor())
                    .seeThrough(false)
                    .transform((t) -> {
                        float time = client.getGameTime();
                        //t.setShapeWorldRotationDegrees(time * 2, time * 3, 0);
                        if(t.shape.isPlayerLookingAt().hit){
                            t.shape.baseColor = new Color(255, 234, 0,100);
                        }else{
                            t.shape.baseColor = new Color(255, 0, 251,100);
                        }
                    })
                    .build(Shape.RenderingType.BATCH);

            s2.addChild(s3);
            s1.addChild(s2);


            // 3...3. Sphere
            ShapeManagers.addShape(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_sphere_2"),
                s1
            );
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_sphere_2_child"),
                    s2
            );
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_sphere_3_child"),
                    s3
            );

            // 4. ObjModel
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_obj_model"),
                    ShapeGenerator.generateObjModel()
                            .pos(new Vec3(xPos(), 0, 0))
                            .model(ResourceLocation.fromNamespaceAndPath(MOD_ID, "models/monkey.obj"))
                            .color(randomColor())
                            .seeThrough(false)
                            .transform((transformer) -> {
                                float time = client.getGameTime();
                                transformer.setShapeWorldRotationDegrees(0, time * 5, 0);
                                Shape shape = transformer.shape;
                                if (shape.isPlayerLookingAt().hit/* 如果玩家正在看着这个 shape*/) {
                                    if (Minecraft.getInstance().mouseHandler.isLeftPressed()) {
                                        Player player = Minecraft.getInstance().player;
                                        // 第一次抓取时，记录形状与玩家视线的距离
                                        if (!shape.getCustomData("isHolding", false)) {
                                            shape.putCustomData("isHolding", true);
                                            double distance = transformer
                                                    .getShapeWorldPivot(true)
                                                    .distanceTo(player.getEyePosition());
                                            shape.putCustomData("distance", distance);
                                        }
                                        // 将模型移动到玩家正前方
                                        transformer.setShapeWorldPivot(
                                                player.getEyePosition(transformer.getTickDelta())
                                                        .add(player.getLookAngle().scale(shape.getCustomData("distance", 5.0)))
                                                        .add(0, -1, 0)
                                        );
                                        transformer.world.position.syncLastToTarget();
                                    } else {
                                        // 松开鼠标 → 结束抓取
                                        if (shape.getCustomData("isHolding", false))
                                            shape.putCustomData("isHolding", false);
                                    }

                                    shape.baseColor = new Color(255, 0, 72, 100);
                                } else {
                                    if (shape.getCustomData("isHolding", false))
                                        shape.putCustomData("isHolding", false);
                                    shape.baseColor = new Color(0, 255, 72, 100);
                                }
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            // 5. ObjModelOutline
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_obj_outline"),
                    ShapeGenerator.generateObjModelOutline()
                            .pos(new Vec3(xPos(), 0,0))
                            .model(ResourceLocation.fromNamespaceAndPath(MOD_ID, "models/monkey.obj"))
                            .lineWidth(4.0f)
                            .color(randomColor())
                            .seeThrough(false)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                 t.setShapeWorldRotationDegrees(0, time * 5, 0);
                                 if(t.shape.isPlayerLookingAt().hit){
                                     t.shape.baseColor = new Color(255,0,72,100);
                                 }else{
                                     t.shape.baseColor = new Color(0, 255, 72,100);
                                 }
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            // 6. Cone
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_cone"),
                    ShapeGenerator.generateCone()
                            .pos(new Vec3(xPos(), 0,0))
                            .radius(2.0f)
                            .height(4.0f)
                            .segments(32)
                            .axis(CircleLikeShape.CircleAxis.Y)
                            .color(randomColor())
                            .seeThrough(false)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                 t.setShapeWorldRotationDegrees(time, 0, 0);

                                double f = (Math.sin(time * 0.1) + 1) / 2; // 0 → 1 → 0
                                int seg = (int)(3 + f * 20-3);
                                 if(t.shape.isPlayerLookingAt().hit){
                                     t.shape.baseColor = new Color(0, 255, 149,100);
                                 }else{
                                     t.shape.baseColor = new Color(136, 136, 136,100);
                                 }
                                t.setSegment(Math.max(3,seg));
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            // 7. Cylinder
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_cylinder"),
                    ShapeGenerator.generateCylinder()
                            .pos(new Vec3(xPos(), 0,0))
                            .radius(1.5f)
                            .height(4.0f)
                            .segments(32)
                            .axis(CircleLikeShape.CircleAxis.Z)
                            .color(randomColor())
                            .seeThrough(false)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                t.setShapeLocalPivot(new Vec3(0,0,0));
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_cone_wire"),
                    ShapeGenerator.generateConeWireframe()
                            .pos(new Vec3(xPos(), 0,0))
                            .radius(2.0f)
                            .height(4.0f)
                            .segments(32)
                            .axis(CircleLikeShape.CircleAxis.Y)
                            .color(randomColor())
                            .seeThrough(false)
                            .width(3)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                 t.setShapeWorldRotationDegrees(time, 0, 0);

                                double f = (Math.sin(time * 0.1) + 1) / 2; // 0 → 1 → 0
                                int seg = (int)(3 + f * 20-3);

                                t.setSegment(Math.max(3,seg));
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_cylinder_wire"),
                    ShapeGenerator.generateCylinderWireframe()
                            .pos(new Vec3(xPos(), 0,0))
                            .radius(1.5f)
                            .height(4.0f)
                            .segments(32)
                            .axis(CircleLikeShape.CircleAxis.Z)
                            .color(randomColor())
                            .seeThrough(false)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                 t.setShapeWorldRotationDegrees(time*2, time * 5, 0);
                                 //t.setShapeLocalRotationDegrees(0,0,time * 5);
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            float linex  = xPos();
            // 8. Line
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_line"),
                    ShapeGenerator.generateLine()
                            .start(new Vec3(linex - 2, 0,0))
                            .end(new Vec3(linex + 2, 4,0))
                            .lineWidth(3.0f)
                            .color(randomColor())
                            .seeThrough(false)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                t.setStart(new Vec3(linex - 2, 0,0));
                                t.setEnd(new Vec3(linex + 2, 4 + (float)Math.sin(time * 0.1) * 2,0));
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            // 9. StripLine（螺旋线）
            float cx = xPos();
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_strip_line"),
                    ShapeGenerator.generateStripLine()
                            .vertexes(generateSpiral(xPos(), 100, 2.0f, 5.0f))
                            .lineWidth(2.0f)
                            .color(randomColor())
                            .seeThrough(false)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                // 动态更新顶点（旋转螺旋）
                                ((StripLineShape)t.getShape()).setVertexes(generateSpiral(cx, 100, 2.0f, 5.0f, time * 0.05f));
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            // 10. BoxFace
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_box_face"),
                    ShapeGenerator.generateBoxFace()
                            .pos(new Vec3(xPos(), 0, 0))
                            .size(new Vec3(2,2,2))
                            .color(randomColor())
                            .seeThrough(false)
                            .construction(BoxShape.BoxConstructionType.CENTER_AND_DIMENSIONS    )
                             .transform((t) -> {
                                float time = client.getGameTime();
                                t.setShapeWorldRotationDegrees(time * 2, time * 3, time * 1.5f);
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            // 11. BoxWireframe
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_box_wire"),
                    ShapeGenerator.generateBoxWireframe()
                            .aabb(new Vec3(xPos() - 2, 0, -2),
                                    new Vec3(xPos() + 2, 4, 2))
                            .edgeWidth(3.0f)
                            .color(randomColor())
                            .seeThrough(false)
                            .construction(BoxShape.BoxConstructionType.CORNERS)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                t.setShapeWorldRotationDegrees(0, time * 4, 0);
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            // 12. WireframedBox
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "demo_wireframed_box"),
                    ShapeGenerator.generateWireframedBox()
                            .aabb(new Vec3(xPos() - 2, 0, -2),
                                    new Vec3(xPos() + 2, 4, 2))
                            .color(randomColor())
                            .edgeColor(new Color(255, 255, 255, 200))
                            .edgeWidth(2.0f)
                            .seeThrough(false)
                            .lineSeeThrough(false)
                            .construction(BoxShape.BoxConstructionType.CORNERS)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                t.setShapeWorldRotationDegrees(time * 1.5f, time * 2.5f, time*0.5f);
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            added = true;
        });

    }
    private static ArrayList<Vec3> generateSpiral(float centerX, int segments, float radius, float height, float offset) {
        ArrayList<Vec3> points = new ArrayList<>();
        for (int i = 0; i <= segments; i++) {
            float t = i / (float) segments;
            float angle = t * 10 * (float)Math.PI + offset;
            float x = centerX + radius * (float)Math.cos(angle);
            float z = radius * (float)Math.sin(angle);
            float y = t * height;
            points.add(new Vec3(x, y, z));
        }
        return points;
    }

    private static List<Vec3> generateSpiral(float centerX, int segments, float radius, float height) {
        return generateSpiral(centerX, segments, radius, height, 0);
    }
    public static void renderTick(PoseStack matrixStack){
    }
    public static void rotate(BoxLikeShape.BoxTransformer boxTransformer, Shape shape){

        float gameTime = Minecraft.getInstance().level.getGameTime();
        float rotationAngle = (gameTime % 3600) * 2f;
        boxTransformer.setShapeWorldRotationDegrees(rotationAngle, rotationAngle, rotationAngle);
    }
    public static void addEntity(Entity entity) {
        Player player = Minecraft.getInstance().player;
        if (player == null || entity == null || entity == player) return;

        int entityId = entity.getId();
        EntityDimensions dimensions = entity.getDimensions(entity.getPose());
        ShapeManagers.addShape(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "entity_tracker_" + entityId + "/bounding_box"),
                new BoxFaceShape(
                        Shape.RenderingType.BATCH,
                        (transformer) -> {
                            Vec3 entityCenter = entity.position().add(0, dimensions.height() / 2, 0);
                            if (entity.isRemoved()) {
                                transformer.shape.discard();
                                return;
                            }
                            transformer.setShapeWorldPivot(entityCenter);
                        },
                        entity.position().add(0, dimensions.height() / 2, 0),
                        new Vec3(dimensions.width(), dimensions.height(), dimensions.width()),
                        new Color(191, 87, 0, 164),
                        true,
                        BoxShape.BoxConstructionType.CENTER_AND_DIMENSIONS
                )
        );

        ShapeManagers.addShape(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "entity_tracker_" + entityId + "/line"),
                new LineShape(
                        Shape.RenderingType.BATCH,
                        (transformer) -> {
                            if (entity.isRemoved()) {
                                transformer.getShape().discard();
                                return;
                            }
                            if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
                                transformer.setStart(player.getEyePosition().add(player.getLookAngle().scale(2)));
                                //transformer.lineModelInfo.syncLastToTarget();
                                transformer.setEnd(entity.position());
                            }
                        },
                        player.getEyePosition().add(player.getLookAngle().scale(2)),
                        entity.position(),
                        new Color(255, 0, 0, 50),
                        3,
                        true
                )
        );

        added = true;
    }

    public static void removeEntity(int entityId) {
        ShapeManagers.removeShapes(ResourceLocation.fromNamespaceAndPath(MOD_ID,"entity_tracker_" + entityId));
    }
}
