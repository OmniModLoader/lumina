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

import org.jetbrains.annotations.NotNull;
import org.omnimc.lumina.MappingType;
import org.omnimc.lumina.Mappings;

import java.net.URI;

/**
 * Represents a contract for classes responsible for serializing mappings from various input sources.
 *
 * <p>Implementations must provide methods to serialize mappings based on a given {@link URI},
 * and optionally a {@link LineSerializer} or a {@link MappingType}.</p>
 *
 * <p>This interface abstracts the process of reading and converting obfuscated mappings into
 * meaningful names using serializers.</p>
 *
 * @see LineSerializer
 * @see AbstractMappingSerializer
 * @see Mappings
 * @see MappingType
 * @see URI
 *
 * @since 1.0.0
 */
public interface MappingSerializer {

    /**
     * Serializes mappings from the given {@link URI} and {@link MappingType}.
     *
     * <p>The provided {@link MappingType} determines the type of {@link LineSerializer} to use.
     *
     * @param uri  The input source {@link URI}.
     * @param type The {@link MappingType} indicating the type of serialization to perform.
     * @return A {@link Mappings} object containing the serialized mappings.
     */
    default Mappings serialize(@NotNull URI uri, MappingType type) {
        return serialize(uri, type.getLineSerializer());
    }

    /**
     * Serializes mappings from the given {@link URI} using the specified {@link LineSerializer}.
     *
     * @param uri    The input source {@link URI}.
     * @param parser The {@link LineSerializer} used to parse the input.
     * @return A {@link Mappings} object containing the serialized mappings.
     */
    Mappings serialize(@NotNull URI uri, @NotNull LineSerializer parser);

    /**
     * Serializes mappings from the given {@link URI} using the default serializer.
     *
     * @param uri The input source {@link URI}.
     * @return A {@link Mappings} object containing the serialized mappings.
     */
    Mappings serialize(@NotNull URI uri);

}