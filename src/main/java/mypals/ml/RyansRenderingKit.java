package mypals.ml;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builderManager.BuilderManagers;
import mypals.ml.render.InformationRender;
import mypals.ml.shapeManagers.ShapeBuilderGetter;
import mypals.ml.shapeManagers.ShapeManagers;
import mypals.ml.test.Tester;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RyansRenderingKit implements ModInitializer {
	public static final String MOD_ID = "ryansrenderingkit";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		BuilderManagers.init();
		ShapeManagers.init();
		ShapeBuilderGetter.init();
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::handleRenderFabulous);
		WorldRenderEvents.LAST.register(this::handleRenderLast);

		Tester.init();

		LOGGER.info("Hello Fabric world!");
	}
	private void handleRenderFabulous(WorldRenderContext ctx) {
		if (!ctx.advancedTranslucency()) return;
		InformationRender.render(ctx.matrixStack(), ctx.camera());
	}

	private void handleRenderLast(WorldRenderContext ctx) {
		if (ctx.advancedTranslucency()) return;
		InformationRender.render(ctx.matrixStack(), ctx.camera());
	}
}