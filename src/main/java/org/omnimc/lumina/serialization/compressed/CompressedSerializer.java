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

import org.jetbrains.annotations.NotNull;
import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.consumer.FailedState;
import org.omnimc.lumina.serialization.AbstractMappingSerializer;
import org.omnimc.lumina.serialization.LineSerializer;

import java.net.URI;

/**
 * A serializer implementation for handling compressed mapping files.
 *
 * <p>This class extends {@link AbstractMappingSerializer} and processes files
 * with a `.mmap` extension using the {@link CompressedLineSerializer}. It ensures
 * that only valid compressed mapping parsers are used during serialization.</p>
 *
 * <p><b>Usage:</b> Compressed mapping files should adhere to a specific format
 * containing information about classes, methods, and fields in a compact representation.</p>
 *
 * @see AbstractMappingSerializer
 * @see CompressedLineSerializer
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class CompressedSerializer extends AbstractMappingSerializer {

    /**
     * Constructs a {@code CompressedSerializer} with the {@link CompressedLineSerializer} as its default parser.
     */
    protected CompressedSerializer() {
        super(CompressedLineSerializer.getInstance());
    }

    /**
     * Serializes mappings from a given {@link URI} using {@link CompressedLineSerializer}.
     *
     * <p>This method validates that the provided parser is an instance of {@link CompressedLineSerializer}.
     * Additionally, it ensures that the mapping file has a `.mmap` extension, failing the operation otherwise.</p>
     *
     * @param uri    The input URI representing the mapping file.
     * @param parser The parser, which must be a {@link CompressedLineSerializer}.
     * @return A {@link Mappings} object containing the serialized mappings, or {@code null} if parsing fails.
     */
    @Override
    public Mappings serialize(@NotNull URI uri, @NotNull LineSerializer parser) {
        if (!(parser instanceof CompressedLineSerializer)) {
            this.fail(FailedState.of("You cannot use non Compressed Parsers", this.getClass()));
            return null;
        }

        String uriName = uri.toString();
        if (!uriName.endsWith(".mmap")) {
            this.fail(FailedState.of("URI does not end with .mmap", this.getClass()));
            return null;
        }

        return super.singleFileRead(uri, parser);
    }
}