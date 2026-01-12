plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.13-SNAPSHOT" apply false
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
    swaps["mod_version"] = "\"" + property("mod.version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    constants["release"] = property("mod.id") != "template"
    dependencies["fapi"] = node.project.property("deps.fabric_api") as String
    replacements.string {
        direction = eval(current.version, "<=1.20.6")
        replace("MeshData.DrawState","BufferBuilder.DrawState")
    }
    replacements.string {
        direction = eval(current.version, "<=1.20.6")
        replace("MeshData.SortState","BufferBuilder.SortState")
    }
    replacements.string {
        direction = eval(current.version, "<=1.20.6")
        replace("MeshData.class","BufferBuilder.class")
    }
    replacements.string{
        direction = eval(current.version, "<=1.20.6")
        replace("ResourceLocation.fromNamespaceAndPath", "ResourceLocation.tryBuild")
    }
    replacements.string{
        direction = eval(current.version, ">=1.21.11")
        replace("ResourceLocation", "Identifier")
    }
    replacements.string{
        direction = eval(current.version, "<=1.18.2")
        replace("ResourceLocation.tryBuild(", "new ResourceLocation(")
    }
    replacements.string{
        direction = eval(current.version, ">=1.21.11")
        replace("import net.minecraft.client.renderer.RenderType;", "import net.minecraft.client.renderer.rendertype.RenderType;")
    }

}