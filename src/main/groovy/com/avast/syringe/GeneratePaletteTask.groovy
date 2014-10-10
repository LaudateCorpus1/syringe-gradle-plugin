package com.avast.syringe

import com.avast.syringe.config.ConfigProperty
import com.avast.syringe.config.perspective.ModuleGenerator
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.AttributeInfo
import javassist.bytecode.ClassFile
import javassist.bytecode.FieldInfo
import javassist.bytecode.annotation.Annotation
import org.gradle.api.tasks.TaskAction
import org.reflections.ReflectionUtils
import org.reflections.Reflections
import org.reflections.scanners.AbstractScanner
import org.reflections.util.ConfigurationBuilder

class GeneratePaletteTask extends SyringeTask {

    GeneratePaletteTask() {
        super("Generates the palette source file.")
    }

    @TaskAction
    void start() {
        def palette = paletteFile()
        project.logger.info("Generating Syringe palette: {}", palette)

        checkPreconditions(palette)
        createDirectories(palette)
        def classesDirectory = project.sourceSets.main.output.classesDir
        def injectableClassNames = scanClassesForInjectable(classesDirectory)

        project.logger.debug("Found following injectable classes: {}", injectableClassNames)

        List<Class> injectableClasses = convertNamesToClasses(injectableClassNames, classesDirectory)

        generatePalette(injectableClasses, palette)

        project.logger.debug("Syringe palette generated: {}", palette)
    }

    private checkPreconditions(File palette) {
        if (palette.exists()) {
            taskFailed(new IllegalStateException("Syringe palette $palette already exists!"))
        }
    }

    private static void createDirectories(File palette) {
        palette.parentFile.mkdirs()
    }

    private List<String> scanClassesForInjectable(File classesDirectory) {
        def injectableClassNames = new ArrayList<String>()
        def reflections = createReflectionsScanner(injectableClassNames)
        reflections.scan(classesDirectory.toURI().toURL())
        injectableClassNames
    }

    private static List<Class> convertNamesToClasses(List<String> classNames, File classesDirectory) {
        def urls = (URL[]) [classesDirectory.toURI().toURL()].toArray()
        URLClassLoader classLoader = new URLClassLoader(urls, ConfigProperty.class.getClassLoader())
        ReflectionUtils.forNames(classNames, classLoader)
    }

    private void generatePalette(List<Class> injectableClasses, File palette) {
        project.logger.debug("Processing classes to generate Scala palette")

        FileWriter writer = new FileWriter(palette)
        try {
            for (Class injectableClass in injectableClasses) {
                project.logger.info("Generating builder for ${injectableClass.getName()} for ${paletteName}")
            }

            def moduleGenerator = ModuleGenerator.instance

            moduleGenerator.generate(
                    palettePackage, paletteDescription, paletteName,
                    injectableClasses, paletteTraits, builderTraits, writer
            )
        } finally {
            writer.close();
        }
    }

    private boolean isInjectable(ClassFile classFile) {
        project.logger.debug("Analyzing ${classFile.getName()}")

        try {
            AttributeInfo classAttribute = classFile.getAttribute(AnnotationsAttribute.visibleTag)
            if (classAttribute != null) {
                if (findAnnotation(classFile, classAttribute, "com.avast.syringe.config.ConfigBean")) {
                    return true
                }
            }

            List<FieldInfo> fields = classFile.getFields()
            for (FieldInfo field : fields) {
                AttributeInfo fieldAttribute = field.getAttribute(AnnotationsAttribute.visibleTag);
                if (fieldAttribute != null) {
                    if (findAnnotation(classFile, fieldAttribute, "com.avast.syringe.config.ConfigProperty")) {
                        return true
                    }
                }
            }

            project.logger.debug("${classFile.getName()} is not an injection target");
        } catch (Exception ex) {
            taskFailed(ex)
        }

        return false;
    }

    private boolean findAnnotation(ClassFile classFile, AttributeInfo classAttribute, String annotationName) {
        AnnotationsAttribute visible = new AnnotationsAttribute(
                classAttribute.getConstPool(),
                classAttribute.getName(),
                classAttribute.get()
        )

        for (Annotation ann : visible.getAnnotations()) {
            if (annotationName.equals(ann.getTypeName())) {
                project.logger.debug("${classFile.getName()} is an injection target")
                return true
            }
        }

        return false
    }

    private createReflectionsScanner(injectableClassNames) {
        new Reflections(
                new ConfigurationBuilder()
                        .setScanners(
                        new AbstractScanner() {
                            @Override
                            public void scan(Object scannedClass) {
                                if ((scannedClass instanceof ClassFile) && isInjectable((ClassFile) scannedClass)) {
                                    injectableClassNames.add(((ClassFile) scannedClass).getName())
                                }
                            }
                        }))
    }

}
