package com.epam.page.object.generator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;


public class JsonFileMapper {

    public static <T> T readFile(String filePath, Class<T> mappedClass) {

        try (InputStream jsonStream = JsonFileMapper.class.getClassLoader()
            .getResourceAsStream(filePath)) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonStream, mappedClass);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
