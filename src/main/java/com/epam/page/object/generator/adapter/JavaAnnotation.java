package com.epam.page.object.generator.adapter;

import com.epam.page.object.generator.errors.XpathToCssTransformerException;
import java.io.IOException;
import java.util.List;

public interface JavaAnnotation {

    Class getAnnotationClass();

    List<AnnotationMember> getAnnotationMembers()
        throws XpathToCssTransformerException;

    class AnnotationMember {

        String name;
        String format;
        String arg;
        JavaAnnotation annotation;

        public AnnotationMember(String name, String format, String arg) {
            this.name = name;
            this.format = format;
            this.arg = arg;
        }

        public AnnotationMember(String name, String format, JavaAnnotation annotation) {
            this.name = name;
            this.format = format;
            this.annotation = annotation;
        }

    }
}