package com.avast.syringe

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.scala.ScalaPlugin

class GradlePlugin implements Plugin<Project> {

    public static final String EXTENSION_NAME = "syringe"

    @Override
    void apply(Project project) {
        enableScalaPlugin(project)
        registerExtension(project)

        project.task("deletePalette", type: DeletePaletteTask)
        project.task("generatePalette", type: GeneratePaletteTask)
    }

    private static registerExtension(Project project) {
        project.extensions.create(EXTENSION_NAME, GradlePluginExtension)
    }

    private static enableScalaPlugin(Project project) {
        project.plugins.apply(ScalaPlugin)
    }

}
