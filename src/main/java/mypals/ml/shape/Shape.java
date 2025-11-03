package mypals.ml.shape;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.basics.tags.ExtractableShape;
import mypals.ml.shapeManagers.ShapeManagers;
import mypals.ml.transform.Vec3dTransformer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import mypals.ml.transform.QuaternionTransformer;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.BiConsumer;

public abstract class Shape {
    public enum RenderingType {
        IMMEDIATE,   // Immediately rendered
        BATCH,      // Stored in bufferBuilder, batch uploads every frame
        BUFFERED    // Stored in VBO
    }
    public ResourceLocation id;
    public final RenderingType type;
    public BiConsumer<DefaultTransformer,Shape> transformFunction;
    public DefaultTransformer transformer;
    public boolean seeThrough;
    public Vec3 centerPoint = Vec3.ZERO;

    protected Shape(RenderingType type,BiConsumer<? super DefaultTransformer, Shape> transform) {
        this(type);
        this.transformFunction = (transformer, shape) -> transform.accept(this.transformer,shape);
    }
    protected Shape(RenderingType type) {
        this(type,false);
    }
    protected Shape(RenderingType type,boolean seeThrough) {
        this.type = type;
        this.seeThrough = seeThrough;
    }
    protected Shape(RenderingType type,BiConsumer<? super DefaultTransformer, Shape> transform,boolean seeThrough) {
        this(type,seeThrough);
        this.transformFunction = (transformer, shape) -> transform.accept(this.transformer,shape);
    }
    public DefaultTransformer getTransformer() {
        return transformer;
    }
    public void setId(ResourceLocation identifier){
        this.id = identifier;
    }
    public void draw(VertexBuilder builder, PoseStack matrixStack, float deltaTime) {
        matrixStack.pushPose();
        beforeDraw(matrixStack,deltaTime);
        builder.setPositionMatrix(matrixStack.last().pose());
        draw(builder);
        matrixStack.popPose();
    }
    public void discard(){
        ShapeManagers.removeShape(this.id);
    }
    public void draw(VertexBuilder builder) {
        if(this instanceof ExtractableShape) throw new UnsupportedOperationException("This shape cant be rendered directly, use addGroup to extract it.");
    }
    public void beforeDraw(PoseStack matrixStack,float deltaTime){
        transformer.updateTickDelta(deltaTime);
        transformFunction.accept(transformer,this);
        transformer.applyTransformations(matrixStack);
    }
    public void setMatrixScale(Vector3f scale, PoseStack matrixStack){
        matrixStack.scale(scale.x,scale.y,scale.z);
    }
    public void setMatrixRotation(Quaternionf rotation, PoseStack matrixStack){
        matrixStack.mulPose(rotation);
    }
    public void setMatrixCenterPos(Vec3 pos, PoseStack matrixStack){
        matrixStack.translate(pos.x, pos.y, pos.z);
    }
    public Vec3 calculateShapeCenterPos(){
        return this.centerPoint;
    }
    public Vec3 getShapeCenterPos(){
        return this.centerPoint;
    }
    public void setShapeCenterPos(Vec3 pos){
        this.centerPoint = pos;
    }
    public void syncLastToTarget(){
        this.transformer.syncLastToTarget();
    }
    public static class DefaultTransformer{
        public float delta = 0;
        public Shape managedShape;
        public Vec3dTransformer shapeCenterTransformer = new Vec3dTransformer();
        public Vec3dTransformer matrixCenterTransformer = new Vec3dTransformer();
        public QuaternionTransformer matrixRotationTransformer = new QuaternionTransformer();
        public Vec3dTransformer matrixScaleTransformer = new Vec3dTransformer(Vec3.ZERO.add(1));
        public DefaultTransformer(Shape managerShape){
            this.managedShape = managerShape;
        }
        public float getTickDelta(){
            return this.delta;
        }
        public void updateTickDelta(float delta){
            this.delta = delta;
        }
        public void applyTransformations(PoseStack matrixStack) {
            float deltaTime = getTickDelta();
            shapeCenterTransformer.updateVector(pos -> managedShape.setShapeCenterPos(pos), deltaTime);
            matrixCenterTransformer.updateVector(pos -> managedShape.setMatrixCenterPos(pos, matrixStack), deltaTime);

            Vec3 matrixCenter = matrixCenterTransformer.getCurrentVector().add(shapeCenterTransformer.getCurrentVector());

            matrixStack.translate(matrixCenter.x, matrixCenter.y, matrixCenter.z);
            matrixRotationTransformer.updateRotation(rot -> managedShape.setMatrixRotation(rot, matrixStack), deltaTime);
            matrixScaleTransformer.updateVector(scl -> managedShape.setMatrixScale(scl.toVector3f(), matrixStack), deltaTime);
            matrixStack.translate(-matrixCenter.x, -matrixCenter.y, -matrixCenter.z);
        }
        public void setShapeCenterPos(Vec3 pos){
            shapeCenterTransformer.setTargetVector(pos);
        }
        public void setMatrixCenterPos(Vec3 pos){
            matrixCenterTransformer.setTargetVector(pos);
        }
        public void setMatrixCenterPos(Vector3f pos){
            matrixCenterTransformer.setTargetVector(new Vec3(pos.x,pos.y,pos.z));
        }
        public void setMatrixRotation(Quaternionf rotation){
            matrixRotationTransformer.setTargetRotation(rotation);
        }
        public void setMatrixRotation(Vec3 rotation){
            matrixRotationTransformer.setTargetRotation(new Quaternionf().rotationXYZ(
                    (float)Math.toRadians(rotation.x),
                    (float)Math.toRadians(rotation.y),
                    (float)Math.toRadians(rotation.z)
            ));
        }
        public void setMatrixScale(Vec3 scale) {
            matrixScaleTransformer.setTargetVector(scale);
        }
        public void syncLastToTarget(){
            this.shapeCenterTransformer.syncLastToTarget();
            this.matrixCenterTransformer.syncLastToTarget();
            this.matrixRotationTransformer.syncLastToTarget();
            this.matrixScaleTransformer.syncLastToTarget();
        }
    }
}

