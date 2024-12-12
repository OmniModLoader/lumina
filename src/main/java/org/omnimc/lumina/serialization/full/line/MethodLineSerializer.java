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

package org.omnimc.lumina.serialization.full.line;

import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.consumer.FailedState;
import org.omnimc.lumina.serialization.LineSerializer;

import java.util.function.Consumer;

/**
 * A {@link LineSerializer} implementation that serializes method mappings from textual entries.
 *
 * <p>This serializer processes lines in the following formats:</p>
 * <ul>
 *     <li><b>Parent Class Definition:</b> Ends with a colon (`:`). Declares the parent class for subsequent entries.</li>
 *     <li><b>Method Entries:</b> Follow the format `obfuscated:unobfuscated`, defining a mapping for a method
 *     under the currently defined parent class.</li>
 * </ul>
 *
 * <p>If no parent class is defined prior to parsing a method entry, or if the format is invalid, the operation fails
 * with a {@link FailedState}.</p>
 *
 * @see LineSerializer
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class MethodLineSerializer implements LineSerializer {

    /**
     * Stores the currently defined parent class for method entries.
     */
    private String parentClass;

    /**
     * Parses and serializes a method mapping line into the {@link Mappings} object.
     *
     * <p>The parsing operates as follows:</p>
     * <ul>
     *   <li>If the line ends with a colon (`:`), it sets the parent class for subsequent method entries.</li>
     *   <li>If the line is a method mapping (`obfuscated:unobfuscated`), it associates the mapping with the current parent class.</li>
     *   <li>If the format is invalid or no parent class is defined, the {@link Consumer} handles a failure.</li>
     * </ul>
     *
     * @param line     The line to serialize.
     * @param mappings The {@link Mappings} object to store the serialized method mappings.
     * @param consumer A {@link Consumer} to handle failures during serialization.
     * @return {@code true} if the line was successfully serialized; otherwise, {@code false}.
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    public boolean serializeLine(String line, Mappings mappings, Consumer<FailedState> consumer) {
        if (line == null || line.isBlank()) {
            consumer.accept(FailedState.of("Line cannot be null or empty!", this.getClass()));
            return false;
        }

        if (line.endsWith(":")) {
            parentClass = line.substring(0, line.length() - 1).trim();
            if (parentClass.isEmpty()) {
                consumer.accept(FailedState.of("Parent class name cannot be empty!", this.getClass()));
                return false;
            }
            return true;
        }

        if (parentClass == null) {
            consumer.accept(FailedState.of("No parent class defined for line: " + line, this.getClass()));
            return false;
        }

        String[] split = line.split(":");
        if (split.length != 2) {
            consumer.accept(FailedState.of("Invalid line format, expected 'obfuscated:unobfuscated': " + line, this.getClass()));
            return false;
        }

        String obfuscatedName = split[0].trim();
        String unobfuscatedName = split[1].trim();

        if (obfuscatedName.isEmpty() || unobfuscatedName.isEmpty()) {
            consumer.accept(FailedState.of("Obfuscated or unobfuscated name cannot be empty: " + line, this.getClass()));
            return false;
        }

        mappings.addMethod(parentClass, obfuscatedName, unobfuscatedName);
        return true;
    }
}