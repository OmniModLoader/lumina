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

package org.omnimc.lumina;

import org.omnimc.lumina.serialization.LineSerializer;
import org.omnimc.lumina.serialization.compressed.CompressedLineSerializer;

/**
 * Represents the different types of mappings supported and provides associated line serializers for each type.
 *
 * <p>The `MappingType` enum is used to define distinct mapping modes within the system.
 * Each mapping type is associated with a specific {@link LineSerializer}, which handles the serialization
 * and deserialization of that type of mapping.</p>
 *
 * <h3>Available Mapping Types:</h3>
 * <ul>
 *   <li><b>FULL:</b> Uses an empty serializer for handling full mappings.</li>
 *   <li><b>COMPRESSED:</b> Utilizes a {@link CompressedLineSerializer} for compressed mappings.</li>
 *   <li><b>PARAMETERS:</b> Uses an empty serializer for mapping parameters specifically.</li>
 *   <li><b>UNKNOWN:</b> Represents an undefined mapping type, with no serializer.</li>
 * </ul>
 *
 * @see LineSerializer
 * @see CompressedLineSerializer
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public enum MappingType {
    /**
     * Represents full mappings with no compression applied.
     *
     * <p>This type utilizes an empty implementation of {@link LineSerializer} for handling mappings.
     */
    FULL(LineSerializer.getEmptySerializer()),
    /**
     * Represents mappings that are serialized in a compressed format.
     *
     * <p>This type uses the {@link CompressedLineSerializer}, which is designed to manage compressed mapping data effectively.</p>
     */
    COMPRESSED(new CompressedLineSerializer()),
    /**
     * Represents mappings that are specific to method or class parameters.
     *
     * <p>Similar to {@code FULL}, this type also uses an empty implementation of {@link LineSerializer}.</p>
     */
    PARAMETERS(LineSerializer.getEmptySerializer()),
    /**
     * Represents an unknown or undefined mapping type.
     *
     * <p>No {@link LineSerializer} is provided for this type, and it is effectively treated as a placeholder.</p>
     */
    UNKNOWN(null);

    /**
     * The {@link LineSerializer} associated with the mapping type.
     */
    private final LineSerializer lineSerializer;

    /**
     * Constructs an instance of the mapping type with the provided {@link LineSerializer}.
     *
     * @param lineSerializer The serializer to be associated with the mapping type.
     */
    MappingType(LineSerializer lineSerializer) {
        this.lineSerializer = lineSerializer;
    }

    /**
     * Retrieves the {@link LineSerializer} associated with this mapping type.
     *
     * @return The associated {@link LineSerializer}, or {@code null} if not available.
     */
    public LineSerializer getLineSerializer() {
        return lineSerializer;
    }
}