package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.minecraftBuiltIn.EntityShape;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
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