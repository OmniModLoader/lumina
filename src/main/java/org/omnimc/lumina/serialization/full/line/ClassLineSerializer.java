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
import java.util.regex.PatternSyntaxException;

/**
 * A {@link LineSerializer} implementation that serializes class mappings from textual entries.
 *
 * <p>This serializer processes each line in the format `obfuscated:unobfuscated`,
 * where:</p>
 * <ul>
 *     <li><b>obfuscated:</b> The obfuscated class name.</li>
 *     <li><b>unobfuscated:</b> The corresponding de-obfuscated class name.</li>
 * </ul>
 *
 * <p>If the format is invalid or any parsing error occurs, the operation fails with a {@link FailedState}.</p>
 *
 * @see LineSerializer
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class ClassLineSerializer implements LineSerializer {

    /**
     * Parses and serializes a class mapping line into the {@link Mappings} object.
     *
     * @param line     The line to serialize, expected in the format `obfuscated:unobfuscated`.
     * @param mappings The {@link Mappings} object to store the serialized class mappings.
     * @param consumer A {@link Consumer} to handle failures during serialization.
     * @return {@code true} if the line was successfully serialized; otherwise, {@code false}.
     */
    @Override
    public boolean serializeLine(String line, Mappings mappings, Consumer<FailedState> consumer) {
        try {
            String[] split = line.split(":");
            if (split.length != 2) {
                consumer.accept(FailedState.of("Invalid value format: " + line, this.getClass()));
                return false;
            }

            String obfuscatedName = split[0].trim();
            String unObfuscatedName = split[1].trim();

            mappings.addClass(obfuscatedName, unObfuscatedName);
        } catch (PatternSyntaxException e) {
            consumer.accept(FailedState.of(e, this.getClass()));
            return false;
        }

        return true;
    }
}