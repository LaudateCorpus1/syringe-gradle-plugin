package com.avast.syringe

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskExecutionException

import java.util.regex.Matcher

import static java.io.File.separator

abstract class SyringeTask extends DefaultTask {

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

    protected File paletteFile() {
        def extension = (GradlePluginExtension) project.extensions.findByName(GradlePlugin.EXTENSION_NAME);
        directory = extension.directory
        palettePackage = extension.palettePackage
        paletteName = extension.paletteName
        project.file(directory +
                separator +
                palettePackage.replaceAll("\\.", Matcher.quoteReplacement(separator)) +
                separator +
                paletteName +
                ".scala"
        )
    }

    protected taskFailed(Throwable cause) {
        throw new TaskExecutionException(this, cause)
    }

}
