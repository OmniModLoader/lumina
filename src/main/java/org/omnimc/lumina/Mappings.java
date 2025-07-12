/*
 * MIT License
 *
 * Copyright (c) 2024-2025 OmniMC
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

package org.omnimc.lumina;

import org.jetbrains.annotations.NotNull;
import org.omnimc.lumina.deserialization.compressed.CompressedDeserializer;
import org.omnimc.lumina.deserialization.full.FullDeserializer;
import org.omnimc.lumina.deserialization.parameter.ParameterDeserializer;
import org.omnimc.lumina.param.ParameterMap;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code Mappings} class is responsible for maintaining mappings between obfuscated
 * and de-obfuscated elements of a program. This includes mappings for classes, methods, fields,
 * and parameters, which are used to interpret obfuscated code and map it back to meaningful names.
 *
 * <p>The class provides utilities for retrieving, adding, and managing these mappings, with methods
 * to handle individual elements as well as bulk operations. It also integrates with different types
 * of deserializers to output mappings in various formats.</p>
 *
 * <p>This class is designed to be thread-safe due to the use of {@link ConcurrentHashMap}.</p>
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class Mappings {

    /**
     * Creates an empty {@code Mappings} object.
     *
     * @return A new instance of {@code Mappings}.
     */
    public static Mappings of() {
        return new Mappings();
    }

    /**
     * Map for class mappings where the key is the obfuscated class name
     * and the value is the de-obfuscated class name.
     */
    private final Map<String, String> classes;

    /**
     * Map for method mappings where the key is the obfuscated method name,
     * and the value is another map. The inner map maps obfuscated class names
     * to de-obfuscated method names.
     */
    private final Map<String, Map<String, String>> methods;

    /**
     * Map for field mappings where the key is the obfuscated class name,
     * and the value is another map. The inner map maps obfuscated field names
     * to de-obfuscated field names.
     */
    private final Map<String, Map<String, String>> fields;

    /**
     * A {@link ParameterMap} for managing parameter name mappings.
     */
    private final ParameterMap parameters;

    /**
     * Constructs an empty {@code Mappings} object with default empty data structures.
     */
    public Mappings() {
        this(new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ParameterMap());
    }

    /**
     * Constructs a {@code Mappings} object with the specified data.
     *
     * @param classes      Mappings for class names.
     * @param methods      Mappings for method names.
     * @param fields       Mappings for field names.
     * @param parameterMap Mapping for parameters.
     */
    public Mappings(@NotNull Map<String, String> classes, @NotNull Map<String, Map<String, String>> methods,
                    @NotNull Map<String, Map<String, String>> fields, @NotNull ParameterMap parameterMap) {
        this.classes = classes;
        this.methods = methods;
        this.fields = fields;
        this.parameters = parameterMap;
    }

    /**
     * Retrieves all class mappings.
     *
     * @return A copy of the class mappings.
     */
    public Map<String, String> getClasses() {
        return new ConcurrentHashMap<>(classes);
    }

    /**
     * Retrieves the de-obfuscated class name for a given obfuscated name.
     *
     * @param obfuscatedName The obfuscated class name.
     * @return The de-obfuscated class name, or {@code null} if not found.
     */
    public String getClassName(@NotNull String obfuscatedName) {
        return classes.getOrDefault(obfuscatedName, obfuscatedName);
    }

    /**
     * Retrieves the obfuscated class name from a de-obfuscated name.
     *
     * @param unObfuscatedName The de-obfuscated class name.
     * @return The obfuscated class name, or {@code null} if not found.
     */
    public String getClassNameByValue(@NotNull String unObfuscatedName) {
        Optional<Map.Entry<String, String>> nameOptional = classes
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(unObfuscatedName))
                .findFirst();

        return nameOptional.map(Map.Entry::getKey).orElse(null);
    }

    /**
     * Adds a new class mapping.
     *
     * @param obfuscatedName   The obfuscated class name.
     * @param unObfuscatedName The de-obfuscated class name.
     * @return {@code true} if an existing mapping was replaced, otherwise {@code false}.
     */
    public boolean addClass(@NotNull String obfuscatedName, @NotNull String unObfuscatedName) {
        String put = classes.put(obfuscatedName, unObfuscatedName);
        return put != null;
    }

    /**
     * Retrieves all method mappings.
     *
     * @return A copy of the method mappings.
     */
    public Map<String, Map<String, String>> getMethods() {
        return new ConcurrentHashMap<>(methods);
    }

    /**
     * Gets the de-obfuscated method name for the given obfuscated class and method names.
     *
     * @param obfuscatedClassName  The obfuscated class name.
     * @param obfuscatedMethodName The obfuscated method name.
     * @return The de-obfuscated method name, or {@code null} if not found.
     */
    public String getMethodName(@NotNull String obfuscatedClassName, @NotNull String obfuscatedMethodName, String descriptor) {
        Map<String, String> methodMap = methods.get(obfuscatedClassName);

        if (methodMap == null) {
            return obfuscatedMethodName;
        }

        return methodMap.getOrDefault(obfuscatedMethodName + descriptor, obfuscatedMethodName);
    }

    /**
     * Adds a new method mapping.
     *
     * @param obfuscatedClassName  The obfuscated class name.
     * @param obfuscatedMethodName The obfuscated method name.
     * @param methodName           The de-obfuscated method name.
     * @return {@code true} if the mapping was successfully added.
     */
    public boolean addMethod(@NotNull String obfuscatedClassName, @NotNull String obfuscatedMethodName, @NotNull String methodName) {
        return addToMap(obfuscatedClassName, obfuscatedMethodName, methodName, methods);
    }

    public Map<String, Map<String, String>> getFields() {
        return new ConcurrentHashMap<>(fields);
    }

    public String getFieldName(@NotNull String obfuscatedClassName, @NotNull String obfuscatedFieldName, String descriptor) {
        Map<String, String> fieldMap = fields.get(obfuscatedClassName);
        if (fieldMap == null) {
            return obfuscatedFieldName;
        }

        return fieldMap.getOrDefault(obfuscatedFieldName + descriptor, obfuscatedFieldName);
    }

    public boolean addField(@NotNull String obfuscatedClassName, @NotNull String obfuscatedFieldName, @NotNull String fieldName) {
        return addToMap(obfuscatedClassName, obfuscatedFieldName, fieldName, fields);
    }

    public ParameterMap getParameterMap() {
        return parameters;
    }

    public void addAllClasses(@NotNull Mappings mappings) {
        addAllClasses(mappings.getClasses());
    }

    public void addAllClasses(@NotNull Map<String, String> classes) {
        this.classes.putAll(classes);
    }

    public void addAllMethods(@NotNull Mappings mappings) {
        addAllMethods(mappings.getMethods());
    }

    public void addAllMethods(@NotNull Map<String, Map<String, String>> methods) {
        this.methods.putAll(methods);
    }

    public void addAllFields(@NotNull Mappings mappings) {
        addAllFields(mappings.getFields());
    }

    public void addAllFields(@NotNull Map<String, Map<String, String>> fields) {
        this.fields.putAll(fields);
    }

    public void addAllParameters(@NotNull Mappings mappings) {
        addAllParameters(mappings.parameters);
    }

    public void addAllParameters(@NotNull ParameterMap params) {
        this.parameters.addAll(params);
    }

    public void addAll(@NotNull Mappings mappings) {
        addAllClasses(mappings);
        addAllMethods(mappings);
        addAllFields(mappings);
        addAllParameters(mappings);
    }

    /**
     * Converts the mapping to a string representation based on the specified {@link MappingType}.
     *
     * @param type The type of the mapping format.
     * @return A string representation of the mappings.
     */
    public String toString(MappingType type) {
        return switch (type) {
            case PARAMETERS -> {
                ParameterDeserializer deserializer = new ParameterDeserializer();
                yield deserializer.deserializeToString(this);
            }
            case COMPRESSED -> {
                CompressedDeserializer deserializer = new CompressedDeserializer();
                yield deserializer.deserializeToString(this);
            }
            case FULL -> {
                FullDeserializer deserializer = new FullDeserializer();
                yield deserializer.deserializeToString(this);
            }
            default -> "Mappings{" +
                    "classes=" + classes +
                    ", methods=" + methods +
                    ", fields=" + fields +
                    ", parameters=" + parameters +
                    '}';
        };
    }

    @Override
    public String toString() {
        return toString(MappingType.UNKNOWN);
    }

    /**
     * Utility method for adding mappings to a nested map structure.
     *
     * @param obfuscatedClassName The obfuscated class name.
     * @param obfuscatedFieldName The obfuscated field or method name.
     * @param fieldName           The de-obfuscated name.
     * @param map                 The map to modify.
     * @return {@code true} if the mapping already existed.
     */
    private boolean addToMap(String obfuscatedClassName, String obfuscatedFieldName, String fieldName, Map<String, Map<String, String>> map) {
        try {
            Map<String, String> specificMap = map.computeIfAbsent(obfuscatedClassName, k -> new ConcurrentHashMap<>());
            specificMap.put(obfuscatedFieldName, fieldName);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }
}