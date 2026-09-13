package ml.mypals.ryansserverrenderingkit.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import ml.mypals.ryansserverrenderingkit.builders.shapeBuilders.ShapeGenerator;
import ml.mypals.ryansserverrenderingkit.collision.RayModelIntersection;
import ml.mypals.ryansserverrenderingkit.shape.basics.CircleLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.box.BoxShape;
import ml.mypals.ryansserverrenderingkit.shape.minecraftBuiltIn.BlockShape;
import ml.mypals.ryansserverrenderingkit.shape.minecraftBuiltIn.ItemShape;
import ml.mypals.ryansserverrenderingkit.shape.minecraftBuiltIn.TextShape;
import ml.mypals.ryansserverrenderingkit.shapeManagers.ShapeManagers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ml.mypals.ryansserverrenderingkit.RyansServerRenderingKit.MOD_ID;
import static ml.mypals.ryansserverrenderingkit.RyansServerRenderingKit.RENDER_PROFILER;

public class Debug {
    public static boolean ENABLE_DEBUG = false;
    private static final Random random = new Random();

    public static Color randomColor() {
        return new Color(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                180
        );
    }

    public static void sendMessage(CommandSourceStack source, String msg) {
        //? if >=1.20 {
        source.sendSuccess(() -> Component.literal(msg), false);
        //?} else {
        /*source.sendSuccess(Component.literal(msg), false);
        *///?}
    }

    public static void registerDebugCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        var root = Commands.literal("rrk")
                .then(Commands.literal("toggle")
                        .executes(ctx -> {
                            boolean val = toggleDebugMode(ctx.getSource());
                            sendMessage(ctx.getSource(), "Ryan's Server Rendering Kit Debug Mode: " + (val ? "§aENABLED" : "§cDISABLED"));
                            return 1;
                        }))
                .then(Commands.literal("clear")
                        .executes(ctx -> {
                            ShapeManagers.removeShapes(Identifier.fromNamespaceAndPath(MOD_ID, "test"));
                            sendMessage(ctx.getSource(), "Cleared all debug shapes.");
                            return 1;
                        }))
                .then(Commands.literal("profile")
                        .executes(ctx -> {
                            RENDER_PROFILER.print();
                            RENDER_PROFILER.reset();
                            sendMessage(ctx.getSource(), "Profile printed to server log.");
                            return 1;
                        }))
                .then(Commands.literal("spawn")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    builder.suggest("all");
                                    builder.suggest("box");
                                    builder.suggest("wire_box");
                                    builder.suggest("line");
                                    builder.suggest("strip_line");
                                    builder.suggest("circle");
                                    builder.suggest("sphere");
                                    builder.suggest("cylinder");
                                    builder.suggest("cone");
                                    builder.suggest("obj");
                                    builder.suggest("text");
                                    builder.suggest("block");
                                    builder.suggest("item");
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String type = StringArgumentType.getString(ctx, "type");
                                    spawnDemoShapes(ctx.getSource(), type);
                                    sendMessage(ctx.getSource(), "Spawned debug shape: " + type);
                                    return 1;
                                })
                        )
                );

        dispatcher.register(root);
        dispatcher.register(Commands.literal("ryansRenderingKit_DEBUG").redirect(dispatcher.getRoot().getChild("rrk")));
    }

    public static void init() {
        // Server side lifecycle handled via ShapeManagers & commands
    }

    private static boolean toggleDebugMode(CommandSourceStack source) {
        ENABLE_DEBUG = !ENABLE_DEBUG;
        if (!ENABLE_DEBUG) {
            ShapeManagers.removeShapes(Identifier.fromNamespaceAndPath(MOD_ID, "test"));
        } else {
            spawnDemoShapes(source, "all");
        }
        return ENABLE_DEBUG;
    }

    public static void spawnDemoShapes(CommandSourceStack source, String type) {
        ServerLevel level = source.getLevel();
        Vec3 origin = source.getPosition();
        Vec3 look = source.getEntity() != null ? source.getEntity().getLookAngle() : new Vec3(0, 0, 1);
        Vec3 basePos = origin.add(look.scale(4.0));

        switch (type.toLowerCase()) {
            case "box" -> spawnBox(level, basePos);
            case "wire_box" -> spawnWireBox(level, basePos);
            case "line" -> spawnLine(level, basePos);
            case "strip_line" -> spawnStripLine(level, basePos);
            case "circle" -> spawnCircle(level, basePos);
            case "sphere" -> spawnSphere(level, basePos);
            case "cylinder" -> spawnCylinder(level, basePos);
            case "cone" -> spawnCone(level, basePos);
            case "obj" -> spawnObj(level, basePos);
            case "text" -> spawnText(level, basePos);
            case "block" -> spawnBlock(level, basePos);
            case "item" -> spawnItem(level, basePos);
            case "all" -> spawnAll(level, basePos);
            default -> sendMessage(source, "Unknown shape type: " + type);
        }
    }

    private static void spawnBox(ServerLevel level, Vec3 pos) {
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_box"),
                ShapeGenerator.generateBox()
                        .level(level)
                        .pos(pos)
                        .size(new Vec3(2, 2, 2))
                        .color(new Color(64, 128, 255, 180))
                        .construction(BoxShape.BoxConstructionType.CENTER_AND_DIMENSIONS)
                        .transform(t -> {
                            /*long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                            t.setShapeWorldRotationDegrees(time * 2, time * 3, time * 1.5f);*/
                            RayModelIntersection.HitResult hitResult = t.shape.isAnyPlayerLookingAt(5);
                            if(hitResult.hit){
                                t.shape.setBaseColor(new Color(64, 64, 64, 180));
                            }else {
                                new Color(64, 128, 255, 180);
                            }
                        })
                        .build()
        );
    }

    private static void spawnWireBox(ServerLevel level, Vec3 pos) {
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_wire_box"),
                ShapeGenerator.generateBox()
                        .level(level)
                        .aabb(pos.add(-1.5, -1.5, -1.5), pos.add(1.5, 1.5, 1.5))
                        .color(Color.WHITE)
                        .renderFace(false)
                        .renderWireframe(true)
                        .wireframeWidth(0.05f)
                        .construction(BoxShape.BoxConstructionType.CORNERS)
                        .transform(t -> {
                            long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                            t.setShapeWorldRotationDegrees(time * 1.5f, time * 2.5f, 0);
                        })
                        .build()
        );
    }

    private static void spawnObj(ServerLevel level, Vec3 pos) {
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_obj"),
                ShapeGenerator.generateObjModel()
                        .level(level)
                        .pos(pos)
                        .model(Identifier.fromNamespaceAndPath(MOD_ID, "models/monkey.obj"))
                        .color(new Color(100, 180, 255, 190))
                        .renderFace(true)
                        .renderWireframe(true)
                        .wireframeWidth(0.025F)
                        .transform(t -> {
                            long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                            t.setShapeWorldRotationDegrees(0, time * 2, 0);
                        })
                        .build()
        );
    }

    private static void spawnLine(ServerLevel level, Vec3 pos) {
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_line"),
                ShapeGenerator.generateLine()
                        .level(level)
                        .start(pos.add(-2, 0, 0))
                        .end(pos.add(2, 2, 0))
                        .lineWidth(0.05f)
                        .color(Color.GREEN)
                        .transform(t -> {
                            long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                            t.setEnd(pos.add(2, 2 + Math.sin(time * 0.1) * 1.5, 0));
                        })
                        .build()
        );
    }

    private static void spawnStripLine(ServerLevel level, Vec3 pos) {
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_strip_line"),
                ShapeGenerator.generateStripLine()
                        .level(level)
                        .vertexes(generateSpiral(pos, 40, 1.5f, 3.0f, 0))
                        .lineWidth(0.05f)
                        .color(Color.CYAN)
                        .transform(t -> {
                            long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                            t.setShapeWorldRotationDegrees(0, time * 4, 0);
                        })
                        .build()
        );
    }

    private static void spawnCircle(ServerLevel level, Vec3 pos) {
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_circle"),
                ShapeGenerator.generateLineCircle()
                        .level(level)
                        .pos(pos)
                        .radius(2.0f)
                        .segments(32)
                        .lineWidth(0.05f)
                        .color(Color.MAGENTA)
                        .axis(CircleLikeShape.CircleAxis.Y)
                        .transform(t -> {
                            long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                            t.setShapeWorldRotationDegrees(time * 3, time * 2, 0);
                        })
                        .build()
        );
    }

    private static void spawnSphere(ServerLevel level, Vec3 pos) {
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_sphere"),
                ShapeGenerator.generateSphere()
                        .level(level)
                        .pos(pos)
                        .radius(1.8f)
                        .segments(12)
                        .color(new Color(255, 60, 60))
                        .seeThrough(false)
                        .renderWireframe(true)
                        .transform(t -> {
                            long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                            t.setShapeWorldRotationDegrees(time * 2, time * 3, 0);
                        })
                        .build()
        );
    }

    private static void spawnCylinder(ServerLevel level, Vec3 pos) {
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_cylinder"),
                ShapeGenerator.generateCylinder()
                        .level(level)
                        .pos(pos)
                        .radius(1.5f)
                        .height(3.0f)
                        .segments(12)
                        .color(Color.YELLOW)
                        .renderWireframe(true)
                        .axis(CircleLikeShape.CircleAxis.Y)
                        .transform(t -> {
                            long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                            t.setShapeWorldRotationDegrees(0, time * 3, 0);
                        })
                        .build()
        );
    }

    private static void spawnCone(ServerLevel level, Vec3 pos) {
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_cone"),
                ShapeGenerator.generateCone()
                        .level(level)
                        .pos(pos)
                        .radius(1.5f)
                        .height(3.0f)
                        .segments(12)
                        .color(Color.ORANGE)
                        .renderWireframe(true)
                        .axis(CircleLikeShape.CircleAxis.Y)
                        .transform(t -> {
                            long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                            t.setShapeWorldRotationDegrees(0, time * 3, 0);
                        })
                        .build()
        );
    }

    private static void spawnText(ServerLevel level, Vec3 pos) {
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_text"),
                ShapeGenerator.generateText()
                        .level(level)
                        .pos(pos)
                        .billBoardMode(TextShape.BillBoardMode.ALL)
                        .seeThrough(true)
                        .shadow(true)
                        .texts("§6§lRyan's Server Rendering Kit", "§aServer-Side DisplayEntity", "§bHigh Performance")
                        .textColors(Color.ORANGE, Color.GREEN, Color.CYAN)
                        .transform(t -> {
                            long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                            t.setShapeMatrixPivot(new Vec3(0, Math.sin(time * 0.1) * 0.3, 0));
                        })
                        .build()
        );
    }

    private static void spawnBlock(ServerLevel level, Vec3 pos) {
        BlockShape blockShape = ShapeGenerator.generateBlock()
                .level(level)
                .pos(pos)
                .block(Blocks.AMETHYST_BLOCK.defaultBlockState())
                .transform(t -> {
                    long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                    t.setShapeWorldRotationDegrees(0, time * 4, 0);
                })
                .build();
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_block"),
                blockShape
        );
    }

    private static void spawnItem(ServerLevel level, Vec3 pos) {
        ItemShape itemShape = ShapeGenerator.generateItem()
                .level(level)
                .pos(pos)
                .item(Items.NETHER_STAR.getDefaultInstance())
                .itemDisplayContext(ItemDisplayContext.GROUND)
                .transform(t -> {
                    long time = t.shape.getLevel() != null ? t.shape.getLevel().getGameTime() : 0;
                    t.setShapeWorldRotationDegrees(0, time * 5, 0);
                    t.setShapeMatrixPivot(new Vec3(0, Math.sin(time * 0.1) * 0.25, 0));
                })
                .build();
        ShapeManagers.addShape(
                Identifier.fromNamespaceAndPath(MOD_ID, "test/demo_item"),
                itemShape
        );
    }

    private static void spawnAll(ServerLevel level, Vec3 pos) {
        double spacing = 3.5;
        int i = 0;
        spawnBox(level, pos.add((i++) * spacing, 0, 0));
        spawnWireBox(level, pos.add((i++) * spacing, 0, 0));
        spawnLine(level, pos.add((i++) * spacing, 0, 0));
        spawnStripLine(level, pos.add((i++) * spacing, 0, 0));
        spawnCircle(level, pos.add((i++) * spacing, 0, 0));
        spawnSphere(level, pos.add((i++) * spacing, 0, 0));
        spawnCylinder(level, pos.add((i++) * spacing, 0, 0));
        spawnCone(level, pos.add((i++) * spacing, 0, 0));
        spawnText(level, pos.add((i++) * spacing, 0, 0));
        spawnBlock(level, pos.add((i++) * spacing, 0, 0));
        spawnItem(level, pos.add((i++) * spacing, 0, 0));
    }

    private static List<Vec3> generateSpiral(Vec3 center, int segments, float radius, float height, float offset) {
        List<Vec3> points = new ArrayList<>();
        for (int i = 0; i <= segments; i++) {
            float t = i / (float) segments;
            float angle = t * 6 * (float) Math.PI + offset;
            float x = (float) center.x + radius * (float) Math.cos(angle);
            float z = (float) center.z + radius * (float) Math.sin(angle);
            float y = (float) center.y + t * height;
            points.add(new Vec3(x, y, z));
        }
        return points;
    }
}
