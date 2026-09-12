package ml.mypals.ryansserverrenderingkit;

import ml.mypals.ryansserverrenderingkit.shapeManagers.ShapeManagers;
import ml.mypals.ryansserverrenderingkit.test.Debug;
import ml.mypals.ryansserverrenderingkit.utils.SimpleRenderProfiler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RyansServerRenderingKit implements ModInitializer {
    public static final String MOD_ID = "ryansserverrenderingkit";
    public static final SimpleRenderProfiler RENDER_PROFILER = new SimpleRenderProfiler();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ShapeManagers.init();

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        Debug.registerDebugCommands(dispatcher)
        );
        Debug.init();
        LOGGER.info("Ryan's Server Rendering Kit (Server-Side DisplayEntity) initialized.");
    }
}