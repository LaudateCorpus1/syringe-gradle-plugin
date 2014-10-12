package com.avast.syringe

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskExecutionException
import org.junit.Test

import java.nio.file.Files
import java.nio.file.Paths

import static org.gradle.testfixtures.ProjectBuilder.builder
import static org.junit.Assert.*

class GradlePluginTest {

    //TODO: See remarks and add tests accordingly.
    //N.A remarks:
    //  Right now the tests do not take into consideration real life case where classes, the palette should be
    //  generated from, depend on 3rd party libraries.
    @Test
    void testPluginAddsTasksToProject() {
        def project = createTestProject()

        assertTrue(project.hasProperty("deletePalette"))
        assertTrue(project.hasProperty("generatePalette"))
    }

    @Test
    void testDeletePalette() {
        def project = createTestProject()
        project.allprojects {
            syringe {
                directory = "syringe"
                paletteName = "TestPalette"
                palettePackage = "com.avast.test"
            }
        }

        def testPalette = project.file("syringe/com/avast/test/TestPalette.scala")
        createPaletteFile(project, testPalette)
        executeTask(project, "deletePalette")
        assertFalse(testPalette.exists())
    }

    @Test(expected = TaskExecutionException.class)
    void testGeneratePaletteFailsToRunIfPaletteExists() {
        def project = createTestProject()

        def testPalette = project.file("src/main/syringe/com/avast/SyringePalette.scala")
        createPaletteFile(project, testPalette)

        executeTask(project, "generatePalette")
    }

    @Test
    void testGeneratePalette() {
        def project = createTestProject()
        def bTraits = new Properties()
        bTraits.setProperty(".*", "TestBuilderTrait")
        project.allprojects {
            syringe {
                directory = "syringe"
                paletteName = "TestPalette"
                palettePackage = "com.avast.test"
                paletteDescription = "Testing palette generation."
                paletteTraits = ["TestTrait"]
                builderTraits = bTraits
            }

            sourceSets.main {
                output.classesDir = project.file("build/src/test/resources")
            }
        }

        copyTestClass(project)

        def testPalette = project.file("syringe/com/avast/test/TestPalette.scala")

        executeTask(project, "generatePalette")
        assertTrue(testPalette.exists())
        def expectedPaletteSource = getClass().getResourceAsStream("/TestPalette.scala").text
        assertEquals(expectedPaletteSource, testPalette.text)
    }

    private static Project createTestProject() {
        Project project = builder().build()
        project.apply plugin: "com.avast.syringe.gradle"
        project
    }

    private static void executeTask(Project project, String name) {
        Task t = project.getTasksByName(name, false).asList().first()
        for (a in t.actions) {
            a.execute(t)
        }
    }

    private static void createPaletteFile(Project project, File palette) {
        assertTrue(palette.parentFile.mkdirs())
        assertTrue(palette.createNewFile())
    }

    private static void copyTestClass(Project project) {
        def target = Paths.get(project.sourceSets.main.output.classesDir.absolutePath, "TestClass.class")
        target.toFile().parentFile.mkdirs()
        Files.copy(new File(getClass().getResource("/TestClass.class").toURI()).toPath(), target)
    }

}
