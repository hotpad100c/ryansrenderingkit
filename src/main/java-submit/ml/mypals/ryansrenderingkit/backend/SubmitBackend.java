package ml.mypals.ryansrenderingkit.backend;

import ml.mypals.ryansrenderingkit.render.renderTypes.RyansRenderingKitRenderTypes;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.Color;

/**
 * Spike: proves a shape can reach the screen through the engine's batcher on 26.1+.
 * <p>
 * The hook matters as much as the API. {@code LevelRenderEvents.END_MAIN} -- what the mod hooks
 * today -- is injected at the return of {@code LevelRenderer.lambda$addMainPass$0}, i.e. after
 * {@code executeTranslucent()} has already drained the submit storage. Submissions made there are
 * a frame late. {@code COLLECT_SUBMITS} is injected at the return of
 * {@code LevelRenderer.submitFeatures}, before any submit geometry is drawn, and is the documented
 * place to add your own.
 */
public final class SubmitBackend {

    /** Spike-only: draws a 2-block red see-through line at the player's feet. */
    public static boolean ENABLED = true;

    private static final SubmitVertexBuilder BUILDER =
            new SubmitVertexBuilder(new Matrix4f(), true);

    private SubmitBackend() {
    }

    public static void init() {
        LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
            if (!ENABLED) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            Camera camera = mc.gameRenderer.getMainCamera();
            Vec3 cameraPos = camera.position();
            Vec3 anchor = mc.player.position();

            // Level-render space is camera-relative.
            Vector3f bottom = new Vector3f(
                    (float) (anchor.x - cameraPos.x),
                    (float) (anchor.y - cameraPos.y),
                    (float) (anchor.z - cameraPos.z));
            Vector3f top = new Vector3f(bottom.x, bottom.y + 2.0f, bottom.z);
            Vector3f normal = new Vector3f(0.0f, 1.0f, 0.0f);

            BUILDER.submit(
                    context.submitNodeCollector(),
                    context.poseStack(),
                    RyansRenderingKitRenderTypes.SEE_THROUGH_LINES,
                    0,
                    builder -> {
                        builder.putVertex(bottom, Color.RED, normal, 4.0f);
                        builder.putVertex(top, Color.RED, normal, 4.0f);
                    });
        });
    }
}
