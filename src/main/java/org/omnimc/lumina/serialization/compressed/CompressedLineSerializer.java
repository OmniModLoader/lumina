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

package org.omnimc.lumina.serialization.compressed;

import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.consumer.FailedState;
import org.omnimc.lumina.serialization.LineSerializer;

import java.util.function.Consumer;

/**
 * A {@link LineSerializer} implementation for processing compressed mapping lines.
 *
 * <p>This serializer processes each line in a mapping file, extracting and mapping the following types:</p>
 * <ul>
 *   <li>CLASS: Maps obfuscated class names to de-obfuscated names.</li>
 *   <li>FIELD: Maps obfuscated field names to de-obfuscated names, tied to a parent class.</li>
 *   <li>METHOD: Maps obfuscated method names to de-obfuscated names, tied to a parent class.</li>
 * </ul>
 *
 * <p>Lines must follow the format: {@code TYPE OBFUSCATED_NAME:DEOBFUSCATED_NAME}.</p>
 *
 * @see LineSerializer
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class CompressedLineSerializer implements LineSerializer {

    /**
     * Retrieves an instance of {@code CompressedLineSerializer}.
     *
     * @return A new instance of {@code CompressedLineSerializer}.
     */
    public static LineSerializer getInstance() {
        return new CompressedLineSerializer();
    }

    /**
     * Stores the parent class being processed to associate fields and methods with the correct class.
     */
    private String parentClass;

    /**
     * Parses and serializes a compressed mapping line, updating the given {@link Mappings}.
     *
     * @param line     The line content to parse.
     * @param mappings The {@link Mappings} instance where parsed data will be stored.
     * @param consumer A {@link Consumer} to handle failures during processing.
     * @return {@code true} if the line was successfully parsed, {@code false} otherwise.
     */
    @Override
    public boolean serializeLine(String line, Mappings mappings, Consumer<FailedState> consumer) {
        if (line == null || line.isBlank()) {
            return false;
        }

        try {
            int i = line.indexOf(" ");
            if (i == -1) {
                consumer.accept(FailedState.of("Invalid line format: missing type and value", this.getClass()));
                return false;
            }

            String type = line.substring(0, i).trim();
            String value = line.substring(i + 1).trim();

            String[] split = value.split(":");
            if (split.length != 2) {
                consumer.accept(FailedState.of("Invalid value format: " + value, this.getClass()));
                return false;
            }

            String obfuscatedName = split[0].trim();
            String unObfuscatedName = split[1].trim();

            switch (type.toUpperCase()) {
                case "CLASS":
                    parentClass = obfuscatedName;
                    mappings.addClass(obfuscatedName, unObfuscatedName);
                    break;
                case "FIELD":
                    if (parentClass == null) {
                        consumer.accept(FailedState.of("FIELD entry without a parent class: " + line, this.getClass()));
                        return false;
                    }
                    mappings.addField(parentClass, obfuscatedName, unObfuscatedName);
                    break;
                case "METHOD":
                    if (parentClass == null) {
                        consumer.accept(FailedState.of("METHOD entry without a parent class: " + line, this.getClass()));
                        return false;
                    }
                    mappings.addMethod(parentClass, obfuscatedName, unObfuscatedName);
                    break;
                default:
                    consumer.accept(FailedState.of("Unknown type: " + type, this.getClass()));
                    return false;
            }

            return true;
        } catch (Exception e) {
            consumer.accept(FailedState.of(e, this.getClass()));
            return false;
        }
    }
}