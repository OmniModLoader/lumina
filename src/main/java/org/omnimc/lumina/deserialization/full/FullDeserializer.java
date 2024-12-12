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

package org.omnimc.lumina.deserialization.full;

import org.jetbrains.annotations.NotNull;
import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.consumer.FailedState;
import org.omnimc.lumina.deserialization.AbstractMappingDeserializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link AbstractMappingDeserializer} implementation for handling complete mapping deserialization.
 *
 * <p>The `FullDeserializer` allows for the serialization of all mapping categories (classes, fields, and methods) into
 * string and file formats. It supports multiple files, where each file is dedicated to a specific mapping category.</p>
 *
 * <p>The serialized output separates mappings for:
 * <ul>
 *   <li>Classes – Written into `*.class.mmap` files.</li>
 *   <li>Methods – Written into `*.method.mmap` files.</li>
 *   <li>Fields – Written into `*.field.mmap` files.</li>
 * </ul>
 * </p>
 *
 * @see AbstractMappingDeserializer
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class FullDeserializer extends AbstractMappingDeserializer {

    /**
     * Serializes all mappings into a structured, multi-segment string representation.
     *
     * <p>The output is divided into sections with clearly defined headers:</p>
     * <ul>
     *   <li>Classes</li>
     *   <li>Methods</li>
     *   <li>Fields</li>
     * </ul>
     *
     * @param mappings The {@link Mappings} object containing mapping data.
     * @return A formatted string containing all mappings.
     */
    @Override
    public String deserializeToString(@NotNull Mappings mappings) {
        StringBuilder builder = new StringBuilder();

        String classOutput = makeClassOutput(mappings);
        builder.append("#################### Classes ####################\n");
        builder.append(classOutput);
        String methodOutput = makeMethodOutput(mappings);
        builder.append("#################### Methods ####################\n");
        builder.append(methodOutput);
        String fieldOutput = makeFieldOutput(mappings);
        builder.append("#################### Fields ####################\n");
        builder.append(fieldOutput);

        return builder.toString();
    }

    /**
     * Serializes mappings into files when the output directory is specified.
     *
     * <p>If the provided file `file` is a directory, the deserializer will attempt to locate
     * the following files within the directory to write the mappings:
     * <ul>
     *   <li>`.class.mmap` file for class mappings.</li>
     *   <li>`.field.mmap` file for field mappings.</li>
     *   <li>`.method.mmap` file for method mappings.</li>
     * </ul>
     *
     * <p>If an appropriate file is not found in the directory, the method will create the required
     * files. If the provided file is not a directory, this operation fails, as writing to a single
     * file is unsupported by this deserializer.</p>
     *
     * @param mappings The {@link Mappings} object containing mapping data.
     * @param file     The directory to write the output into.
     * @return {@code true} if successful; {@code false} otherwise.
     */
    @Override
    public boolean deserializeToFile(@NotNull Mappings mappings, @NotNull File file) {
        if (file.isDirectory()) {
            File classLocation = null;
            File fieldLocation = null;
            File methodLocation = null;

            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                String name = listFile.getName().toLowerCase();
                if (name.endsWith(".class.mmap")) {
                    classLocation = listFile;
                }

                if (name.endsWith(".field.mmap")) {
                    fieldLocation = listFile;
                }

                if (name.endsWith(".method.mmap")) {
                    methodLocation = listFile;
                }
            }

            return writeOutput(classLocation, fieldLocation, methodLocation, mappings);
        }

        this.fail(FailedState.of("`FullDeserializer` does not support single file deserialization!", this.getClass()));
        return false;
    }

    /**
     * Writes serialized mappings into multiple files based on mapping categories.
     *
     * <p>The appropriate categories are identified by file extensions:
     * <ul>
     *   <li>`.class.mmap` for classes.</li>
     *   <li>`.field.mmap` for fields.</li>
     *   <li>`.method.mmap` for methods.</li>
     * </ul>
     * Existing files are overwritten, and missing files are created.</p>
     *
     * @param mappings  The {@link Mappings} object containing mapping data.
     * @param locations An array of file objects to write data into.
     * @return {@code true} if serialization is successful; {@code false} otherwise.
     */
    @Override
    public boolean deserializeToFiles(@NotNull Mappings mappings, @NotNull File[] locations) {
        Objects.requireNonNull(mappings);
        Objects.requireNonNull(locations);

        File classLocation = null;
        File fieldLocation = null;
        File methodLocation = null;

        for (@NotNull File location : locations) {
            String name = location.getName().toLowerCase();
            if (name.endsWith(".class.mmap")) {
                classLocation = location;
            }

            if (name.endsWith(".field.mmap")) {
                fieldLocation = location;
            }

            if (name.endsWith(".method.mmap")) {
                methodLocation = location;
            }
        }

        return writeOutput(classLocation, fieldLocation, methodLocation, mappings);
    }

    /**
     * Serializes all mappings into their respective output files.
     *
     * <p>The mappings are split into three components:
     * <ul>
     *   <li>Class mappings are written into `classLocation`.</li>
     *   <li>Field mappings are written into `fieldLocation`.</li>
     *   <li>Method mappings are written into `methodLocation`.</li>
     * </ul>
     * Existing files are overwritten, and missing files are created.</p>
     *
     * @param classLocation  The file for class mappings.
     * @param fieldLocation  The file for field mappings.
     * @param methodLocation The file for method mappings.
     * @param mappings       The {@link Mappings} object containing mapping data.
     * @return {@code true} if successful; otherwise {@code false}.
     */
    private boolean writeOutput(File classLocation, File fieldLocation, File methodLocation, Mappings mappings) {
        writeFile(classLocation, makeClassOutput(mappings));
        writeFile(fieldLocation, makeFieldOutput(mappings));
        writeFile(methodLocation, makeMethodOutput(mappings));
        return true;
    }

    /**
     * Writes serialized data into the specified file.
     *
     * @param file The file to write into.
     * @param data The serialized data as a string.
     */
    private void writeFile(File file, String data) {
        if (file != null) {
            if (file.exists()) {
                file.delete();
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(data);
                writer.flush();
            } catch (IOException e) {
                this.fail(FailedState.of(e, this.getClass()));
            }
        }
    }

    private String makeClassOutput(Mappings mappings) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> classEntry : mappings.getClasses().entrySet()) {
            String obfuscatedClassName = classEntry.getKey();
            String unObfuscatedClassName = classEntry.getValue();

            builder.append(obfuscatedClassName).append(":").append(unObfuscatedClassName).append("\n");
        }

        return builder.toString();
    }

    private String makeMethodOutput(Mappings mappings) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> classEntry : mappings.getClasses().entrySet()) {
            String obfuscatedClassName = classEntry.getKey();

            Map<String, String> methodMappings = mappings.getMethods().get(obfuscatedClassName);
            append(builder, obfuscatedClassName, methodMappings);
        }
        return builder.toString();
    }

    private String makeFieldOutput(Mappings mappings) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> classEntry : mappings.getClasses().entrySet()) {
            String obfuscatedClassName = classEntry.getKey();

            Map<String, String> fieldMappings = mappings.getFields().get(obfuscatedClassName);
            append(builder, obfuscatedClassName, fieldMappings);
        }
        return builder.toString();
    }

    private void append(StringBuilder builder, String obfuscatedClassName, Map<String, String> mappings) {
        if (mappings == null) {
            return;
        }

        builder.append(obfuscatedClassName).append(":\n");

        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            String obfuscatedMethodName = entry.getKey();
            String unObfuscatedMethodName = entry.getValue();

            builder.append(obfuscatedMethodName)
                    .append(":")
                    .append(unObfuscatedMethodName)
                    .append("\n");
        }
    }

}