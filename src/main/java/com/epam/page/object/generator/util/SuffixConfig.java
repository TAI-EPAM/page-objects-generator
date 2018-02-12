package com.epam.page.object.generator.util;

import java.util.List;

public class SuffixConfig {

    private boolean enable;
    private List<String> allowedSuffixes;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<String> getAllowedSuffixes() {
        return allowedSuffixes;
    }

    public void setAllowedSuffixes(List<String> allowedSuffixes) {
        this.allowedSuffixes = allowedSuffixes;
    }

}
