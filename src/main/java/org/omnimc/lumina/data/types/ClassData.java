package org.omnimc.lumina.data.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * {@link ClassData} holds all class information from methods to dependent classes.
 * <p>
 * This class know exactly what the current class has.
 *
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class ClassData {

    /**
     * This is the unobfuscatedName.
     */
    private final String className;

    private final ArrayList<String> dependentClasses = new ArrayList<>();

    private final Map<String, FieldData> fields = new HashMap<>();
    private final Map<String, FieldData> privateFields = new HashMap<>();

    private final Map<String, MethodData> methods = new HashMap<>();
    private final Map<String, MethodData> privateMethods = new HashMap<>();

    public ClassData(String className) {
        this.className = className;
    }

    /**
     * @return The unobfuscatedName.
     */
    public String getClassName() {
        return className;
    }

    public void addDependentClass(String className) {
        if (dependentClasses.contains(className)) {
            return;
        }

        dependentClasses.add(className);
    }

    public ArrayList<String> getDependentClasses() {
        return dependentClasses;
    }

    public void addMethod(String obfuscatedName, String unObfuscatedName, String descriptor) {
        if (methods.containsKey(obfuscatedName + descriptor)) {
            return;
        }

        methods.put(obfuscatedName + descriptor, new MethodData(obfuscatedName, unObfuscatedName, descriptor));
    }

    public MethodData getMethod(String obfuscatedMethodName, String descriptor) {
        return methods.getOrDefault(obfuscatedMethodName + descriptor, new MethodData(obfuscatedMethodName, obfuscatedMethodName, descriptor));
    }

    public Map<String, MethodData> getMethods() {
        return methods;
    }

    public void addPrivateMethod(String obfuscatedName, String unObfuscatedName, String descriptor) {
        if (privateMethods.containsKey(obfuscatedName + descriptor)) {
            return;
        }

        privateMethods.put(obfuscatedName + descriptor, new MethodData(obfuscatedName, unObfuscatedName, descriptor));
    }

    public MethodData getPrivateMethod(String obfuscatedMethodName, String descriptor) {
        return privateMethods.getOrDefault(obfuscatedMethodName + descriptor, new MethodData(obfuscatedMethodName, obfuscatedMethodName, descriptor));
    }

    public Map<String, MethodData> getPrivateMethods() {
        return privateMethods;
    }

    public void addField(String obfuscatedName, String unObfuscatedName, String descriptor) {
        if (fields.containsKey(obfuscatedName + descriptor)) {
            return;
        }

        fields.put(obfuscatedName + descriptor, new FieldData(obfuscatedName, unObfuscatedName, descriptor));
    }

    public FieldData getField(String obfuscatedFieldName, String descriptor) {
        return fields.getOrDefault(obfuscatedFieldName + descriptor, new FieldData(obfuscatedFieldName, obfuscatedFieldName, descriptor));
    }

    public Map<String, FieldData> getFields() {
        return fields;
    }

    public void addPrivateField(String obfuscatedName, String unObfuscatedName, String descriptor) {
        if (privateFields.containsKey(obfuscatedName + descriptor)) {
            return;
        }

        privateFields.put(obfuscatedName + descriptor, new FieldData(obfuscatedName, unObfuscatedName, descriptor));
    }

    public FieldData getPrivateField(String obfuscatedFieldName, String descriptor) {
        return privateFields.getOrDefault(obfuscatedFieldName + descriptor, new FieldData(obfuscatedFieldName, obfuscatedFieldName, descriptor));
    }

    public Map<String, FieldData> getPrivateFields() {
        return privateFields;
    }

    @Override
    public String toString() {
        return "ClassData{" +
                "className='" + className + '\'' +
                ", dependentClasses=" + dependentClasses +
                ", fields=" + fields +
                ", privateFields=" + privateFields +
                ", methods=" + methods +
                ", privateMethods=" + privateMethods +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClassData classData = (ClassData) o;
        return Objects.equals(className, classData.className) &&
                Objects.equals(dependentClasses, classData.dependentClasses) &&
                Objects.equals(fields, classData.fields) &&
                Objects.equals(privateFields, classData.privateFields) &&
                Objects.equals(methods, classData.methods) &&
                Objects.equals(privateMethods, classData.privateMethods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, dependentClasses, fields, privateFields, methods, privateMethods);
    }
}