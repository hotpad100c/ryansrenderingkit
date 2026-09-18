plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom") version "1.15.3" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.15.3" apply false
    // id("me.modmuss50.mod-publish-plugin") version "1.0.+" apply false
}

stonecutter active "26.3"

stonecutter tasks {
    order("publishToMavenCentral")
}

tasks.register("mavenUpload") {
    group = "publishing"
    description = "Uploads every supported Minecraft version to Maven Central for validation."
    dependsOn(stonecutter.tasks.named("publishToMavenCentral"))
}

/*
// Make newer versions be published last
stonecutter tasks {
    order("publishModrinth")
    order("publishCurseforge")
}
 */

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    swaps["mod_version"] = "\"${property("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    constants["release"] = property("mod.id") != "template"
    dependencies["fapi"] = node.project.property("deps.fabric_api") as String
    replacements {

        string(eval(current.version, "<1.21.11")) {
            replace("Identifier.fromNamespaceAndPath", "ResourceLocation.fromNamespaceAndPath")
        }

        string(eval(current.version, "<1.21.1")) {
            replace("ResourceLocation.fromNamespaceAndPath", "ResourceLocation.tryBuild")
        }
        string(eval(current.version, "<1.19.4")) {
            replace("ResourceLocation.tryBuild", "new ResourceLocation")
        }

        string(eval(current.version, "<1.21.11")) {
            replace("Identifier", "ResourceLocation")
        }

        string(eval(current.version, "<1.21.11")) {
            replace(
                "import net.minecraft.client.renderer.rendertype.RenderType;",
                "import net.minecraft.client.renderer.RenderType;"
            )
        }

        string(eval(current.version, "<=1.20.6")) {
            replace("MeshData.DrawState", "BufferBuilder.DrawState")
        }
        string(eval(current.version, "<=1.20.6")) {
            replace("MeshData.SortState", "BufferBuilder.SortState")
        }
        string(eval(current.version, "<=1.20.6")) {
            replace("MeshData.class", "BufferBuilder.class")
        }

        // 26.3 -> 26.2 RenderPearl API 降级为 Blaze3D
        string(eval(current.version, "<26.3")) {
            // Pipeline & State
            replace("com.mojang.renderpearl.api.pipeline.PrimitiveTopology", "com.mojang.blaze3d.PrimitiveTopology")
            replace("com.mojang.renderpearl.api.pipeline.IndexType", "com.mojang.blaze3d.IndexType")
            replace("com.mojang.renderpearl.api.pipeline.RenderPipeline", "com.mojang.blaze3d.pipeline.RenderPipeline")
            replace("com.mojang.renderpearl.api.pipeline.DepthStencilState", "com.mojang.blaze3d.pipeline.DepthStencilState")
            replace("com.mojang.renderpearl.api.pipeline.CompareOp", "com.mojang.blaze3d.platform.CompareOp")

            // Vertex Formats
            replace("com.mojang.renderpearl.api.vertex.VertexFormatElement", "com.mojang.blaze3d.vertex.VertexFormatElement")
            replace("com.mojang.renderpearl.api.vertex.VertexFormat", "com.mojang.blaze3d.vertex.VertexFormat")

            // Buffers & Textures
            replace("com.mojang.renderpearl.api.buffers.GpuBufferSlice", "com.mojang.blaze3d.buffers.GpuBufferSlice")
            replace("com.mojang.renderpearl.api.buffers.GpuBuffer", "com.mojang.blaze3d.buffers.GpuBuffer")
            replace("com.mojang.renderpearl.api.textures.GpuTextureView", "com.mojang.blaze3d.textures.GpuTextureView")

            // Device & Commands & Backend
            replace("com.mojang.renderpearl.api.device.GpuDevice", "com.mojang.blaze3d.systems.GpuDevice")
            replace("com.mojang.renderpearl.api.commands.RenderPass", "com.mojang.blaze3d.systems.RenderPass")
            replace("com.mojang.renderpearl.backend.opengl.GlStateManager", "com.mojang.blaze3d.opengl.GlStateManager")

            // PoseStack rotation
            replace("poseStack.rotate(", "poseStack.mulPose(")
            replace("stack.rotate(", "stack.mulPose(")
        }

        string(eval(current.version, "<26.2")) {
            replace(".mainCamera()", ".getMainCamera()")
        }

// 5. 26.1 以下的降级，互相独立
        string(eval(current.version, "<26.1")) {
            replace("import net.minecraft.util.LightCoordsUtil;", "import net.minecraft.client.renderer.LightTexture;")
        }
        string(eval(current.version, "<26.1")) {
            replace("LightCoordsUtil", "LightTexture")
        }
        string(eval(current.version, "<26.1")) {
            replace("240", "LightTexture.FULL_BLOCK")
        }
        string(eval(current.version, "<26.1")) {
            replace("ClientCommands", "ClientCommandManager")
        }
    }
}
