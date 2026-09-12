package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.minecraftBuiltIn.EntityShape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.world.entity.Entity;

public class EntityShapeBuilder extends BaseBuilder<EntityShapeBuilder, DefaultTransformer> {

    private Entity entity;
    private int light = 0x00F000F0;

    public EntityShapeBuilder entity(Entity entity) {
        this.entity = entity;
        return this;
    }

    public EntityShapeBuilder light(int light) {
        this.light = light;
        return this;
    }

    @Override
    public EntityShape build() {
        EntityShape shape = new EntityShape(
                getTransformer(),
                center,
                entity,
                light
        );
        return applyCommon(shape);
    }
}