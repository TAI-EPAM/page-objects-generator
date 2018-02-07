package com.epam.page.object.generator.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadFileIntoList {

    public static List<String> ReadFileIntoList (String filePath){

        List<String> list = new ArrayList<>();

        try (Stream<String> stream = Files. lines(Paths.get(filePath))) {
            list = stream
                .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
