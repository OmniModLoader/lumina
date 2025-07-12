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

package org.omnimc.lumina.deserialization.compressed;

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
 * A {@link AbstractMappingDeserializer} implementation for handling compressed mapping deserialization.
 *
 * <p>This class provides logic for serializing mapping data into a compressed format that is
 * human-readable but compact. It supports deserialization into:
 * <ul>
 *   <li>A single file with the extension `.mmap`.</li>
 *   <li>A string representation of the mapping data.</li>
 * </ul>
 * Multiple files are not supported by this deserializer.</p>
 *
 * <p>Entries written by this deserializer include class, method, and field mappings, where:
 * <ul>
 *   <li>Class entries are prefixed with `CLASS`.</li>
 *   <li>Method entries are prefixed with `METHOD`.</li>
 *   <li>Field entries are prefixed with `FIELD`.</li>
 * </ul>
 * </p>
 *
 * @see AbstractMappingDeserializer
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class CompressedDeserializer extends AbstractMappingDeserializer {

    /**
     * Serializes the provided {@link Mappings} object into a string representation.
     *
     * <p>The returned string is encoded in a compact format that uses prefixes
     * (`CLASS`, `METHOD`, `FIELD`) to define different types of mappings.</p>
     *
     * @param mappings The {@link Mappings} object containing data to serialize.
     * @return A string representation of the mapping data.
     */
    @Override
    public String deserializeToString(@NotNull Mappings mappings) {
        return makeOutput(mappings);
    }

    /**
     * Serializes the given {@link Mappings} object into a single `.mmap` file.
     *
     * <p>The output file must:
     * <ul>
     *   <li>Have a `.mmap` file extension.</li>
     *   <li>Not be a directory.</li>
     *   <li>Be writable.</li>
     * </ul>
     * </p>
     *
     * @param mappings The {@link Mappings} to serialize.
     * @param file     The target file for the serialized output.
     * @return {@code true} if the operation is successful, otherwise {@code false}.
     */
    @Override
    public boolean deserializeToFile(@NotNull Mappings mappings, @NotNull File file) {
        Objects.requireNonNull(mappings, "Mappings is null");
        Objects.requireNonNull(file, "File is null");

        if (file.isDirectory()) {
            this.fail(FailedState.of("File cannot be a directory!", this.getClass()));
            return false;
        }

        if (!file.getName().endsWith(".mmap")) {
            this.fail(FailedState.of("File must end with `.mmap`", this.getClass()));
            return false;
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                this.fail(FailedState.of("Failed to create the file while it didn't exist.", e, this.getClass()));
                return false;
            }
        } else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                this.fail(FailedState.of("Failed to create the file after deleting a previous version of said file.", e, this.getClass()));
                return false;
            }
        }

        String output = makeOutput(mappings);

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(output);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            this.fail(FailedState.of(e, this.getClass()));
            return false;
        }

        return true;
    }

    /**
     * Deserialization to multiple files is unsupported by this deserializer.
     *
     * @param mappings  The {@link Mappings} object.
     * @param locations An array of files to which mappings will be serialized.
     * @return Always returns {@code false}.
     */
    @Override
    public boolean deserializeToFiles(@NotNull Mappings mappings, @NotNull File[] locations) {
        this.fail(FailedState.of("Compressed Deserializer does not support Multiple Files!", this.getClass()));
        return false;
    }

    /**
     * Converts the given {@link Mappings} object into a formatted string.
     *
     * <p>Each mapping is written as a line in the following format:
     * <ul>
     *   <li><b>Class:</b> {@code CLASS obfuscated:deobfuscated}</li>
     *   <li><b>Method:</b> {@code METHOD obfuscated:deobfuscated}</li>
     *   <li><b>Field:</b> {@code FIELD obfuscated:deobfuscated}</li>
     * </ul>
     * Method and field mappings are grouped under their respective classes.</p>
     *
     * @param mainMappings The {@link Mappings} object to format.
     * @return A formatted string representing the mappings.
     */
    private String makeOutput(Mappings mainMappings) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> classEntry : mainMappings.getClasses().entrySet()) {
            String obfuscatedName = classEntry.getKey();
            String unObfuscatedName = classEntry.getValue();

            builder.append("CLASS")
                    .append(" ")
                    .append(obfuscatedName)
                    .append(":")
                    .append(unObfuscatedName)
                    .append("\n");

            Map<String, Map<String, String>> methods = mainMappings.getMethods();
            if (methods != null) {
                Map<String, String> methodMappings = methods.get(obfuscatedName);
                if (methodMappings != null) {
                    for (Map.Entry<String, String> methodNames : methodMappings.entrySet()) {
                        String obfuscatedMethodName = methodNames.getKey();
                        String unObfuscatedMethodName = methodNames.getValue();

                        builder.append("METHOD")
                                .append(" ")
                                .append(obfuscatedMethodName)
                                .append(":")
                                .append(unObfuscatedMethodName)
                                .append("\n");
                    }
                }
            }


            Map<String, Map<String, String>> fields = mainMappings.getFields();
            if (fields != null) {
                Map<String, String> fieldMappings = fields.get(obfuscatedName);
                if (fieldMappings != null) {
                    for (Map.Entry<String, String> fieldNames : fieldMappings.entrySet()) {
                        String obfuscatedFieldName = fieldNames.getKey();
                        String unObfuscatedFieldName = fieldNames.getValue();

                        builder.append("FIELD")
                                .append(" ")
                                .append(obfuscatedFieldName)
                                .append(":")
                                .append(unObfuscatedFieldName)
                                .append("\n");
                    }
                }
            }
        }

        return builder.toString();
    }
}