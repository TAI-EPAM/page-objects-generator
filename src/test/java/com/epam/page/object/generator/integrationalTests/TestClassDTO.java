package com.epam.page.object.generator.integrationalTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class TestClassDTO {
    private String smallName;
    private String smallPath;
    private String jsonPath;
    private String testURL;
    private boolean checkLocatorUniqueness;
    private boolean forceGenerateFiles;

    public TestClassDTO(String smallName,
                        String smallPath,
                        String jsonPath,
                        String testURL,
                        boolean checkLocatorUniqueness,
                        boolean forceGenerateFiles) {
        this.smallName = smallName;
        this.smallPath = smallPath;
        this.jsonPath = jsonPath;
        this.testURL = testURL;
        this.checkLocatorUniqueness = checkLocatorUniqueness;
        this.forceGenerateFiles = forceGenerateFiles;
    }

    public String getSmallName() {
        return smallName;
    }

    public String getSmallPath() {
        return smallPath;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public String getTestURL() {
        return testURL;
    }

    public boolean isCheckLocatorUniqueness() {
        return checkLocatorUniqueness;
    }

    public boolean isForceGenerateFiles() {
        return forceGenerateFiles;
    }
}
