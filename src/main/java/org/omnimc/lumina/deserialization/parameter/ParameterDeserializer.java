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

package org.omnimc.lumina.deserialization.parameter;

import org.jetbrains.annotations.NotNull;
import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.consumer.FailedState;
import org.omnimc.lumina.deserialization.AbstractMappingDeserializer;
import org.omnimc.lumina.param.ParameterMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A {@link AbstractMappingDeserializer} implementation responsible for serializing parameter mappings.
 *
 * <p>The `ParameterDeserializer` handles mappings where parameters of methods are serialized into
 * structured outputs. It generates outputs in both string and file formats, with the following structure:</p>
 * <ul>
 *   <li>Classes are prefixed with <strong>CLASS</strong> and enclosed in square brackets.</li>
 *   <li>Methods are prefixed with <strong>METHOD</strong> and enclosed in square brackets.</li>
 *   <li>Parameters are listed with their index and value.</li>
 * </ul>
 *
 * <p>Examples of the serialized structure:
 * <pre>
 * CLASS [MyClass] {
 *     METHOD [myMethod] {
 *         0: param1;
 *         1: param2;
 *     };
 * };
 * </pre>
 * </p>
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class ParameterDeserializer extends AbstractMappingDeserializer {

    /**
     * Converts the parameter mappings in {@link Mappings} into a structured string representation.
     *
     * @param mappings The {@link Mappings} object containing mapping data.
     * @return A structured string representation of the parameter mappings.
     */
    @Override
    public String deserializeToString(@NotNull Mappings mappings) {
        return makeOutput(mappings.getParameterMap());
    }

    /**
     * Serializes parameter mappings from the {@link Mappings} object into a single `.parameter.mmap` file.
     *
     * <p>If the file already exists, it will be overwritten.</p>
     *
     * <p>File extension validation is enforced, requiring `.parameter.mmap`. If the extension is invalid or
     * the operation fails for any reason, the process will return {@code false} and a {@link FailedState} will
     * be triggered.</p>
     *
     * @param mappings The {@link Mappings} object containing mapping data.
     * @param file     The file to write the serialized data into.
     * @return {@code true} if serialization is successful; {@code false} otherwise.
     */
    @Override
    public boolean deserializeToFile(@NotNull Mappings mappings, @NotNull File file) {
        Objects.requireNonNull(file);
        Objects.requireNonNull(mappings);

        if (!file.getName().endsWith(".parameter.mmap")) {
            this.fail(FailedState.of("File has to end with `.parameter.mmap` for it to qualify!", this.getClass()));
            return false;
        }

        if (file.exists()) {
            file.delete();
        }

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(makeOutput(mappings.getParameterMap()));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            this.fail(FailedState.of(e, this.getClass()));
            return false;
        }

        return true;
    }

    /**
     * Denies support for serializing parameter mappings into multiple files.
     *
     * <p>This method is deliberately unsupported, triggering a failure state when invoked.</p>
     *
     * @param mappings  The {@link Mappings} object.
     * @param locations An array of file objects to write into.
     * @return Always returns {@code false}.
     */
    @Override
    public boolean deserializeToFiles(@NotNull Mappings mappings, @NotNull File[] locations) {
        this.fail(FailedState.of("Parameter Deserializer does not support Multiple Files!", this.getClass()));
        return false;
    }

    /**
     * Generates a string representation of the parameter mappings.
     *
     * @param parameterMap The {@link ParameterMap} object to be serialized.
     * @return A structured string representation of parameter mappings.
     */
    private String makeOutput(ParameterMap parameterMap) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Map<String, CopyOnWriteArrayList<String>>> entry : parameterMap.getParameters().entrySet()) {
            String className = entry.getKey();
            builder.append("CLASS")
                    .append(" [")
                    .append(className)
                    .append("]")
                    .append(" {\n");

            for (Map.Entry<String, CopyOnWriteArrayList<String>> methodEntry : entry.getValue().entrySet()) {
                String methodName = methodEntry.getKey();
                builder.append("\t")
                        .append("METHOD")
                        .append(" [")
                        .append(methodName)
                        .append("]")
                        .append(" {\n");

                CopyOnWriteArrayList<String> values = methodEntry.getValue();

                for (int i = 0; i < values.size(); i++) {
                    builder.append("\t\t")
                            .append(i)
                            .append(": ")
                            .append(values.get(i))
                            .append(";\n");
                }
                builder.append("\t};\n");
            }

            builder.append("};\n");
        }

        return builder.toString();
    }

}