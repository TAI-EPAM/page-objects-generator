package com.epam.page.object.generator.model;

/**
 * Forms pairs of JDI elements and Selenium annotations
 */
public class ClassAndAnnotationPair {

    /**
     * JDI class for specific web element
     */
    private Class<?> elementClass;
    /**
     * Selenium annotation
     */
    private Class<?> elementAnnotation;

    public ClassAndAnnotationPair(Class<?> elementClass, Class<?> elementAnnotation) {
        this.elementClass = elementClass;
        this.elementAnnotation = elementAnnotation;
    }

    public Class<?> getElementClass() {
        return elementClass;
    }

    public Class<?> getElementAnnotation() {
        return elementAnnotation;
    }

}