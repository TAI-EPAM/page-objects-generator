package com.epam.page.object.generator.adapter;

import com.epam.page.object.generator.model.Selector;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaField is struct class that contains necessary attributes for field of {@link JavaClass}
 */
public class JavaField {

    private String fullClassField = "";
    private String fieldName;
    private JavaAnnotation annotation;
    private Modifier[] modifiers;
    private Map<String, Object[]> initializer;
    private TypeName typeName;
    private boolean isAnnotation;

    public JavaField(String fullClassField, String fieldName, JavaAnnotation annotation,
                     Modifier[] modifiers) {
        this.fullClassField = fullClassField;
        this.fieldName = fieldName;
        this.annotation = annotation;
        this.modifiers = modifiers;
    }

    public JavaField(TypeName typeName, String fieldName, JavaAnnotation annotation,
                     Modifier[] modifiers) {
        this.typeName = typeName;
        this.fieldName = fieldName;
        this.annotation = annotation;
        this.modifiers = modifiers;
    }

    public TypeName getTypeName() {
        return this.typeName;
    }

    public boolean isSelenideTypeField() {
        return fullClassField.contains("Selenide") && !isAnnotation;
    }

    public void setAnnotationFlag(boolean isAnnotation) {
        this.isAnnotation = isAnnotation;
    }

    public void setInitializer(Selector selector) {
        initializer = new HashMap<>();
        String prefix = selector.getType().equals("xpath")
                ? "$x"
                : "$";
        initializer.put("$L($S)", new String[] {prefix, selector.getValue()});
    }

    Map<String, Object[]> getInitializer() {
        return this.initializer;
    }

    public String getFullFieldClass() {
        return fullClassField;
    }

    public JavaAnnotation getAnnotation() {
        return annotation;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Modifier[] getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return "JavaField{" +
                "fullClassField='" + fullClassField + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", annotation=" + annotation +
                ", modifiers=" + Arrays.toString(modifiers) +
                '}';
    }
}
