package ml.mypals.ryansrenderingkit.render.nine;
//? =1.21.9 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
*///?}

public class WorldRenderContext {
    //? =1.21.9 {
    /*public PoseStack poseStack;
    public Camera camera;
    public DeltaTracker deltaTracker;

    public WorldRenderContext(
            PoseStack poseStack,
            Camera camera,
            DeltaTracker deltaTracker) {
        this.poseStack = poseStack;
        this.camera = camera;
        this.deltaTracker = deltaTracker;
    }
    public PoseStack matrices(){
        return poseStack;
    }
    public Camera camera(){
        return camera;
    }
    *///?}

}