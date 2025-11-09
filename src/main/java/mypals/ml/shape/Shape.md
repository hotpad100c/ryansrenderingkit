### Shape Rendering System

#### How It Works
Think of your shape in **3 layers**:

| Layer | What It Is | When It Updates | Magic Applied |
|-------|------------|-----------------|---------------|
| **Raw** | Your "blueprint" (e.g., 8 corners of a cube) | Only when you change the design | Nothing yet! |
| **Shape** | "Baked" version in game space | When position/scale changes | **Local + World** transforms (locked in forever) |
| **Rendering** | Final "draw me!" list (e.g., 36 triangles or wireframe lines) | Every frame (or when style changes) | **Pivot + Spin + Zoom** (animated live!) |

#### Superpowers
- **Animate anything**: Spin a cube? `transformer.setRotation(...)`
- **Switch styles**: Solid → Wireframe? Just flip a flag!
- **No stutter**: Only rebuild what's dirty.
- **Type-safe**: Code knows *exactly* what shape it is (no casting hacks).
- **Minecraft-ready**: Works with `PoseStack`, colors, transparency, visibility.

#### Example: Spinning Red Cube
```java
class RedCube extends Shape {
    RedCube() {
        super(BATCH);  // Fast batch rendering
        setBaseColor(Color.RED);
        setLocalScale(new Vec3(2,2,2));  // Big!
        markGeometryDirty();  // Bake it!
    }
    
    // 8 raw corners
    void generateRawGeometry() { /* add 8 Vec3 points */ }
    
    // 36 triangles for solid look
    void generateRenderingTopology() { /* unfold faces */ }
}

// Use it:
RedCube cube = new RedCube();
cube.transformer().setPivot(new Vec3(0,1,0));  // Spin around top
cube.transformer().setRotation(new Quaternionf().rotateY(1));  // Spin!
```

**Result**: A red cube spins smoothly in-game. Add to your mod → instant 3D UI, tools, or effects!

**Pro Tip**: Copy the **prompt table** (from before) into AI chats for instant code gen.  
**Ready to build?** Load OBJ models next? 🚀 Let me know!