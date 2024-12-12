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

package org.omnimc.lumina.serialization;

import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.consumer.FailedState;

import java.util.function.Consumer;

/**
 * Represents a functional interface for parsing and serializing individual lines of input.
 *
 * <p>Each implementation defines the logic for parsing a line, managing failures through
 * the provided {@link Consumer} and updating the {@link Mappings} object.</p>
 *
 * <p>A helper method {@link #getEmptySerializer()} provides a no-op implementation that
 * always returns {@code false} for parsing.</p>
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public interface LineSerializer {

    /**
     * Provides a no-op implementation of {@link LineSerializer} that always fails.
     *
     * @return A {@link LineSerializer} instance that does nothing and fails for every line.
     */
    static LineSerializer getEmptySerializer() {
        return (line, mappings, consumer) -> false;
    }

    /**
     * Parses and serializes a single line of input and updates the given {@link Mappings} object.
     *
     * @param line     The input line to parse.
     * @param mappings The {@link Mappings} object to update with parsed data.
     * @param consumer A {@link Consumer} to handle failures during serialization.
     * @return {@code true} if the line was successfully serialized, or {@code false} otherwise.
     */
    boolean serializeLine(String line, Mappings mappings, Consumer<FailedState> consumer);

}