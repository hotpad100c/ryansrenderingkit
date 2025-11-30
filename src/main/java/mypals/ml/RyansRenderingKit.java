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
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
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
        WorldRenderEvents.LAST.register(this::handleRenderLast);
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

    private void handleRenderLast(WorldRenderContext ctx) {
        MainRender.render(ctx.matrixStack(), ctx.camera(), ctx.tickCounter().getGameTimeDeltaPartialTick(true));
    }
}