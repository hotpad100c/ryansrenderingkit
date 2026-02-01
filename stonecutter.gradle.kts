plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom") version "1.15.3" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.15.3" apply false
    // id("me.modmuss50.mod-publish-plugin") version "1.0.+" apply false
}

stonecutter active "1.21.5"

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
        string(eval(current.version, "<=1.20.6")) {
            replace("MeshData.DrawState", "BufferBuilder.DrawState")
        }
        string(eval(current.version, "<=1.20.6")) {
            replace("MeshData.SortState", "BufferBuilder.SortState")
        }
        string(eval(current.version, "<=1.20.6")) {
            replace("MeshData.class", "BufferBuilder.class")
        }
        string(eval(current.version, "<=1.20.6")) {
            replace("ResourceLocation.fromNamespaceAndPath", "ResourceLocation.tryBuild")
        }
        string(eval(current.version, ">=1.21.11")) {
            replace("ResourceLocation.fromNamespaceAndPath", "Identifier.fromNamespaceAndPath")
        }
        string(eval(current.version, ">=1.21.11")) {
            replace("ResourceLocation", "Identifier")
        }
        string(eval(current.version, "<=1.18.2")) {
            replace("ResourceLocation.tryBuild(", "new ResourceLocation(")
        }
        string(eval(current.version, ">=1.21.11")) {
            replace(
                "import net.minecraft.client.renderer.RenderType;",
                "import net.minecraft.client.renderer.rendertype.RenderType;"
            )
        }
        string(eval(current.version, ">=26.1")) {
            replace("LightTexture.FULL_BLOCK", "240")
        }
        string(eval(current.version, ">=26.1")) {
            replace("import net.minecraft.client.renderer.LightTexture;", "import net.minecraft.util.LightCoordsUtil;")
        }
        string(eval(current.version, ">=26.1")) {
            replace("LightTexture", "LightCoordsUtil")
        }
        string(eval(current.version, ">=26.1")) {
            replace("ClientCommandManager", "ClientCommands")
        }
    }
}