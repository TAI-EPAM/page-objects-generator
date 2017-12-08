package com.epam.page.object.generator.integrationalTests;

class TestClassDTO {
    private String smallName;
    private String smallPath;
    private String jsonPath;
    private String testURL;
    private boolean checkLocatorUniqueness;
    private boolean forceGenerateFiles;

    TestClassDTO(String smallName,
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

    String getSmallName() {
        return smallName;
    }

    String getSmallPath() {
        return smallPath;
    }

    String getJsonPath() {
        return jsonPath;
    }

    String getTestURL() {
        return testURL;
    }

    boolean isCheckLocatorUniqueness() {
        return checkLocatorUniqueness;
    }

    boolean isForceGenerateFiles() {
        return forceGenerateFiles;
    }
}
