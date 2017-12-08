package com.epam.page.object.generator.integrationalTests;

import java.io.IOException;
import java.io.InputStream;

public class CustomClassLoader extends ClassLoader {

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith("test.") || name.startsWith("manual.")) {
            try {
                InputStream is = getParent().getResourceAsStream(
                        name.replace('.', '/') + ".class"
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
