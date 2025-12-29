package ml.mypals.ryansrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.minecraftBuiltIn.EntityShape;
import ml.mypals.ryansrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.Entity;

public class EntityShapeBuilder extends BaseBuilder<EntityShapeBuilder, DefaultTransformer> {

    private Entity entity;
    private int light = LightTexture.FULL_BRIGHT;

    public EntityShapeBuilder entity(Entity entity) {
        this.entity = entity;
        return this;
    }

    public EntityShapeBuilder light(int light) {
        this.light = light;
        return this;
    }

    @Override
    @Deprecated(since = "type is ignored, use build() instead")
    public EntityShape build(Shape.RenderingType type) {
        return build();
    }

    public EntityShape build() {
        return new EntityShape(
                getTransformer(),
                center,
                entity,
                light
        );
    }

}