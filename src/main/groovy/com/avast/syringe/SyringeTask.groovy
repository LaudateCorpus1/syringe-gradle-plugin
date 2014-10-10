package com.avast.syringe

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskExecutionException

import static java.io.File.separator

abstract class SyringeTask extends SourceTask {

    SyringeTask(String description) {
        this.description = description
        group = 'Syringe'
    }

    @Input
    String directory

    @Input
    String paletteName

    @Input
    String palettePackage

    @Input
    String paletteDescription

    @Input
    List<String> paletteTraits

    @Input
    Properties builderTraits

    @OutputFile
    File paletteFile

    protected File paletteFile() {
        def extension = (GradlePluginExtension) project.extensions.findByName(GradlePlugin.EXTENSION_NAME);
        directory = extension.directory
        palettePackage = extension.palettePackage
        paletteName = extension.paletteName
        paletteFile = project.file(directory +
                separator +
                palettePackage.replaceAll("\\.", separator) +
                separator +
                paletteName +
                ".scala"
        )
    }

    protected taskFailed(Throwable cause) {
        throw new TaskExecutionException(this, cause)
    }

}
