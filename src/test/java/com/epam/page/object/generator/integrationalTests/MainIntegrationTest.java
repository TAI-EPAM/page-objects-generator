package com.epam.page.object.generator.integrationalTests;

import com.epam.page.object.generator.PageObjectsGenerator;
import com.epam.page.object.generator.adapter.JavaPoetAdapter;
import com.epam.page.object.generator.containers.SupportedTypesContainer;
import com.epam.page.object.generator.parser.JsonRuleMapper;
import com.epam.page.object.generator.utils.XpathToCssTransformation;
import com.epam.page.object.generator.validators.ValidatorsStarter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MainIntegrationTest {
    private static final String RESOURCE_DIR = "src/test/resources/";
    private static final String TEST_DIR = "test/";
    private static final String MANUAL_DIR = "manual/";
    private static final String TEST_CLASS_PACKAGE_NAME = "test.";
    private static final String MANUAL_CLASS_PACKAGE_NAME = "manual.";
    private static final String PACKAGE_TEST_NAME = "test";
    private static final IOFileFilter IO_FILE_FILTER = new IOFileFilter() {
        @Override
        public boolean accept(File file) {
            return false;
        }

        @Override
        public boolean accept(File dir, String name) {
            return false;
        }
    };

    private boolean checkLocatorUniqueness;
    private boolean forceGenerateFiles;
    private String inputJavaFileTest;
    private String inputJavaFileManual;
    private String generatedClassFullName;
    private String manualClassFullName;
    private String jsonSourcePath;
    private String url;
    private Class testClass;
    private Class manualClass;

    private List<TestClassDTO> classesList;

    @SuppressWarnings("Duplicates")
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

    public MainIntegrationTest(List<TestClassDTO> list) {
        this.classesList = list;
    }

    @Parameters
    public static Iterable<Object[]> data() throws IOException, ClassNotFoundException {

        List<File> filesList = new ArrayList<>();
        List<File> caseDirs = getMainDirsNames();
        List<Object[]> testParameters = new ArrayList<>();

        for (File caseDir : caseDirs) {
            List<TestClassDTO> objectList = new ArrayList<>();
            Properties caseProperties = new Properties();

            List<File> insideDirs = new ArrayList<>(FileUtils.listFilesAndDirs(
                    caseDir,
                    IO_FILE_FILTER,
                    TrueFileFilter.INSTANCE
            ));
            List<File> caseProps = new ArrayList<>(FileUtils.listFiles(caseDir, null, false));
            caseProperties.load(new FileInputStream(caseProps.get(0)));
            insideDirs.remove(0);
            for (File insideDir : insideDirs) {
                filesList = (List<File>) FileUtils.listFiles(
                        insideDir,
                        TrueFileFilter.INSTANCE,
                        TrueFileFilter.INSTANCE
                );
                for (File classFile : filesList) {
                    //TODO: fix magic numbers
                    //s = src/../google/site
                    //smallPath = google/site
                    String[] s = insideDir.getPath().split("[\\\\/]");
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
            Object[] arr = new Object[]{objectList};
            testParameters.add(arr);
            filesList.clear();
        }
        return testParameters;
    }

    private static List<File> getMainDirsNames() {
        List<File> caseDirs = (List<File>) FileUtils.listFilesAndDirs(
                new File(RESOURCE_DIR + MANUAL_DIR),
                IO_FILE_FILTER,
                new IOFileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return Pattern.compile("manual([\\\\/])[a-zA-Z]+$").matcher(file.getPath())
                                .find();
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
        testClass = classLoader.loadClass(this.generatedClassFullName);
        manualClass = classLoader.loadClass(this.manualClassFullName);

    }

    @Test
    public void runForPackage() throws Exception {
        classesList.sort((pathToJavaFile1, pathToJavaFile2) -> pathToJavaFile1.getSmallPath()
                .endsWith("page/") ? -1 : pathToJavaFile2.getSmallPath().endsWith("page/") ? 1 : 0);

        for (TestClassDTO classDTO : classesList) {
            this.inputJavaFileTest = RESOURCE_DIR + TEST_DIR + classDTO.getSmallPath()
                    .split("/")[1] + "/" + classDTO.getSmallName() + ".java";
            this.inputJavaFileManual = RESOURCE_DIR + MANUAL_DIR + classDTO.getSmallPath()
                    + classDTO.getSmallName() + ".java";
            this.generatedClassFullName = TEST_CLASS_PACKAGE_NAME + classDTO.getSmallPath()
                    .split("/")[1] + "." + classDTO.getSmallName();
            this.manualClassFullName = MANUAL_CLASS_PACKAGE_NAME + classDTO.getSmallPath().
                    replace("/", ".") + classDTO.getSmallName();
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
        String[] testClassName = testClass.getName().split("\\.");
        String[] manualClassName = manualClass.getName().split("\\.");
        assertEquals("Different class names", testClassName[testClassName.length - 1],
                manualClassName[manualClassName.length - 1]);
    }

    private void testAnnotationOfClass() {
        List<Annotation> testClassAnnotations = Arrays.asList(testClass.getDeclaredAnnotations());
        List<Annotation> manualClassAnnotations = Arrays.asList(manualClass.getDeclaredAnnotations());
        assertEquals("Different class annotations", manualClassAnnotations,
                testClassAnnotations);
    }

    private void testModifiersOfClass() {
        int testMods = testClass.getModifiers();
        int manualMods = manualClass.getModifiers();
        assertEquals("Different class modifiers", testMods, manualMods);
    }

    private void testSuperClassOfClass() {
        Class<?> testSuperClass = testClass.getSuperclass();
        Class<?> manualSuperClass = manualClass.getSuperclass();
        assertEquals("Different superclasses", testSuperClass, manualSuperClass);
    }

    private void testFields() {
        List<Field> testFields = Arrays.asList(testClass.getDeclaredFields());
        List<Field> manualFields = Arrays.asList(manualClass.getDeclaredFields());
        assertEquals("Different number of fields", testFields.size(),
                manualFields.size());

        Iterator<Field> testIt = testFields.iterator();
        Iterator<Field> manualIt = manualFields.iterator();

        while (testIt.hasNext()) {
            Field testCurrentField = testIt.next();
            Field manualCurrentField = manualIt.next();

            List<Annotation> testFieldAnnotations = Arrays
                    .asList(testCurrentField.getDeclaredAnnotations());
            List<Annotation> manualFieldAnnotations = Arrays
                    .asList(manualCurrentField.getDeclaredAnnotations());
            assertEquals("Different field annotations", testFieldAnnotations,
                    manualFieldAnnotations);

            int testFieldModifiers = testCurrentField.getModifiers();
            int manualFieldModifiers = manualCurrentField.getModifiers();
            assertEquals("Different field modifiers", testFieldModifiers,
                    manualFieldModifiers);

            String[] newTestName = testCurrentField.toString().split(" ");
            String[] newManualName = manualCurrentField.toString().split(" ");
            String testImportForField = newTestName[1];
            String manualImportForField = newManualName[1];
            assertEquals("Different import for field", testImportForField,
                    manualImportForField);
            assertEquals("Different field names", testCurrentField.getName(),
                    manualCurrentField.getName());
        }
    }
}
