package com.avast.syringe

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskExecutionException

import static java.io.File.separator

abstract class SyringeTask extends DefaultTask {

    SyringeTask(String description) {
        this.description = description
        group = 'Syringe'
    }

    @Input
    String directory() {
        getExtension().directory
    }

    @Input
    String paletteName() {
        getExtension().paletteName
    }

    @Input
    String palettePackage() {
        getExtension().palettePackage
    }

    @Input
    String paletteDescription() {
        getExtension().paletteDescription
    }

    @Input
    List<String> paletteTraits() {
        getExtension().paletteTraits
    }

    @Input
    Properties builderTraits() {
        getExtension().builderTraits
    }

    protected File paletteFile() {
        project.file(directory() +
                separator +
                palettePackage().replaceAll("\\.", separator) +
                separator +
                paletteName() +
                ".scala"
        )
    }

    protected getExtension() {
        project.extensions.findByName(GradlePlugin.EXTENSION_NAME)
    }

    protected taskFailed(Throwable cause) {
        throw new TaskExecutionException(this, cause)
    }

}
