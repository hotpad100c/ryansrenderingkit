package ml.mypals.ryansrenderingkit;

import ml.mypals.ryansrenderingkit.builderManager.BuilderManagers;
import ml.mypals.ryansrenderingkit.render.MainRender;
import ml.mypals.ryansrenderingkit.shapeManagers.ShapeManagers;
import ml.mypals.ryansrenderingkit.shapeManagers.VertexBuilderGetter;
import ml.mypals.ryansrenderingkit.test.Debug;
import ml.mypals.ryansrenderingkit.utils.SimpleRenderProfiler;
import net.fabricmc.api.ModInitializer;
//? if >1.18.2 {
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//?} else {
/*import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
 *///?}
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >= 26.1 {
/*import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
*///?} else if >=1.21.10 {
/*import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
*///?} else if =1.21.9 {
/*import ml.mypals.ryansrenderingkit.render.nine.WorldRenderContext;
 *///?} else {
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
//?}
//? if >= 1.21.6 {
/*import net.minecraft.client.Minecraft;
 *///?}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ml.mypals.ryansrenderingkit.test.Debug.registerDebugCommands;

public class RyansRenderingKit implements ModInitializer {
    public static final String MOD_ID = "ryansrenderingkit";
    public static final SimpleRenderProfiler RENDER_PROFILER = new SimpleRenderProfiler();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean endOfWorldTick = false;

    @Override
    public void onInitialize() {
        BuilderManagers.init();
        ShapeManagers.init();
        VertexBuilderGetter.init();

        //? if >= 26.1 {
        /*LevelRenderEvents.END_MAIN.register(this::handleRenderLast);
         *///?} else if >= 1.21.10 {
        /*WorldRenderEvents.END_MAIN.register(this::handleRenderLast);
         *///?} else if > 1.21.1 && < 1.21.9 {
        WorldRenderEvents.LAST.register(this::handleRenderLast);
        //?} else if <= 1.21.1 {
        /*WorldRenderEvents.AFTER_ENTITIES.register(this::handleRenderLast);
         *///?}

        //? if >= 26.1 {
        /*ClientTickEvents.END_LEVEL_TICK.register(c -> {
         *///?} else {
        ClientTickEvents.END_WORLD_TICK.register(c -> {
            //?}
            if (c.getGameTime() % 20 == 0) {
                RENDER_PROFILER.reset();
            }
            endOfWorldTick = true;
        });

        //? if >= 26.1 {
        /*ClientTickEvents.START_LEVEL_TICK.register(c -> {
         *///?} else {
        ClientTickEvents.START_WORLD_TICK.register(c -> {
            //?}
            endOfWorldTick = false;
            RENDER_PROFILER.push("syncTransforms");
            ShapeManagers.syncShapeTransform();
            RENDER_PROFILER.pop();
        });

        //? if >1.18.2 {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) ->
                        registerDebugCommands(dispatcher)
        );
        //?} else {
        /*registerDebugCommands(ClientCommandManager.DISPATCHER);
         *///?}
        Debug.init();
    }

    public static boolean isEndOfWorldTick() {
        return endOfWorldTick;
    }

    public /*? if = 1.21.9 {*//*static*//*?}*/ void handleRenderLast(/*? if >= 26.1 {*//*LevelRenderContext*//*?} else {*/WorldRenderContext/*?}*/ ctx) {
        MainRender.render(
                //? >=1.21.9 {
                /*/^? if >= 26.1 {^//^ctx.poseStack()^//^?} else {^/ctx.matrices()/^?}^/, /^? if != 1.21.9 {^/ctx.gameRenderer().getMainCamera()/^?} else {^//^ctx.camera()^//^?}^/,
                *///?} else {
                ctx.matrixStack(), ctx.camera(),
                 //?}
                //? >= 1.21.6 {
                /*Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true)
                 *///?} else if > 1.20.6 {
                ctx.tickCounter().getGameTimeDeltaPartialTick(true)
                //?} else {
                /*ctx.tickDelta()
                 *///?}
        );
    }
}