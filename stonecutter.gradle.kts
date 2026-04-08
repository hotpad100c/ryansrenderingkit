plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom") version "1.15.3" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.15.3" apply false
    // id("me.modmuss50.mod-publish-plugin") version "1.0.+" apply false
}

stonecutter active "26.2"

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