package com.epam.page.object.generator.integrationalTests;

import static org.junit.Assert.assertEquals;

import com.epam.page.object.generator.PageObjectsGenerator;
import com.epam.page.object.generator.adapter.JavaPoetAdapter;
import com.epam.page.object.generator.containers.SupportedTypesContainer;
import com.epam.page.object.generator.integrationalTests.Data.CompilationResult;
import com.epam.page.object.generator.integrationalTests.Data.TestClassesData;
import com.epam.page.object.generator.parser.JsonRuleMapper;
import com.epam.page.object.generator.utils.XpathToCssTransformation;
import com.epam.page.object.generator.validators.ValidatorsStarter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests if .java files that generates POG corresponds to the .java files that we expect.
 *
 * Usually POG generates set of classes that corresponds to specified web-site. POG object
 * has parameter that specify main directory where all generated classes will be stored.
 *
 * For example: POG object's parameter (main directory) value = /test and testing site = google.com,
 * so POG will generate following directories with .java files:
 * /test/site with Site.java
 * /test/page with Google.java
 *
 * Main goal of MainIntegrationTest is to compare generated set of classes
 * (from example above: Site.java, Google.java)
 * to set of manual classes that can be found in src/test/resources/manual/TEST_CASE_DIR
 * (it will be src/test/resources/manual/google/ for example above), where
 * set of classes for specified site = test case
 *
 * MainIntegrationTest methods (like testFields() or testAnnotations()) is used for testing
 * one class of test case. Test class are just comparing to manual class and this procedure
 * continues for all classes of test case.
 * As it was said above, test case contains set of classes for specified site. So
 * Junit @Parameters annotation is used in MainIntegrationClass, where each parameter represents
 * each test case.
 */
@RunWith(Parameterized.class)
public class MainIntegrationTest {

    private static final String RESOURCE_DIR = FilenameUtils
        .separatorsToSystem("src/test/resources/");
    private static final String MANUAL_DIR = FilenameUtils
        .separatorsToSystem("manual/");
    private static final String PACKAGE_TEST_NAME = "test";
    private static final int MAX_DEPTH_FOR_DIRS = 1;

    private List<TestClassesData> caseClassesList;

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

    public MainIntegrationTest(List<TestClassesData> list) {
        this.caseClassesList = list;
    }

    /**
     * Forms List<Object[]> which contains parameters for testing. Every list's element
     * (it's Object array) contains one parameter - list of TestClassesData objects. This list
     * represents each test case. For this purpose data() method scans
     * through scr/test/resources/manual to find all test cases dirs and then form list
     * of TestClassesData objects for every test case
     *
     * About regex: .+\.[a-z]+$
     * This regex is used for matching files with extensions (in this particular situation for
     * matching .java or .properties files). Example:
     * src/test/resources/manual/google/Google.java - matches
     * src/test/resources/manual/google             - doesn't match
     *
     * @return list of parameters set for every test case
     */

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        data();
    }

    @Parameters
    public static Iterable<Object[]> data() throws IOException, ClassNotFoundException {

        List<Path> caseDirs = getCaseDirsNames(
            RESOURCE_DIR + MANUAL_DIR);

        List<Object[]> testParameters = new ArrayList<>();

        for (Path caseDir : caseDirs) {
            List<TestClassesData> objectList = new ArrayList<>();
            Properties caseProperties = new Properties();

            List<Path> insideCaseDirs = Files
                .list(caseDir)
                .filter(s -> Files.isDirectory(s))
                .collect(Collectors.toList());

            Optional<Path> testCasePropertyPath = Files
                .list(caseDir)
                .filter(path -> Files.isRegularFile(path))
                .filter(path -> path.toString().endsWith(".properties"))
                .findAny();
            if (testCasePropertyPath.isPresent()) {
                caseProperties.load(Files.newInputStream(testCasePropertyPath.get()));
            }

            for (Path insideDir : insideCaseDirs) {
                List<Path> javaFilesList = Files
                    .list(insideDir)
                    .filter(path -> Files.isRegularFile(path))
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());

                for (Path classFile : javaFilesList) {
                    TestClassesData testClassesData = new TestClassesData(
                        getFullClassNameFromClassFilePath(classFile.toString()),
                        classFile.toString(),
                        caseProperties.getProperty("json"),
                        caseProperties.getProperty("url"),
                        Boolean.parseBoolean(caseProperties.getProperty("checkLocatorUniqueness")),
                        Boolean.parseBoolean(caseProperties.getProperty("forceGenerateFiles")),
                        getFullClassNameFromClassFilePath(
                            manualFilePathToTestFilePath(classFile.toString())),
                        manualFilePathToTestFilePath(classFile.toString())
                    );
                    objectList.add(testClassesData);
                }
            }
            Object[] arr = new Object[]{objectList};
            testParameters.add(arr);
        }
        return testParameters;
    }

    /**
     * Run tests for every TestClassesData object (in other words for every
     * class that was generated by POG (every class of the test case)).
     * Number of iterations depends on number of classes to test in this particular test case.
     */
    @Test
    public void runForPackage() throws Exception {
        caseClassesList = sortClassesList(caseClassesList);
        for (TestClassesData classDTO : caseClassesList) {
            runTestCase(classDTO);
        }
    }

    /**
     * Compiles two classes (manual and generated by POG)
     *
     * @return CompilationDTO object with Class objects (for manual and generated by POG test
     * classes) and result of compilation
     */
    private CompilationResult prepareAndCompileTestClassesPair(
        TestClassesData currentTestClassesData)
        throws Exception {

        PageObjectsGenerator pog = initPog(
            currentTestClassesData.getJsonPath(),
            currentTestClassesData.getTestURL(),
            currentTestClassesData.isCheckLocatorUniqueness(),
            currentTestClassesData.isForceGenerateFiles());

        TestClassesCompiler compiler = new TestClassesCompiler(pog);
        CompilationResult compilationResult = compiler.compileClasses(
            currentTestClassesData.getTestClassFilePath(),
            currentTestClassesData.getManualClassFilePath(),
            currentTestClassesData.getTestClassFullName(),
            currentTestClassesData.getManualClassFullName()
        );
        assertEquals(true, compilationResult.isCompilationSuccess());
        return compilationResult;
    }

    /**
     * Run all test methods for one generated class (and compare it to manual class)
     */
    private void runTestCase(TestClassesData currentClassesDTO) throws Exception {
        CompilationResult compilationResult = prepareAndCompileTestClassesPair(currentClassesDTO);

        currentClassesDTO.setTestClass(compilationResult.getExpectedClass());
        currentClassesDTO.setManualClass(compilationResult.getManualClass());

        testNameOfClass(currentClassesDTO);
        testAnnotationOfClass(currentClassesDTO);
        testModifiersOfClass(currentClassesDTO);
        testSuperClassOfClass(currentClassesDTO);
        testFields(currentClassesDTO);
    }

    /**
     * Compare names of test and manual classes. For example:
     * test.page.Site           - test class
     * manual.google.page.Site  - manual class
     * will pass assertEquals
     */
    private void testNameOfClass(TestClassesData testClassesData) throws Exception {
        assertEquals("Different class names", testClassesData.getManualClass().getSimpleName(),
            testClassesData.getTestClass().getSimpleName());
    }

    private void testAnnotationOfClass(TestClassesData testClassesData) {
        List<Annotation> testClassAnnotations = Arrays
            .asList(testClassesData.getTestClass().getDeclaredAnnotations());
        List<Annotation> manualClassAnnotations = Arrays
            .asList(testClassesData.getManualClass().getDeclaredAnnotations());
        assertEquals("Different class annotations", manualClassAnnotations,
            testClassAnnotations);
    }

    private void testModifiersOfClass(TestClassesData currentClassesDTO) {
        int testMods = currentClassesDTO.getTestClass().getModifiers();
        int manualMods = currentClassesDTO.getManualClass().getModifiers();
        assertEquals("Different class modifiers", testMods, manualMods);
    }

    private void testSuperClassOfClass(TestClassesData testClassesData) {
        Class<?> testSuperClass = testClassesData.getTestClass().getSuperclass();
        Class<?> manualSuperClass = testClassesData.getManualClass().getSuperclass();
        assertEquals("Different superclasses", testSuperClass, manualSuperClass);
    }

    /**
     * Compares fields of testing class and manual class. Implementation considers order of
     * fields in classes. If fields order of manual and test classes aren't the same, but fields
     * are the same (same name, type, annotations) test will be passed
     */
    private void testFields(TestClassesData testClassesData) {
        List<Field> testFields = Arrays
            .asList(testClassesData.getTestClass().getDeclaredFields());
        List<Field> manualFields = Arrays
            .asList(testClassesData.getManualClass().getDeclaredFields());
        assertEquals("Different number of fields", testFields.size(),
            manualFields.size());

        Comparator<Field> fieldsComparator = (f1, f2) -> {
            String[] typeAndNameField1 = f1.getType().getName().split("\\.");
            String[] typeAndNameField2 = f2.getType().getName().split("\\.");
            return (typeAndNameField1[typeAndNameField1.length - 1] + f1.getName())
                .compareTo((typeAndNameField2[typeAndNameField2.length - 1] + f2.getName()));
        };

        if (manualFields.size() != 0 && testFields.size() != 0) {

            manualFields.sort(fieldsComparator);
            testFields.sort(fieldsComparator);

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

    /**
     * Gets case dirs names. For example: in src/test/manual dir, test case path will be:
     * src/test/manual/TEST_CASE_DIR. Every case dir name is used for searching
     * manual classes in this dir
     *
     * @return list of test cases dirs name
     */
    private static List<Path> getCaseDirsNames(String searchingDir) throws IOException {
        return Files.list(Paths.get(searchingDir))
            .filter(path -> Files.isDirectory(path))
            .collect(Collectors.toList());
    }

    /**
     * Sorts list of TestClassesData objects. It's necessary, because all classes of test case
     * can have dependencies of classes which represents pages. So all classes from page subdir
     * have to be tested first.
     *
     * @param classesList list of test case classes to sort
     * @return sorted list
     */
    private static List<TestClassesData> sortClassesList(List<TestClassesData> classesList) {

        Map<Boolean, List<TestClassesData>> grouped = classesList.stream()
            .collect(Collectors.groupingBy(t -> t.getManualClassFilePath().contains("page")));
        ArrayList<TestClassesData> result = new ArrayList<>(grouped.get(true));
        result.addAll(grouped.get(false));
        return result;
    }

    /**
     * Method is used for getting full class name with .java file path. It could be better
     * to get class full name with Class#getCanonicalName(), but by the time full class name
     * is needed, Class object doesn't exist.
     *
     * @param classFilePath path to .java file
     * @return full class name
     */
    private static String getFullClassNameFromClassFilePath(String classFilePath) {
        int index = classFilePath.indexOf(RESOURCE_DIR);
        String smallPath = classFilePath.substring(index + RESOURCE_DIR.length())
            .replace(FileSystems.getDefault().getSeparator().charAt(0), '.');
        return FilenameUtils.removeExtension(smallPath);
    }

    /**
     * Transform manual class .java file path to generated class .java file path. This two
     * paths are very similar. For example:
     * manual :src/test/resources/manual/google/page/Google.java, where google dir stands for case
     * directory
     * generated: src/test/resources/test/page/Google.java
     * So to get generated path, method delete case directory from manual path.
     *
     * @param manualFilePath path to manual .java file
     * @return path to generated class .java file
     */
    private static String manualFilePathToTestFilePath(String manualFilePath) {
        return manualFilePath.replaceAll("manual([\\\\/])[a-zA-Z]+", PACKAGE_TEST_NAME);
    }
}
