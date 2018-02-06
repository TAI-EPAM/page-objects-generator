package com.epam.page.object.generator.example;

import com.epam.page.object.generator.PageObjectGeneratorFactory;
import com.epam.page.object.generator.PageObjectsGenerator;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class SelenideExampleTest {

    private String outputDir = "src/test/resources/";
    private String packageName = "selenideTest";

    private PageObjectsGenerator pog;

    @Before
    public void setUp() throws IOException {
        FileUtils.deleteDirectory(new File(outputDir + packageName));
        pog = PageObjectGeneratorFactory
                .getPageObjectGenerator(packageName, "/groups.json", false);
    }

    @Test
    public void exampleSelenideElementCSS() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/example/selenide_element_css.json", outputDir,
                Collections.singletonList("/example/example.html"));
    }

    @Test
    public void exampleSelenideElementXPATH() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/example/selenide_element_xpath.json", outputDir,
                Collections.singletonList("/example/example.html"));
    }
}
