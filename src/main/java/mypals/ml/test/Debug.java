package mypals.ml.test;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.CommandDispatcher;
import mypals.ml.builders.shapeBuilders.ShapeGenerator;
import mypals.ml.shape.basics.BoxLikeShape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.basics.core.TwoPointsLineShape;
import mypals.ml.shape.box.BoxFaceShape;
import mypals.ml.shape.box.BoxShape;
import mypals.ml.shape.Shape;
import mypals.ml.shape.line.LineShape;
import mypals.ml.shape.line.StripLineShape;
import mypals.ml.shape.minecraftBuiltIn.BlockShape;
import mypals.ml.shape.minecraftBuiltIn.EntityShape;
import mypals.ml.shape.minecraftBuiltIn.ItemShape;
import mypals.ml.shape.minecraftBuiltIn.TextShape;
import mypals.ml.shapeManagers.ShapeManagers;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static mypals.ml.RyansRenderingKit.MOD_ID;
import static mypals.ml.RyansRenderingKit.RENDER_PROFILER;

public class Debug {
    public static boolean added =true;
    public static Random random = new Random();
    public static float spacing = 8.0f;
    public static int index = 0;
    public static boolean ENABLE_DEBUG = false;

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
    public static void registerDebugCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        dispatcher.register(
                ClientCommandManager.literal("ryansRenderingKit_DEBUG")
                        .then(ClientCommandManager.literal("toggle")
                                .executes(ctx -> {
                                    boolean newValue = toggleDebugMode();
                                    ctx.getSource().sendFeedback(
                                            Component.nullToEmpty("Ryan's Rendering Kit Debug Mode: " + (newValue ?
                                                    "§aENABLED"
                                                    : "§cDISABLED")));
                                    return 1;
                                })
                        ).then(ClientCommandManager.literal("reload")
                            .executes(ctx -> {
                                added = false;
                                ctx.getSource().sendFeedback(
                                        Component.nullToEmpty("Ryan's Rendering Kit Debug Shapes Reloaded."));
                                return 0;
                            })
                        ).then(ClientCommandManager.literal("profile")
                                .executes(ctx -> {
                                    RENDER_PROFILER.print();
                                    RENDER_PROFILER.reset();
                                    ctx.getSource().sendFeedback(
                                            Component.nullToEmpty("Ryan's Rendering Kit Profile printed to the console."));
                                    return 0;
                                })
                        )
        );
    }
    private static boolean toggleDebugMode(){
        ENABLE_DEBUG = !ENABLE_DEBUG;
        if(!ENABLE_DEBUG){
            added = true;
            ShapeManagers.removeShapes(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test")
            );
        }else{
            added = false;
        }
        return ENABLE_DEBUG;
    }
    public static void init(){
        ClientTickEvents.START_WORLD_TICK.register(client -> {
            if (added || !ENABLE_DEBUG) return;
            index = 0;
            ShapeManagers.removeShapes(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test")
            );

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_face_circle"),
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

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_line_circle"),
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
                                 t.setShapeLocalRotationDegrees(0,time*4,0);
                                 t.setShapeWorldRotationDegrees(time*4, 0, 0);
                            })
                            .build(Shape.RenderingType.BUFFERED)
            );

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_sphere"),
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
                        if(t.shape.isPlayerLookingAt().hit){
                            t.shape.baseColor = new Color(255, 234, 0,100);
                        }else{
                            t.shape.baseColor = new Color(255, 0, 251,100);
                        }
                    })
                    .build(Shape.RenderingType.BATCH);

            s2.addChild(s3);
            s1.addChild(s2);

            ShapeManagers.addShape(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_sphere_2"),
                s1
            );
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_sphere_2_child"),
                    s2
            );
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_sphere_3_child"),
                    s3
            );

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_obj_model"),
                    ShapeGenerator.generateObjModel()
                            .pos(new Vec3(xPos(), 0, 0))
                            .model(ResourceLocation.fromNamespaceAndPath(MOD_ID, "models/monkey.obj"))
                            .color(randomColor())
                            .seeThrough(false)
                            .transform((transformer) -> {
                                float time = client.getGameTime();
                                transformer.setShapeWorldRotationDegrees(0, time * 5, 0);
                                Shape shape = transformer.shape;
                                if (shape.isPlayerLookingAt().hit) {
                                    if (Minecraft.getInstance().mouseHandler.isLeftPressed()) {
                                        Player player = Minecraft.getInstance().player;
                                        if (!shape.getCustomData("isHolding", false)) {
                                            shape.putCustomData("isHolding", true);
                                            double distance = transformer
                                                    .getShapeWorldPivot(true)
                                                    .distanceTo(player.getEyePosition());
                                            shape.putCustomData("distance", distance);
                                        }
                                        transformer.setShapeWorldPivot(
                                                player.getEyePosition(transformer.getTickDelta())
                                                        .add(player.getLookAngle().scale(shape.getCustomData("distance", 5.0)))
                                                        .add(0, -1, 0)
                                        );
                                        transformer.world.position.syncLastToTarget();
                                    } else {
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

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_obj_outline"),
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

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_cone"),
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

                                double f = (Math.sin(time * 0.1) + 1) / 2;
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

            float fixedCylX = xPos();
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_cylinder"),
                    ShapeGenerator.generateCylinder()
                            .pos(new Vec3(fixedCylX, 0,0))
                            .radius(1.5f)
                            .height(4.0f)
                            .segments(32)
                            .axis(CircleLikeShape.CircleAxis.Z)
                            .color(randomColor())
                            .seeThrough(false)
                             .transform((t) -> {
                                float time = client.getGameTime();

                                double offset = (Math.sin(time * 0.05));
                                t.setShapeLocalPivot(new Vec3(offset * 3,0,0));
                                t.setShapeMatrixPivot(new Vec3(0,offset * -2,0));
                                t.setShapeWorldPivot(new Vec3(fixedCylX,0,offset * 1));
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_cone_wire"),
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

                                double f = (Math.sin(time * 0.1) + 1) / 2;
                                int seg = (int)(3 + f * 20-3);

                                t.setSegment(Math.max(3,seg));
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_cylinder_wire"),
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
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            float linex  = xPos();
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_line"),
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

            float cx = xPos();
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_strip_line"),
                    ShapeGenerator.generateStripLine()
                            .vertexes(generateSpiral(xPos(), 100, 2.0f, 5.0f))
                            .lineWidth(2.0f)
                            .color(randomColor())
                            .seeThrough(false)
                             .transform((t) -> {
                                float time = client.getGameTime();
                                ((StripLineShape)t.getShape()).setVertexes(generateSpiral(cx, 100, 2.0f, 5.0f, time * 0.05f));
                            })
                            .build(Shape.RenderingType.BATCH)
            );

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_box_face"),
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

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_box_wire"),
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

            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_wireframed_box"),
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
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_text1"),
                    ShapeGenerator.generateText()
                            .pos(new Vec3(xPos(), 0, 0))
                            .billBoardMode(TextShape.BillBoardMode.ALL)
                            .seeThrough(false)
                            .shadow(true)
                            .outline(true)
                            .texts("§b§l!TEST!", "§e你好", "§aAWA", "§dBillBoardMode.ALL")
                            .textColors(Color.CYAN, Color.YELLOW, Color.GREEN, Color.MAGENTA)

                            .transform(t -> t.setShapeMatrixPivot(
                                    new Vec3(0, Math.sin(Minecraft.getInstance().level.getGameTime() * 0.05) * 0.3, 0)
                            ))

                            .build()
            );
            generateLineTracker();

            BlockShape blockShape = ShapeGenerator.generateBlock()
                    .pos(new Vec3(xPos(), 0, 0))
                    .transform(
                    (t)->{handleMouseGrab(Minecraft.getInstance().player,
                            t.shape,t);})
                    .block(Blocks.GLASS.defaultBlockState())
                    .light(LightTexture.FULL_BLOCK)
                    .build();
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_block_shape"),
                    blockShape
            );

            ItemShape itemShape = new ItemShape(
                    (t)->{handleMouseGrab(Minecraft.getInstance().player,
                            t.shape,t);},
                    new Vec3(xPos(), 0, 0),
                    Items.DRAGON_EGG.getDefaultInstance(), ItemDisplayContext.GROUND, LightTexture.FULL_BLOCK);
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_item_shape"),
                    itemShape
            );

            EntityShape entityShape = new EntityShape(
                    (t)->{
                        handleMouseGrab(Minecraft.getInstance().player, t.shape,t);
                        float time = client.getGameTime();
                        t.setShapeWorldRotationDegrees(time * 2, time * 3, time * 1.5f);
                    },
                    new Vec3(xPos(), 0, 0),
                    Minecraft.getInstance().player, LightTexture.FULL_BLOCK);
            ShapeManagers.addShape(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_entity_shape"),
                    entityShape
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
    public static void generateLineTracker(){
        Minecraft mc = Minecraft.getInstance();
        Shape anchor1 = ShapeGenerator.generateSphere()
                .pos(new Vec3(xPos(), 0,0))
                .radius(0.1f)
                .segments(16)
                .color(new Color(0,0,1,0.5f))
                .seeThrough(false)
                .transform((transformer) -> {
                    Shape shape = transformer.getShape();
                    handleMouseGrab(mc.player,shape,transformer);

                })
                .build(Shape.RenderingType.BATCH);
        Shape anchor2 = ShapeGenerator.generateSphere()
                .pos(new Vec3(xPos(), 0,0))
                .radius(0.1f)
                .segments(16)
                .color(new Color(1,0,0,0.5f))
                .seeThrough(false)
                .transform((transformer) -> {
                    Shape shape = transformer.getShape();
                    handleMouseGrab(mc.player,shape,transformer);
                })
                .build(Shape.RenderingType.BATCH);

        Shape line = ShapeGenerator.generateLine()
                .pos(new Vec3(0, 0,0))
                .start(anchor1.transformer.getShapeWorldPivot(false))
                .end(anchor2.transformer.getShapeWorldPivot(false))
                .lineWidth(2.1f)
                .color(Color.WHITE)
                .seeThrough(false)
                .transform((transformer) -> {
                    transformer.setStart(anchor1.transformer.getShapeWorldPivot(true));
                    transformer.setEnd(anchor2.transformer.getShapeWorldPivot(true));
                })
                .build(Shape.RenderingType.BATCH);

        Shape text = ShapeGenerator.generateText()
                .pos(new Vec3(xPos(), 0, 0))
                .billBoardMode(TextShape.BillBoardMode.ALL)
                .seeThrough(false)
                .shadow(true)
                .outline(false)
                .texts("-")
                .textColors(Color.CYAN, Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .transform(t -> {
                    Vec3 start = ((TwoPointsLineShape.TwoPointsLineTransformer)line.transformer).getStart(true);
                    Vec3 end   = ((TwoPointsLineShape.TwoPointsLineTransformer)line.transformer).getEnd(true);
                    Entity player = Minecraft.getInstance().player;
                    if (player == null) return;
                    Vec3 eyePos = player.getPosition(t.getTickDelta()).add(0, player.getEyeHeight(), 0);
                    Vec3 lineDir = end.subtract(start);
                    double lineLength = lineDir.length();
                    if (lineLength < 0.001) {
                        t.setShapeMatrixPivot(start.add(0, 0.1, 0));
                        return;
                    }
                    Vec3 toEye = eyePos.subtract(start);
                    double proj = toEye.dot(lineDir) / (lineLength * lineLength);
                    proj = Mth.clamp(proj, 0.0, 1.0);
                    Vec3 closestPoint = start.add(lineDir.scale(proj));
                    Vec3 labelOffset = new Vec3(0, 0.3, 0);
                    Vec3 towardsPlayer = eyePos.subtract(closestPoint).normalize().scale(0.15);
                    Vec3 finalPos = closestPoint.add(labelOffset).add(towardsPlayer.yRot((float)Math.toRadians(15)));
                    t.setShapeWorldPivot(finalPos);
                    t.world.syncLastToTarget();
                    double d = anchor1.transformer.getWorldPivot().distanceTo(anchor2.transformer.getWorldPivot());
                    ((TextShape)t.shape).setText(1,"Distance : " + String.format("%.2f", d));
                })
                .build();

        ShapeManagers.addShape(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_a1"),
            anchor1
        );
        ShapeManagers.addShape(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_a2"),
                anchor2
        );
        ShapeManagers.addShape(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_l1"),
                line
        );
        ShapeManagers.addShape(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "test/demo_t1"),
                text
        );
    }
    public static void handleMouseGrab(Player player, Shape shape, DefaultTransformer transformer) {
        Minecraft mc = Minecraft.getInstance();
        boolean isLeftPressed = mc.mouseHandler.isLeftPressed();
        boolean wasHolding = shape.getCustomData("isHolding", false);
        boolean isLookingAt = shape.isPlayerLookingAt().hit;
        if(!wasHolding) shape.putCustomData("color",shape.baseColor);
        boolean shouldHold = wasHolding || (isLeftPressed && isLookingAt);
        if (shouldHold && !wasHolding) {
            double distance = transformer.getShapeWorldPivot(true)
                    .distanceTo(player.getEyePosition());
            shape.putCustomData("grabDistance", distance);
            shape.putCustomData("isHolding", true);
        }
        if (isLeftPressed && shape.getCustomData("isHolding", false)) {
            double savedDistance = shape.getCustomData("grabDistance", 4.0);
            Vec3 eyePos = player.getEyePosition(transformer.getTickDelta());
            Vec3 look = player.getLookAngle();

            Vec3 targetPos = eyePos.add(look.scale(savedDistance));

            transformer.setShapeWorldPivot(targetPos);
            transformer.world.position.syncLastToTarget();
            shape.baseColor = new Color(255, 255, 255, 200);
        }
        else if (!isLeftPressed && wasHolding) {
            shape.putCustomData("isHolding", false);
            shape.putCustomData("grabDistance", null);
            shape.baseColor = shape.getCustomData("color",Color.WHITE);
        }
        else if (!isLeftPressed) {
            shape.baseColor = shape.getCustomData("color",Color.WHITE);
        }
      }
    public static void rotate(BoxLikeShape.BoxTransformer boxTransformer, Shape shape){

        float gameTime = Minecraft.getInstance().level.getGameTime();
        float rotationAngle = (gameTime % 3600) * 2f;
        boxTransformer.setShapeWorldRotationDegrees(rotationAngle, rotationAngle, rotationAngle);
    }
    public static void addEntity(Entity entity) {
        Player player = Minecraft.getInstance().player;
        if (player == null || entity == null || entity == player || !ENABLE_DEBUG) return;

        int entityId = entity.getId();
        EntityDimensions dimensions = entity.getDimensions(entity.getPose());
        ShapeManagers.addShape(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "entity_tracker_" + entityId + "/bounding_box"),
                new BoxFaceShape(
                        Shape.RenderingType.BATCH,
                        (transformer) -> {
                            Vec3 entityCenter = entity.position().add(0, dimensions.height() / 2, 0);
                            if (entity.isRemoved() || !ENABLE_DEBUG) {
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
                            if (entity.isRemoved() || !ENABLE_DEBUG) {
                                transformer.getShape().discard();
                                return;
                            }
                            if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
                                ((LineShape)transformer.getShape()).forceSetStart(player.getEyePosition(transformer.getTickDelta()).add(player.getLookAngle().scale(2)));
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
