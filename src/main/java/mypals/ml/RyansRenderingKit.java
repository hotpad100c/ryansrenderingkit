package mypals.ml;

import mypals.ml.builderManager.BuilderManagers;
import mypals.ml.render.MainRender;
import mypals.ml.shapeManagers.ShapeManagers;
import mypals.ml.shapeManagers.VertexBuilderGetter;
import mypals.ml.test.Debug;
import mypals.ml.utils.SimpleRenderProfiler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=1.21.10 {
/*import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
*///?} else if =1.21.9 {
/*import mypals.ml.render.nine.WorldRenderContext;
*///?} else {
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
//?}
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mypals.ml.test.Debug.registerDebugCommands;

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

        //? if >= 1.21.10 {
        /*WorldRenderEvents.END_MAIN.register(this::handleRenderLast);
        *///?} else if > 1.21.1 && < 1.21.9 {
        /*WorldRenderEvents.LAST.register(this::handleRenderLast);
        *///?} else if <= 1.21.1 {
        WorldRenderEvents.AFTER_ENTITIES.register(this::handleRenderLast);
        //?}
        ClientTickEvents.END_WORLD_TICK.register(c -> {
            if (c.getGameTime() % 20 == 0) {
                RENDER_PROFILER.reset();
            }
            endOfWorldTick = true;
        });
        ClientTickEvents.START_WORLD_TICK.register(c -> {
            endOfWorldTick = false;
            RENDER_PROFILER.push("syncTransforms");
            ShapeManagers.syncShapeTransform();
            RENDER_PROFILER.pop();
        });
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) ->
                        registerDebugCommands(dispatcher)
        );
        Debug.init();
    }

    public static boolean isEndOfWorldTick() {
        return endOfWorldTick;
    }

    public /*? if = 1.21.9 {*//*static*//*?}*/ void handleRenderLast(WorldRenderContext ctx) {
        MainRender.render(
                //? >=1.21.9 {
                /*ctx.matrices(), /^? if != 1.21.9 {^/ctx.gameRenderer().getMainCamera()/^?} else {^//^ctx.camera()^//^?}^/,
                *///?} else {
                ctx.matrixStack(), ctx.camera(),
                //?}
                //? >=1.21.6 {
                /*Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true)
                *///?} else if > 1.20.6 {
                /*ctx.tickCounter().getGameTimeDeltaPartialTick(true)
                *///?} else {
                ctx.tickDelta()
                //?}
                );
    }
}