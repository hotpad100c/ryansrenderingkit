package mypals.ml;

import mypals.ml.builderManager.BuilderManagers;
import mypals.ml.render.InformationRender;
import mypals.ml.shapeManagers.VertexBuilderGetter;
import mypals.ml.shapeManagers.ShapeManagers;
import mypals.ml.test.Tester;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mypals.ml.test.Tester.registerDebugCommands;

public class RyansRenderingKit implements ModInitializer {
	public static final String MOD_ID = "ryansrenderingkit";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		BuilderManagers.init();
		ShapeManagers.init();
		VertexBuilderGetter.init();
		WorldRenderEvents.LAST.register(this::handleRenderLast);
		ClientTickEvents.END_WORLD_TICK.register(c-> ShapeManagers.syncShapeTransform());
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			registerDebugCommands(dispatcher);
		});
		Tester.init();
	}
	private void handleRenderLast(WorldRenderContext ctx) {
		InformationRender.render(ctx.matrixStack(), ctx.camera(),ctx.tickCounter().getGameTimeDeltaPartialTick(true));
	}
}