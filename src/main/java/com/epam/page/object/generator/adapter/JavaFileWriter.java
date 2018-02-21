package com.epam.page.object.generator.adapter;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class which generates .java source file for each {@link JavaClass} from input list.
 */
public class JavaFileWriter {

    private List<JavaFile> javaFiles = new ArrayList<>();

    private final static Logger logger = LoggerFactory.getLogger(JavaFileWriter.class);

    /**
     * Calls a method to write each {@link JavaClass} in outputDir folder.
     *
     * @param outputDir   path to the folder where need to generate .java source files.
     * @param javaClasses list of {@link JavaClass}.
     * @throws IOException can be throwing if outputDir path doesn't correct.
     */
    public void writeFiles(String outputDir, List<JavaClass> javaClasses) throws IOException {
        for (JavaClass javaClass : javaClasses) {
            logger.debug("Start generating " + javaClass + "\n");
            writeClass(outputDir, javaClass);
            logger.debug("Generate " + javaClass + "\n");
        }
    }

    /**
     * Write .java source file in outputDir folder.
     *
     * @param outputDir path to the folder where need to generate .java source files.
     * @param javaClass {@link JavaClass} which we will be writing.
     * @throws IOException can be throwing if outputDir path doesn't correct.
     */
    private void writeClass(String outputDir, JavaClass javaClass) throws IOException {
        JavaFile javaFile = getJavaFileFromClass(javaClass);
        try {
            String formattedFile = new Formatter().formatSource(javaFile.toString());
            Path path = this.getFullPath(Paths.get(outputDir), javaFile);
            Files.write(path, formattedFile.getBytes());
        } catch (FormatterException e) {
            logger.warn("There was an error during format a source: " + e.getMessage());
        }
    }

    /**
     * Returns .java file build from an appropriate class
     * @param javaClass {@link JavaClass} created from predefined data
     * @return {@Link JavaFile} file generated from {@Link JavaClass}
     */
    private JavaFile getJavaFileFromClass(JavaClass javaClass) {
        JavaFile javaFile = JavaFile.builder(
                javaClass.getPackageName(),
                buildTypeSpec(javaClass))
                .build();
        javaFiles.add(javaFile);
        return javaFile;
    }

    /**
     * Returns a list of java files generated during POG process
     * @return list of generated {@Link JavaFile}
     */
    public List<JavaFile> getJavaFiles() {
        return this.javaFiles;
    }

    /**
     * Returns a full path for a {@Link JavaFile} in the appropriate output directory
     * @param outputDirectory path to the folder where .java source file will be saved
     * @param javaFile {@Link JavaFile} file which path we want to get
     * @return {@Link Path} full path to the .java file
     * @throws IOException can be thrown if outputDir path is not correct.
     */
    public Path getFullPath(Path outputDirectory, JavaFile javaFile) throws IOException {
        String packageName = javaFile.packageName;
        TypeSpec typeSpec = javaFile.typeSpec;

        if(!packageName.isEmpty()) {
            for (String packageComponent : packageName.split("\\.")) {
                outputDirectory = outputDirectory.resolve(packageComponent);
            }
            Files.createDirectories(outputDirectory);
        }
        return outputDirectory.resolve(typeSpec.name + ".java");
    }

    /**
     * Build java class with all fields contains in it.
     *
     * @param javaClass {@link JavaClass} which contains all information about future .java source
     *                  files.
     * @return {@link TypeSpec} used by JavaPoet for generation .java source files.
     */
    private TypeSpec buildTypeSpec(JavaClass javaClass) {
        List<FieldSpec> fieldSpecList = new ArrayList<>();

        for (JavaField field : javaClass.getFieldsList()) {
            logger.debug("Start building " + field);
            fieldSpecList.add(buildFieldSpec(field));
            logger.debug("Finish building field\n");
        }

        TypeSpec.Builder builder = TypeSpec.classBuilder(javaClass.getClassName())
                .addModifiers(javaClass.getModifier())
                .superclass(javaClass.getSuperClass())
                .addFields(fieldSpecList);

        if (javaClass.getAnnotation() != null) {
            builder.addAnnotation(buildAnnotationSpec(javaClass.getAnnotation()));
        }

        return builder.build();
    }

    /**
     * Build field for .java source file.
     *
     * @param field {@link JavaField} which is used for generation field.
     * @return {@link FieldSpec} used by JavaPoet for generation {@link TypeSpec}.
     */
    private FieldSpec buildFieldSpec(JavaField field) {
        return field.isSelenideTypeField()
                ? buildSelenideFieldSpec(field)
                : buildRegularFieldSpec(field);
    }

    private FieldSpec buildRegularFieldSpec(JavaField field) {
        return FieldSpec
                .builder(ClassName.bestGuess(field.getFullFieldClass()), field.getFieldName())
                .addModifiers(field.getModifiers())
                .addAnnotation(buildAnnotationSpec(field.getAnnotation()))
                .build();
    }

    private FieldSpec buildSelenideFieldSpec(JavaField field) {
        return FieldSpec
                .builder(ClassName.bestGuess(field.getFullFieldClass()), field.getFieldName())
                .addModifiers(field.getModifiers())
                .initializer("$L($S)", field.getInitializer().get("$L($S)"))
                .build();
    }

    /**
     * Build annotation for .java source file.
     *
     * @param annotation {@link JavaAnnotation} which is used for generation annotation.
     * @return {@link AnnotationSpec} used by JavaPoet for generation {@link FieldSpec}.
     */
    private AnnotationSpec buildAnnotationSpec(JavaAnnotation annotation) {
        logger.debug("Start building " + annotation);
        AnnotationSpec annotationSpec =
                AnnotationSpec
                        .builder(annotation.getAnnotationClass())
                        .build();

        for (AnnotationMember annotationMember : annotation.getAnnotationMembers()) {
            if (annotationMember.getFormat().equals("$S")) {
                annotationSpec = annotationSpec.toBuilder()
                        .addMember(annotationMember.getName(), annotationMember.getFormat(),
                                annotationMember.getArg())
                        .build();
            } else if (annotationMember.getFormat().equals("$L")) {
                annotationSpec = annotationSpec.toBuilder()
                        .addMember(annotationMember.getName(), annotationMember.getFormat(),
                                buildAnnotationSpec(annotationMember.getAnnotation()))
                        .build();
            }
            logger.debug("Build " + annotationMember);
        }
        logger.debug("Finish building annotation");

        return annotationSpec;
    }
}
