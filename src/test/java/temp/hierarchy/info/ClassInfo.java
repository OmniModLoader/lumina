/*
 * MIT License
 *
 * Copyright (c) 2024 OmniMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package temp.hierarchy.info;

import org.jetbrains.annotations.NotNull;
import org.omnimc.asm.file.ClassFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * {@code ClassInfo} holds information about a specific class, including its name, fields, methods, and any classes it
 * depends on. It helps in managing both public and private fields and methods.
 *
 * <p>This class is used to store details about a class and track its dependencies. It can add and retrieve
 * information about fields and methods, whether they are public or private, and keep track of which other classes this
 * class depends on.</p>
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
public class ClassInfo {

    private final String className;

    private final ArrayList<String> dependentClasses = new ArrayList<>();

    private final HashMap<String, FieldInfo> fields = new HashMap<>();
    private final HashMap<String, FieldInfo> privateFields = new HashMap<>();

    private final HashMap<String, MethodInfo> methods = new HashMap<>();
    private final HashMap<String, MethodInfo> privateMethods = new HashMap<>();

    /**
     * <h6>Creates a new {@code ClassInfo} instance for a class with the given name.
     *
     * @param className The name of the class.
     */
    public ClassInfo(@NotNull String className) {
        this.className = className;
    }

    /**
     * <h6>Gets the name of the class.
     *
     * @return The class name.
     */
    @NotNull
    public String getClassName() {
        return className;
    }

    /**
     * <h6>Adds a class that this class depends on.
     *
     * @param className The name of the dependent class.
     */
    public void addDependentClass(@NotNull String className) {
        if (dependentClasses.contains(className)) {
            return;
        }

        dependentClasses.add(className);
    }

    /**
     * <h6>Gets a list of all classes that this class depends on.
     *
     * @return A list of dependent class names.
     */
    @NotNull
    public ArrayList<String> getDependentClasses() {
        return dependentClasses;
    }

    /**
     * <h6>Adds a method to the class, using its obfuscated name, readable name, and descriptor.
     *
     * @param obfuscatedName   The obfuscated name of the method.
     * @param unObfuscatedName The human-readable name of the method.
     * @param descriptor       The method descriptor.
     */
    public void addMethod(@NotNull String obfuscatedName, @NotNull String unObfuscatedName, @NotNull String descriptor) {
        if (methods.containsKey(obfuscatedName + descriptor)) {
            return;
        }

        methods.put(obfuscatedName + descriptor, new MethodInfo(obfuscatedName, unObfuscatedName, descriptor));
    }

    /**
     * <h6>Gets a list of all methods that this class has.
     *
     * @return A list of {@linkplain MethodInfo}'s.
     */
    @NotNull
    public HashMap<String, MethodInfo> getMethods() {
        return methods;
    }

    /**
     * <h6>Adds a private method to the class, using its obfuscated name, readable name, and descriptor.
     *
     * @param obfuscatedName   The obfuscated name of the method.
     * @param unObfuscatedName The human-readable name of the method.
     * @param descriptor       The method descriptor.
     */
    public void addPrivateMethod(@NotNull String obfuscatedName, @NotNull String unObfuscatedName, @NotNull String descriptor) {
        if (privateMethods.containsKey(obfuscatedName + descriptor)) {
            return;
        }

        privateMethods.put(obfuscatedName + descriptor, new MethodInfo(obfuscatedName, unObfuscatedName, descriptor));
    }

    /**
     * <h6>Gets a list of all private methods that this class has.
     *
     * @return A list of {@linkplain MethodInfo}'s.
     */
    @NotNull
    public HashMap<String, MethodInfo> getPrivateMethods() {
        return privateMethods;
    }

    /**
     * <h6>Adds a field to the class, using its obfuscated name, readable name, and descriptor.
     *
     * @param obfuscatedName   The obfuscated name of the field.
     * @param unObfuscatedName The human-readable name of the field.
     * @param descriptor       The field descriptor.
     */
    public void addField(@NotNull String obfuscatedName, @NotNull String unObfuscatedName, @NotNull String descriptor) {
        if (fields.containsKey(obfuscatedName + descriptor)) {
            return;
        }

        fields.put(obfuscatedName + descriptor, new FieldInfo(obfuscatedName, unObfuscatedName, descriptor));
    }

    public void addFields(HashMap<String, FieldInfo> fieldInfos, ClassInfo file) {

        if (className.equals("net/minecraft/client/player/AbstractClientPlayer")) {
            System.out.println("Adding fields from class: " + file.getClassName());
        }

        for (Map.Entry<String, FieldInfo> entry : fieldInfos.entrySet()) {
            if (className.equals("net/minecraft/client/player/AbstractClientPlayer")) {
                System.out.println(entry.getValue().getFieldName());
            }

            fields.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * <h6>Gets a list of all fields that this class has.
     *
     * @return A list of {@linkplain FieldInfo}'s.
     */
    @NotNull
    public HashMap<String, FieldInfo> getFields() {
        return fields;
    }

    /**
     * <h6>Adds a private field to the class, using its obfuscated name, readable name, and descriptor.
     *
     * @param obfuscatedName   The obfuscated name of the field.
     * @param unObfuscatedName The human-readable name of the field.
     * @param descriptor       The field descriptor.
     */
    public void addPrivateField(@NotNull String obfuscatedName, @NotNull String unObfuscatedName, @NotNull String descriptor) {
        if (privateFields.containsKey(obfuscatedName + descriptor)) {
            return;
        }

        privateFields.put(obfuscatedName + descriptor, new FieldInfo(obfuscatedName, unObfuscatedName, descriptor));
    }

    /**
     * <h6>Gets a list of all private fields that this class has.
     *
     * @return A list of {@linkplain FieldInfo}'s.
     */
    @NotNull
    public HashMap<String, FieldInfo> getPrivateFields() {
        return privateFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassInfo classInfo = (ClassInfo) o;
        return Objects.equals(getClassName(), classInfo.getClassName())
               && Objects.equals(getDependentClasses(), classInfo.getDependentClasses())
               && Objects.equals(getFields(), classInfo.getFields())
               && Objects.equals(getMethods(), classInfo.getMethods());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassName(), getDependentClasses(), getFields(), getMethods());
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "className='" + className + '\'' +
                ", dependentClasses=" + dependentClasses +
                ", fields=" + fields +
                ", methods=" + methods +
                '}';
    }
}