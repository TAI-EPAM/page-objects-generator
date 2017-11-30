package com.epam.page.object.generator.integrationalTests;

import com.epam.page.object.generator.PageObjectsGenerator;
import com.epam.page.object.generator.adapter.JavaPoetAdapter;
import com.epam.page.object.generator.containers.SupportedTypesContainer;
import com.epam.page.object.generator.parser.JsonRuleMapper;
import com.epam.page.object.generator.utils.XpathToCssTransformation;
import com.epam.page.object.generator.validators.ValidatorsStarter;

import java.net.MalformedURLException;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MainIntegrationTest {

    private static final String RESOURCE_DIR = "src/test/resources/";
    private static final String TEST_DIR = "test/page/";
    private static final String MANUAL_DIR = "manual/page/";
    private static final String TEST_CLASS_PACKAGE_NAME = "test.page";
    private static final String MANUAL_CLASS_PACKAGE_NAME = "manual.page";

    private static final String PACKAGE_TEST_NAME = "test";
    private static final String PACKAGE_MANUAL_NAME = "manual";

    private String inputJavaFileTest;
    private String inputJavaFileManual;
    private String generatedClassFullName;
    private String manualClassFullName;
    private String jsonSourcePath;
    private String url;
    private boolean checkLocatorUniqueness;
    private boolean forceGenerateFiles;
    private String smallClassName;

    private Class testingClass;
    private Class manualClass;

    private PageObjectsGenerator initPog(String jsonPath, String url,
                                         boolean checkLocatorUniqueness,
                                         boolean forceGenerateFiles) throws IOException {
        List<String> urls = new ArrayList<>();
        urls.add(url);
        SupportedTypesContainer bc = new SupportedTypesContainer();
        JsonRuleMapper parser = new JsonRuleMapper(new File(jsonPath), new ObjectMapper());
        XpathToCssTransformation xpathToCssTransformation = new XpathToCssTransformation();
        JavaPoetAdapter javaPoetAdapter = new JavaPoetAdapter(bc, xpathToCssTransformation);

        ValidatorsStarter validatorsStarter = new ValidatorsStarter(bc);
        validatorsStarter.setCheckLocatorsUniqueness(checkLocatorUniqueness);

        PageObjectsGenerator pog = new PageObjectsGenerator(parser, validatorsStarter,
                javaPoetAdapter, RESOURCE_DIR, urls, PACKAGE_TEST_NAME);
        pog.setForceGenerateFile(forceGenerateFiles);

        return pog;
    }

    //TODO: make using @Parameter
    public MainIntegrationTest(String smallClassName,
                               String jsonSourcePath,
                               String url,
                               boolean checkLocatorUniqueness,
                               boolean forceGenerateFiles) throws Exception {
        this.smallClassName = smallClassName;
        this.inputJavaFileTest = RESOURCE_DIR + TEST_DIR + smallClassName + ".java";
        this.inputJavaFileManual = RESOURCE_DIR + MANUAL_DIR + smallClassName + ".java";
        this.generatedClassFullName = TEST_CLASS_PACKAGE_NAME + "." + smallClassName;
        this.manualClassFullName = MANUAL_CLASS_PACKAGE_NAME + "." + smallClassName;
        this.jsonSourcePath = jsonSourcePath;
        this.url = url;
        this.checkLocatorUniqueness = checkLocatorUniqueness;
        this.forceGenerateFiles = forceGenerateFiles;
    }

    @Parameters
    public static Iterable<Object[]> data() throws MalformedURLException, ClassNotFoundException {
        return Arrays.asList(new Object[][]{
                {
                        "DropdownMaterialize",
                        "src/test/resources/dropdown-inner-root.json",
                        "http://materializecss.com/dropdown.html",
                        false,
                        false
                },

                {
                        "HowToCreate",
                        "src/test/resources/dropdown.json",
                        "https://www.w3schools.com/howto/howto_js_dropdown.asp",
                        true,
                        false
                }
        });

    }


    public void compileTestClasses() throws Exception {

        PageObjectsGenerator pog = initPog(
                jsonSourcePath,
                url,
                checkLocatorUniqueness,
                forceGenerateFiles);

        pog.generatePageObjects();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int c1 = compiler.run(null, null, null, inputJavaFileTest);
        int c2 = compiler.run(null, null, null, inputJavaFileManual);
        assertEquals(0, c1);
        assertEquals(0, c2);

        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{
                new File(RESOURCE_DIR).toURI().toURL()});

        testingClass = Class.forName(this.generatedClassFullName, true, classLoader);
        manualClass = Class.forName(this.manualClassFullName, true, classLoader);

    }

    @Test
    public void runTestCase() throws Exception {
        compileTestClasses();
        testNameOfClass();
        testAnnotationOfClass();
        testModifiersOfClass();
        testSuperClassOfClass();
        testFields();
    }

    @After
    public void cleanUp() throws IOException {
        FileUtils.deleteQuietly(new File(RESOURCE_DIR + PACKAGE_TEST_NAME));
        FileUtils.deleteQuietly(new File(RESOURCE_DIR + MANUAL_DIR + smallClassName + ".class"));
    }

    public void testNameOfClass() throws Exception {
        String[] className = testingClass.getName().split("\\.");
        String[] className1 = manualClass.getName().split("\\.");
        assertEquals(className[className.length - 1], className1[className1.length - 1]);
    }

    public void testAnnotationOfClass() {
        List<Annotation> classAnnotations = Arrays.asList(testingClass.getDeclaredAnnotations());
        List<Annotation> classAnnotations1 = Arrays.asList(manualClass.getDeclaredAnnotations());
        assertEquals(classAnnotations, classAnnotations1);
    }

    public void testModifiersOfClass() {
        int mods = testingClass.getModifiers();
        int mods1 = manualClass.getModifiers();
        assertEquals(mods, mods1);
    }

    public void testSuperClassOfClass() {
        Class<?> actualSuperClass = testingClass.getSuperclass();
        Class<?> expectedSuperClass1 = manualClass.getSuperclass();
        assertEquals(actualSuperClass, expectedSuperClass1);
    }

    public void testFields() {
        List<Field> fields = Arrays.asList(testingClass.getDeclaredFields());
        List<Field> fields1 = Arrays.asList(manualClass.getDeclaredFields());
        assertEquals(fields.size(), fields1.size());

        Iterator<Field> it = fields.iterator();
        Iterator<Field> it1 = fields1.iterator();

        while (it.hasNext()) {
            Field currentField = it.next();
            Field currentField1 = it1.next();

            List<Annotation> fieldAnnotations = Arrays
                    .asList(currentField.getDeclaredAnnotations());
            List<Annotation> fieldAnnotations1 = Arrays
                    .asList(currentField1.getDeclaredAnnotations());
            assertEquals(fieldAnnotations, fieldAnnotations1);

            int fieldModifiers = currentField.getModifiers();
            int fieldModifiers1 = currentField1.getModifiers();
            assertEquals(fieldModifiers, fieldModifiers1);

            String[] newName = currentField.toString().split(" ");
            String[] newName1 = currentField1.toString().split(" ");
            String importForField = newName[1];
            String importForField1 = newName1[1];
            assertEquals(importForField, importForField1);
            assertEquals(currentField.getName(), currentField1.getName());

            String type = currentField.getType().getName();
            String type1 = currentField1.getType().getName();
            assertEquals(type, type1);
        }
    }
}
