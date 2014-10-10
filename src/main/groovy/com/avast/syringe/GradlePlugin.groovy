package com.avast.syringe

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.scala.ScalaPlugin

class GradlePlugin implements Plugin<Project> {

    public static final String EXTENSION_NAME = "syringe"

    private Project project
    private GradlePluginExtension extension

    @Override
    void apply(Project project) {
        this.project = project

        enableScalaPlugin(project)
        registerExtension()

        project.task("deletePalette", type: DeletePaletteTask)
        project.task("generatePalette", type: GeneratePaletteTask)

        project.tasks.withType(SyringeTask.class) { SyringeTask task ->
            task.conventionMapping.map('directory', { extension.directory })
            task.conventionMapping.map('paletteName', { extension.paletteName })
            task.conventionMapping.map('palettePackage', { extension.palettePackage })
            task.conventionMapping.map('paletteDescription', { extension.paletteDescription })
            task.conventionMapping.map('paletteTraits', { extension.paletteTraits })
            task.conventionMapping.map('builderTraits', { extension.builderTraits })
        }
    }

    private registerExtension() {
        extension = project.extensions.create(EXTENSION_NAME, GradlePluginExtension)
    }

    private static enableScalaPlugin(Project project) {
        project.plugins.apply(ScalaPlugin)
    }

}
