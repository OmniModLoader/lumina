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
import org.omnimc.lumina.consumer.AcceptConsumer;

import java.io.File;

/**
 * An abstract base class for mapping deserializers that provides a foundation for implementing mapping deserialization logic.
 *
 * <p>This class extends {@link AcceptConsumer}, allowing deserialization implementations to leverage consumer-based
 * error handling. The class implements {@link MappingDeserializer} and serves as a base for defining:
 * <ul>
 *   <li>Deserialization to a string representation.</li>
 *   <li>Deserialization to a single file.</li>
 *   <li>Deserialization to multiple files.</li>
 * </ul>
 * Subclasses are required to provide specific implementations for these methods.</p>
 *
 * @see MappingDeserializer
 * @see AcceptConsumer
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public abstract class AbstractMappingDeserializer extends AcceptConsumer implements MappingDeserializer {

    /**
     * Serializes mappings to a string representation.
     *
     * @param mappings The {@link Mappings} object containing classes, methods, and field mappings.
     * @return A string representation of the mappings.
     */
    @Override
    public abstract String deserializeToString(@NotNull Mappings mappings);

    /**
     * Serializes mappings to a single file.
     *
     * @param mappings The {@link Mappings} object containing classes, methods, and field mappings.
     * @param file     The file where the deserialization output will be written.
     * @return {@code true} if successful, otherwise {@code false}.
     */
    @Override
    public abstract boolean deserializeToFile(@NotNull Mappings mappings, @NotNull File file);

    /**
     * Serializes mappings to multiple files.
     *
     * @param mappings  The {@link Mappings} object containing classes, methods, and field mappings.
     * @param locations An array of files representing output locations for serialized data.
     * @return {@code true} if successful, otherwise {@code false}.
     */
    @Override
    public abstract boolean deserializeToFiles(@NotNull Mappings mappings, @NotNull File[] locations);

}