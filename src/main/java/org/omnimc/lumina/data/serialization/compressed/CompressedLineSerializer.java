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

package org.omnimc.lumina.data.serialization.compressed;

import org.jetbrains.annotations.Nullable;
import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.types.ClassData;
import org.omnimc.lumina.data.serialization.LineSerializer;

/**
 * A {@link LineSerializer} implementation for processing compressed mapping lines.
 *
 * <p>This serializer processes each line in a mapping file, extracting and mapping the following types:</p>
 * <ul>
 *   <li>c: Maps obfuscated class names to de-obfuscated names.</li>
 *   <li>f: Maps obfuscated field names to de-obfuscated names, tied to a parent class.</li>
 *   <li>m: Maps obfuscated method names to de-obfuscated names, tied to a parent class.</li>
 * </ul>
 *
 * <p>Lines must follow the format: {@code TYPE OBFUSCATED_NAME:DEOBFUSCATED_NAME}.</p>
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @see LineSerializer
 * @since 1.0.0
 */
public class CompressedLineSerializer implements LineSerializer {

    /**
     * Retrieves an instance of {@link CompressedLineSerializer}.
     *
     * @return A new instance of {@link CompressedLineSerializer}.
     */
    public static LineSerializer getInstance() {
        return new CompressedLineSerializer();
    }

    private String parentClass;

    private ClassData classData;

    /**
     * {@inheritDoc}
     *
     * @param line     The line that contains the data you wish to parse.
     * @param mappings The {@link Mappings} you wish to populate with the data provided.
     * @return {@code true} meaning it passed, while {@code false} means you failed.
     */
    @Override
    public boolean serialize(String line, Mappings mappings) {
        if (line == null || line.isBlank()) {
            return false;
        }

        try {
            int i = line.indexOf(" ");
            if (i == -1) {
                throw new UnsupportedOperationException("Invalid line format: missing type and value");
            }

            String type = line.substring(0, i).trim();
            String value = line.substring(i + 1).trim();

            String[] split = value.split(":");
            if (split.length != 2) {
                throw new UnsupportedOperationException("Invalid value format: " + value);
            }

            String obfuscatedName = split[0].trim();
            String unObfuscatedName = split[1].trim();

            switch (type.toLowerCase()) {
                case "c":
                    parentClass = obfuscatedName;
                    classData = mappings.addClass(obfuscatedName, unObfuscatedName);
                    break;
                case "f":
                    if (parentClass == null) {
                        throw new RuntimeException("FIELD entry without a parent class: " + line);
                    }
                    // Descriptor is empty because it's included in the obfuscatedName;
                    classData.addField(obfuscatedName, unObfuscatedName, "");
                    break;
                case "m":
                    if (parentClass == null) {
                        throw new RuntimeException("METHOD entry without a parent class: " + line);
                    }
                    // Descriptor is empty because it's included in the obfuscatedName;
                    classData.addMethod(obfuscatedName, unObfuscatedName, "");
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported type: " + type);
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO add an actual log message.
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param line        The line that contains the field you wish to parse.
     * @param classData   The {@link ClassData} you wish to populate with the data provided.
     * @return {@code true} meaning it passed, while {@code false} means you failed.
     */
    @Override
    public boolean serializeFields(String line, ClassData classData) {
        Result result = getResult(line, "f");
        if (result == null) return true;

        classData.addField(result.obfuscatedName, result.unObfuscatedName, "");
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param line        The line that contains the method you wish to parse.
     * @param classData   The {@link ClassData} you wish to populate with the data provided.
     * @return {@code true} meaning it passed, while {@code false} means you failed.
     */
    @Override
    public boolean serializeMethods(String line, ClassData classData) {
        Result result = getResult(line, "m");
        if (result == null) {
            return true;
        }

        classData.addMethod(result.obfuscatedName, result.unObfuscatedName, "");
        return true;
    }

    @Nullable
    private Result getResult(String line, String typeIndicator) {
        if (line == null || line.isBlank()) {
            return null;
        }

        int i = line.indexOf(" ");
        if (i == -1) {
            throw new UnsupportedOperationException("Invalid line format: missing type and value");
        }

        String type = line.substring(0, i).trim();
        String value = line.substring(i + 1).trim();

        if (!type.equalsIgnoreCase(typeIndicator)) {
            return null;
        }

        String[] split = value.split(":");
        if (split.length != 2) {
            throw new UnsupportedOperationException("Invalid value format: " + value);
        }

        String obfuscatedName = split[0].trim();
        String unObfuscatedName = split[1].trim();
        return new Result(obfuscatedName, unObfuscatedName);
    }

    private record Result(String obfuscatedName, String unObfuscatedName) {}
}