package com.epam.page.object.generator.integrationalTests;

import com.epam.page.object.generator.PageObjectsGenerator;
import com.epam.page.object.generator.adapter.JavaPoetAdapter;
import com.epam.page.object.generator.containers.SupportedTypesContainer;
import com.epam.page.object.generator.parser.JsonRuleMapper;
import com.epam.page.object.generator.utils.XpathToCssTransformation;
import com.epam.page.object.generator.validators.ValidatorsStarter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("Duplicates")
@RunWith(Parameterized.class)
public class MainIntegrationTest {
    private static final String RESOURCE_DIR = "src/test/resources/";
    private static final String TEST_DIR = "test/";
    private static final String MANUAL_DIR = "manual/";
    private static final String TEST_CLASS_PACKAGE_NAME = "test.";
    private static final String MANUAL_CLASS_PACKAGE_NAME = "manual.";
    private static final String PACKAGE_TEST_NAME = "test";

    private boolean checkLocatorUniqueness;
    private boolean forceGenerateFiles;
    private String inputJavaFileTest;
    private String inputJavaFileManual;
    private String generatedClassFullName;
    private String manualClassFullName;
    private String jsonSourcePath;
    private String url;
    private Class testingClass;
    private Class manualClass;

    private static List<File> caseDirs;
    private static List<File> insideDirs;
    private static List<File> caseProps;
    private static List<File> filesList = new ArrayList<>();

    private List<TestClassDTO> classesList;

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

    public MainIntegrationTest(List<TestClassDTO> list) throws MalformedURLException {
        this.classesList = list;
    }

   @Parameters
    public static Iterable<Object[]> data() throws IOException, ClassNotFoundException {

        caseDirs = getMainDirsNames();
        List<Object[]> testParameters = new ArrayList<>();

        for (File caseDir : caseDirs) {
            List<TestClassDTO> objectList = new ArrayList<>();
            Properties caseProperties = new Properties();

            insideDirs = (List<File>) FileUtils.listFilesAndDirs(
                    caseDir,
                    new IOFileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return false;
                        }

                        @Override
                        public boolean accept(File dir, String name) {
                            return false;
                        }
                    },
                    TrueFileFilter.INSTANCE
            );
            caseProps = (List<File>) FileUtils.listFiles(caseDir, null, false);
            caseProperties.load(new FileInputStream(caseProps.get(0)));
            insideDirs.remove(0);
            for (File insideDir : insideDirs) {
                filesList = (List<File>)FileUtils.listFiles(
                        insideDir,
                        TrueFileFilter.INSTANCE,
                        TrueFileFilter.INSTANCE
                );
                for (File classFile : filesList) {
                    String[] s = insideDir.getPath().split("\\\\|\\/");
                    TestClassDTO testClassDTO = new TestClassDTO(
                            classFile.getName().split("\\.")[0],
                            s[s.length - 2] + "/" + s[s.length - 1] + "/",
                            caseProperties.getProperty("json"),
                            caseProperties.getProperty("url"),
                            Boolean.parseBoolean(caseProperties.getProperty("checkLocatorUniqueness")),
                            Boolean.parseBoolean(caseProperties.getProperty("forceGenerateFiles"))
                    );
                    objectList.add(testClassDTO);
                }

            }
            Object[] arr = new Object[] {objectList};
            testParameters.add(arr);
            filesList.clear();
        }
        return testParameters;
    }

    private static List<File> getMainDirsNames() {
        List<File> caseDirs = (List<File>) FileUtils.listFilesAndDirs(
                new File(RESOURCE_DIR + MANUAL_DIR),
                new IOFileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return false;
                    }

                    @Override
                    public boolean accept(File dir, String name) {
                        return false;
                    }
                },
                new IOFileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return Pattern.compile("manual(\\\\|\\/)[a-zA-Z]+$").matcher(file.getPath()).find();
                    }

                    @Override
                    public boolean accept(File dir, String name) {
                        return true;
                    }
                }
        );
        caseDirs.remove(0);
        return caseDirs;
    }

    private void compileTestClasses() throws Exception {

        PageObjectsGenerator pog = initPog(
                jsonSourcePath,
                url,
                checkLocatorUniqueness,
                forceGenerateFiles);

        pog.generatePageObjects();

        /*JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int c1 = compiler.run(null, null, null, inputJavaFileTest);
        int c2 = compiler.run(null, null, null, inputJavaFileManual);*/
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(null, null, null);
        String[] options = new String[]{"-d", "target/test-classes"};
        File[] files = new File[]{
                new File(inputJavaFileTest),
                new File(inputJavaFileManual)
        };

        JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, null, null,
                Arrays.asList(options),
                null,
                standardJavaFileManager.getJavaFileObjects(files));
        boolean compileSuccess = compilationTask.call();
        assertEquals(true, compileSuccess);

        CustomClassLoader classLoader = new CustomClassLoader();

        testingClass = classLoader.loadClass(this.generatedClassFullName);
        manualClass = classLoader.loadClass(this.manualClassFullName);

        System.out.println(testingClass.getClassLoader());
        System.out.println(manualClass.getClassLoader());
    }

    @Test
    public void runForPackage() throws Exception {
        classesList.sort((pathToJavaFile1, pathToJavaFile2) -> {
            if (pathToJavaFile1.getSmallPath().endsWith("page/")) {
                return -1;
            }
            if (pathToJavaFile2.getSmallPath().endsWith("page/")) {
                return 1;
            }
            return 0;
        });

        for (TestClassDTO classDTO : classesList) {
            this.inputJavaFileTest = RESOURCE_DIR + TEST_DIR + classDTO.getSmallPath().split("/")[1] + "/" +
                    classDTO.getSmallName() + ".java";
            this.inputJavaFileManual = RESOURCE_DIR + MANUAL_DIR + classDTO.getSmallPath() + classDTO.getSmallName() +
                    ".java";
            this.generatedClassFullName = TEST_CLASS_PACKAGE_NAME + classDTO.getSmallPath().split("/")[1] + "."
                    + classDTO.getSmallName();
            this.manualClassFullName = MANUAL_CLASS_PACKAGE_NAME + classDTO.getSmallPath().replace("/", ".")
                    + classDTO.getSmallName();
            this.jsonSourcePath = classDTO.getJsonPath();
            this.url = classDTO.getTestURL();
            this.checkLocatorUniqueness = classDTO.isCheckLocatorUniqueness();
            this.forceGenerateFiles = classDTO.isForceGenerateFiles();
            runTestCase();
        }
    }

    private void runTestCase() throws Exception {
        compileTestClasses();
        testNameOfClass();
        testAnnotationOfClass();
        testModifiersOfClass();
        testSuperClassOfClass();
        testFields();
    }

    private void testNameOfClass() throws Exception {
        String[] className = testingClass.getName().split("\\.");
        String[] className1 = manualClass.getName().split("\\.");
        assertEquals("Different class names", className[className.length - 1],
                className1[className1.length - 1]);
    }

    private void testAnnotationOfClass() {
        List<Annotation> classAnnotations = Arrays.asList(testingClass.getDeclaredAnnotations());
        List<Annotation> classAnnotations1 = Arrays.asList(manualClass.getDeclaredAnnotations());
        assertEquals("Different class annotations", classAnnotations1, classAnnotations);
    }

    private void testModifiersOfClass() {
        int mods = testingClass.getModifiers();
        int mods1 = manualClass.getModifiers();
        assertEquals("Different class modifiers", mods, mods1);
    }

    private void testSuperClassOfClass() {
        Class<?> actualSuperClass = testingClass.getSuperclass();
        Class<?> expectedSuperClass1 = manualClass.getSuperclass();
        assertEquals("Different superclasses", actualSuperClass, expectedSuperClass1);
    }

    private void testFields() {
        List<Field> fields = Arrays.asList(testingClass.getDeclaredFields());
        List<Field> fields1 = Arrays.asList(manualClass.getDeclaredFields());
        assertEquals("Different number of fields", fields.size(), fields1.size());

        Iterator<Field> it = fields.iterator();
        Iterator<Field> it1 = fields1.iterator();

        while (it.hasNext()) {
            Field currentField = it.next();
            Field currentField1 = it1.next();

            List<Annotation> fieldAnnotations = Arrays
                    .asList(currentField.getDeclaredAnnotations());
            List<Annotation> fieldAnnotations1 = Arrays
                    .asList(currentField1.getDeclaredAnnotations());
            assertEquals("Different field annotations", fieldAnnotations, fieldAnnotations1);

            int fieldModifiers = currentField.getModifiers();
            int fieldModifiers1 = currentField1.getModifiers();
            assertEquals("Different field modifiers", fieldModifiers, fieldModifiers1);

            String[] newName = currentField.toString().split(" ");
            String[] newName1 = currentField1.toString().split(" ");
            String importForField = newName[1];
            String importForField1 = newName1[1];
            assertEquals("Different import for field", importForField, importForField1);
            assertEquals("Different field names", currentField.getName(), currentField1.getName());

            //TODO have a problem with field type, when have import remote class, then it's ok, but when our
            //TODO class depend from another our class we get a problem
            /*String type = currentField.getType().getName();
            String type1 = currentField1.getType().getName();
            assertEquals("Different field type",type, type1);*/
        }
    }
}
