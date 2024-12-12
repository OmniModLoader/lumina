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

package org.omnimc.lumina.deserialization;

import org.jetbrains.annotations.NotNull;
import org.omnimc.lumina.Mappings;

import java.io.File;

/**
 * A contract for converting {@link Mappings} objects into various deserialized outputs.
 *
 * <p>This interface defines methods for serializing mappings into different formats,
 * including:
 * <ul>
 *   <li>String-based representation.</li>
 *   <li>File-based representation for single files.</li>
 *   <li>File-based representation across multiple files.</li>
 * </ul>
 * Implementing classes are expected to define specific serialization logic for these operations.</p>
 *
 * @see Mappings
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public interface MappingDeserializer {

    /**
     * Serializes the provided {@link Mappings} into a string representation.
     *
     * @param mappings The mappings to be serialized.
     * @return A string representation of the mappings.
     */
    String deserializeToString(@NotNull Mappings mappings);

    /**
     * Serializes the provided {@link Mappings} into a single file.
     *
     * @param mappings The mappings to be serialized.
     * @param file     The file where the serialized result will be written.
     * @return {@code true} if the operation is successful, otherwise {@code false}.
     */
    boolean deserializeToFile(@NotNull Mappings mappings, @NotNull File file);

    /**
     * Serializes the provided {@link Mappings} into multiple files.
     *
     * @param mappings  The mappings to be serialized.
     * @param locations An array of files to distribute mapping data across.
     * @return {@code true} if the operation is successful, otherwise {@code false}.
     */
    boolean deserializeToFiles(@NotNull Mappings mappings, @NotNull File[] locations);
}