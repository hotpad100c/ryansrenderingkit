package mypals.ml.builders.shapeBuilders;

public final class ShapeGenerator {

    private ShapeGenerator() {}

    public static FaceCircleBuilder generateFaceCircle() {
        return new FaceCircleBuilder();
    }

    public static LineCircleBuilder generateLineCircle() {
        return new LineCircleBuilder();
    }

    public static SphereBuilder generateSphere() {
        return new SphereBuilder();
    }

    public static ObjModelBuilder generateObjModel() {
        return new ObjModelBuilder();
    }

    public static ObjModelOutlineBuilder generateObjModelOutline() {
        return new ObjModelOutlineBuilder();
    }

    public static ConeBuilder generateCone() {
        return new ConeBuilder();
    }

    public static CylinderBuilder generateCylinder() {
        return new CylinderBuilder();
    }
    public static ConeWireframeBuilder generateConeWireframe() {
        return new ConeWireframeBuilder();
    }

    public static CylinderWireframeBuilder generateCylinderWireframe() {
        return new CylinderWireframeBuilder();
    }
    public static LineBuilder generateLine() {
        return new LineBuilder();
    }

    public static StripLineBuilder generateStripLine() {
        return new StripLineBuilder();
    }

    public static BoxFaceBuilder generateBoxFace() {
        return new BoxFaceBuilder();
    }

    public static BoxWireframeBuilder generateBoxWireframe() {
        return new BoxWireframeBuilder();
    }

    public static WireframedBoxBuilder generateWireframedBox() {
        return new WireframedBoxBuilder();
    }
}