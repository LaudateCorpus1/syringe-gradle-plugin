package com.avast.syringe

import org.gradle.api.tasks.TaskAction


class DeletePaletteTask extends SyringeTask {

    DeletePaletteTask() {
        super("Deletes the generated palette source file.")
    }

    @TaskAction
    void start() {
        def palette = paletteFile()
        project.logger.info("Deleting Syringe palette: {}", palette)

        if (palette.exists()) {
            def deleted = palette.delete()
            if (deleted) {
                project.logger.debug("Syringe palette deleted: {}", palette)
            } else {
                taskFailed(new IllegalStateException("Syringe palette $palette could not be deleted!"))
            }
        } else {
            project.logger.warn("Syringe palette {} could not be deleted because it didn't exist!", palette)
        }
    }

}
