package mypals.ml.shape;

import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shape.basics.tags.ExtractableShape;
import mypals.ml.shapeManagers.ShapeManagers;
import mypals.ml.transform.Vec3dTransformer;
import mypals.ml.transform.QuaternionTransformer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.BiConsumer;

public abstract class Shape {
    public enum RenderingType {
        IMMEDIATE,   // Immediately rendered
        BATCH,      // Stored in bufferBuilder, batch uploads every frame
        BUFFERED    // Stored in VBO
    }
    public Identifier id;
    public final RenderingType type;
    public BiConsumer<DefaultTransformer,Shape> transformFunction;
    public DefaultTransformer transformer;
    public boolean seeThrough = false;
    public Vec3d centerPoint = Vec3d.ZERO;

    protected Shape(RenderingType type,BiConsumer<? super DefaultTransformer, Shape> transform) {
        this(type);
        this.transformFunction = (transformer, shape) -> transform.accept(this.transformer,shape);
        setShapeCenterPos(calculateShapeCenterPos());
    }
    protected Shape(RenderingType type) {
        this.type = type;
        this.seeThrough = false;
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
    public void setId(Identifier identifier){
        this.id = identifier;
    }
    public void draw(ShapeBuilder builder, MatrixStack matrixStack,float deltaTime) {
        matrixStack.push();
        //Vec3d campos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        //matrixStack.translate();
        beforeDraw(matrixStack,deltaTime);
        builder.setPositionMatrix(matrixStack.peek().getPositionMatrix());
        draw(builder);
        matrixStack.pop();
    }
    public void discard(){
        ShapeManagers.removeShape(this.id);
    }
    public void draw(ShapeBuilder builder) {
        if(this instanceof ExtractableShape) throw new UnsupportedOperationException("This shape cant be rendered directly, use addGroup to extract it.");
    }
    public void beforeDraw(MatrixStack matrixStack,float deltaTime){
        transformer.updateTickDelta(deltaTime);
        transformFunction.accept(transformer,this);
        transformer.applyTransformations(matrixStack);
    }
    public void setMatrixScale(Vector3f scale, MatrixStack matrixStack){
        matrixStack.scale(scale.x,scale.y,scale.z);
    }
    public void setMatrixRotation(Quaternionf rotation, MatrixStack matrixStack){
        matrixStack.multiply(rotation);
    }
    public void setMatrixCenterPos(Vec3d pos, MatrixStack matrixStack){
        matrixStack.translate(pos.x, pos.y, pos.z);
    }
    public Vec3d calculateShapeCenterPos(){
        return Vec3d.ZERO;
    }
    public Vec3d getShapeCenterPos(){
        return this.centerPoint;
    }
    public void setShapeCenterPos(Vec3d pos){
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
        public Vec3dTransformer matrixScaleTransformer = new Vec3dTransformer(Vec3d.ZERO.add(1));
        public DefaultTransformer(Shape managerShape){
            this.managedShape = managerShape;
        }
        public float getTickDelta(){
            return this.delta;
        }
        public void updateTickDelta(float delta){
            this.delta = delta;
        }
        public void applyTransformations(MatrixStack matrixStack) {
            float deltaTime = getTickDelta();
            shapeCenterTransformer.updateVector(pos -> managedShape.setShapeCenterPos(pos), deltaTime);
            matrixCenterTransformer.updateVector(pos -> managedShape.setMatrixCenterPos(pos, matrixStack), deltaTime);

            Vec3d matrixCenter = matrixCenterTransformer.getCurrentVector().add(shapeCenterTransformer.getCurrentVector());

            matrixStack.translate(matrixCenter.x, matrixCenter.y, matrixCenter.z);
            matrixRotationTransformer.updateRotation(rot -> managedShape.setMatrixRotation(rot, matrixStack), deltaTime);
            matrixScaleTransformer.updateVector(scl -> managedShape.setMatrixScale(scl.toVector3f(), matrixStack), deltaTime);
            matrixStack.translate(-matrixCenter.x, -matrixCenter.y, -matrixCenter.z);
        }
        public void setShapeCenterPos(Vec3d pos){
            shapeCenterTransformer.setTargetVector(pos);
        }
        public void setMatrixCenterPos(Vec3d pos){
            matrixCenterTransformer.setTargetVector(pos);
        }
        public void setMatrixCenterPos(Vector3f pos){
            matrixCenterTransformer.setTargetVector(new Vec3d(pos.x,pos.y,pos.z));
        }
        public void setMatrixRotation(Quaternionf rotation){
            matrixRotationTransformer.setTargetRotation(rotation);
        }
        public void setMatrixRotation(Vec3d rotation){
            matrixRotationTransformer.setTargetRotation(new Quaternionf().rotationXYZ(
                    (float)Math.toRadians(rotation.x),
                    (float)Math.toRadians(rotation.y),
                    (float)Math.toRadians(rotation.z)
            ));
        }
        public void setMatrixScale(Vec3d scale) {
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

