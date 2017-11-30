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
import org.junit.BeforeClass;
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

    private static final String resourceDir = "src/test/resources/";

    private String inputJavaFileTest = "src/test/resources/test/page/DropdownMaterialize.java";
    private String inputJavaFileManual = "src/test/resources/manual/page/DropdownMaterialize.java";

    private String packageTestName = "test";

    private String packageManualName = "manual";

    private static String classPackageNameTest = "test.page";
    private static String classPackageNameManual = "manual.page";

    private String generatedClassFullName;
    private String manualClassFullName;
    private String jsonSourcePath;
    private String url;
    private boolean checkLocatorUniqueness;
    private boolean forceGenerateFiles;

    private Class actualClass;
    private Class expectedClass;

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
                javaPoetAdapter, resourceDir, urls, packageTestName);
        pog.setForceGenerateFile(forceGenerateFiles);

        return pog;
    }

    public MainIntegrationTest(String inputJavaFileTest,
                               String inputJavaFileManual,
                               String generatedClassFullName,
                               String manualClassFullName,
                               String jsonSourcePath,
                               String url,
                               boolean checkLocatorUniqueness,
                               boolean forceGenerateFiles) throws Exception {
        this.inputJavaFileTest = inputJavaFileTest;
        this.inputJavaFileManual = inputJavaFileManual;
        this.generatedClassFullName = generatedClassFullName;
        this.manualClassFullName = manualClassFullName;
        this.jsonSourcePath = jsonSourcePath;
        this.url = url;
        this.checkLocatorUniqueness = checkLocatorUniqueness;
        this.forceGenerateFiles = forceGenerateFiles;
        method();
    }

    @Parameters
    public static Iterable<Object[]> data() throws MalformedURLException, ClassNotFoundException {
        return Arrays.asList(new Object[][]{
                {
                        "src/test/resources/test/page/DropdownMaterialize.java",
                        "src/test/resources/manual/page/DropdownMaterialize.java",
                        classPackageNameTest + ".DropdownMaterialize",
                        classPackageNameManual + ".DropdownMaterialize",
                        "src/test/resources/dropdown-inner-root.json",
                        "http://materializecss.com/dropdown.html",
                        false,
                        false
                },

                {


                }
        });

    }

    public void method() throws Exception {
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
                new File(resourceDir).toURI().toURL()});

        actualClass = Class.forName(this.generatedClassFullName, true, classLoader);
        expectedClass = Class.forName(this.manualClassFullName, true, classLoader);

    }

    @After
    public void clearUp() throws IOException {
        FileUtils.deleteQuietly(new File("src/test/resources/test"));
        FileUtils.deleteQuietly(new File("src/test/resources/manual/page/DropdownMaterialize.class"));
        FileUtils.deleteDirectory(new File(resourceDir + packageTestName));
    }

    @Test
    public void testNameOfClass() throws Exception {
        String[] className = actualClass.getName().split("\\.");
        String[] className1 = expectedClass.getName().split("\\.");
        assertEquals(className[className.length - 1], className1[className1.length - 1]);
    }

    @Test
    public void testAnnotationOfClass() {
        List<Annotation> classAnnotations = Arrays.asList(actualClass.getDeclaredAnnotations());
        List<Annotation> classAnnotations1 = Arrays.asList(expectedClass.getDeclaredAnnotations());
        assertEquals(classAnnotations, classAnnotations1);
    }

    @Test
    public void testModifiersOfClass() {
        int mods = actualClass.getModifiers();
        int mods1 = expectedClass.getModifiers();
        assertEquals(mods, mods1);
    }

    @Test
    public void testSuperClassOfClass() {
        Class<?> actualSuperClass = actualClass.getSuperclass();
        Class<?> expectedSuperClass1 = expectedClass.getSuperclass();
        assertEquals(actualSuperClass, expectedSuperClass1);
    }

    @Test
    public void testFields() {
        List<Field> fields = Arrays.asList(actualClass.getDeclaredFields());
        List<Field> fields1 = Arrays.asList(expectedClass.getDeclaredFields());
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
