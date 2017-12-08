package com.epam.page.object.generator.integrationalTests;

import java.io.IOException;
import java.io.InputStream;

public class CustomClassLoader extends ClassLoader {
    private static final String CLASS_EXTENSION = ".class";
    private static final String TEST_PACKAGE = "test";
    private static final String MANUAL_PACKAGE = "manual";

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith(TEST_PACKAGE) || name.startsWith(MANUAL_PACKAGE)) {
            try {
                InputStream is = getParent().getResourceAsStream(
                        name.replace('.', '/') + CLASS_EXTENSION
                );
                byte[] buf = new byte[10000];
                int len = is.read(buf);
                return defineClass(name, buf, 0, len);
            } catch (IOException e) {
                throw new ClassNotFoundException("", e);
            }
        }
        return getParent().loadClass(name);
    }
}
