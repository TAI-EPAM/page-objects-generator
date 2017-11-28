package com.epam.page.object.generator.integrationalTests;

import com.epam.page.object.generator.PageObjectsGenerator;
import com.epam.page.object.generator.adapter.JavaPoetAdapter;
import com.epam.page.object.generator.containers.SupportedTypesContainer;
import com.epam.page.object.generator.parser.JsonRuleMapper;
import com.epam.page.object.generator.utils.XpathToCssTransformation;
import com.epam.page.object.generator.validators.ValidatorsStarter;
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

import static org.junit.Assert.assertEquals;

public class Test1 {
    private String outputDir = "src/test/resources/";

    private String outputFile1 = "src/test/resources/test/page/DropdownMaterialize.java";

    private String outputFile2 = "src/test/resources/manual/page/DropdownMaterialize.java";

    private String packageName = "test";

    private String packageName1 = "manual";

    @After
    public void setUp() throws IOException {
        FileUtils.deleteQuietly(new File(outputDir + packageName + "/page/DropdownMaterialize" +
                ".class"));
        FileUtils.deleteQuietly(new File(outputDir + packageName1 + "/page/DropdownMaterialize.class"));
    }

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
                javaPoetAdapter, outputDir, urls, packageName);

        pog.setForceGenerateFile(forceGenerateFiles);

        return pog;
    }

    @Test
    public void pageObjectsGenerator_GenerateDropdownElementWithInnerElements() throws Exception {
       /* PageObjectsGenerator pog = initPog(
                "src/test/resources/dropdown-inner-root.json",
                "http://materializecss.com/dropdown.html",
                false,
                false);

        pog.generatePageObjects();*/

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int c1 = compiler.run(null, null, null, outputFile1);
        int c2 = compiler.run(null, null, null, outputFile2);
        assertEquals(0, c1);
        assertEquals(0, c2);

        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{
                new File("src/test/resources/").toURI().toURL(),
                new File("src/test/resources/").toURI().toURL()});

        Class<?> cls = Class.forName("test.page.DropdownMaterialize", true, classLoader);
        Class<?> cls1 = Class.forName("manual.page.DropdownMaterialize", true, classLoader);

        String[] className = cls.getName().split("\\.");
        String[] className1 = cls1.getName().split("\\.");
        assertEquals(className[className.length - 1], className1[className1.length - 1]);

        List<Annotation> classAnnotations = Arrays.asList(cls.getDeclaredAnnotations());
        List<Annotation> classAnnotations1 = Arrays.asList(cls1.getDeclaredAnnotations());
        assertEquals(classAnnotations, classAnnotations1);

        int mods = cls.getModifiers();
        int mods1 = cls1.getModifiers();
        assertEquals(mods, mods1);

        Class<?> superClass = cls.getSuperclass();
        Class<?> superClass1 = cls1.getSuperclass();
        assertEquals(superClass, superClass1);

        List<Field> fields = Arrays.asList(cls.getDeclaredFields());
        List<Field> fields1 = Arrays.asList(cls1.getDeclaredFields());

        Iterator<Field> it = fields.iterator();
        Iterator<Field> it1 = fields1.iterator();
        assertEquals(fields.size(), fields1.size());

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
