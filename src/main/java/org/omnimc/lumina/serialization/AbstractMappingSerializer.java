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
import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.consumer.AcceptConsumer;
import org.omnimc.lumina.consumer.FailedState;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Abstract base class for mapping serializers, providing common logic for
 * serializing mappings from various input sources (files, URIs, etc.).
 *
 * <p>This class extends {@link AcceptConsumer} to manage serialization errors and failures,
 * and implements the {@link MappingSerializer} interface to standardize the serialization process.</p>
 *
 * <p>Supports single-file and multi-file reading through the {@link LineSerializer} parser, with
 * an abstract method {@code serialize} for customizing the serialization implementation.</p>
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public abstract class AbstractMappingSerializer extends AcceptConsumer implements MappingSerializer {

    /**
     * A {@link Mappings} object to hold the parsed mappings.
     */
    protected final Mappings mappings = Mappings.of();

    /**
     * The {@link LineSerializer} used to parse each line of the file or input source.
     */
    protected final LineSerializer chosenSerializer;

    /**
     * Constructs an instance with the specified {@link LineSerializer}.
     *
     * @param chosenSerializer The serializer used to parse input lines.
     */
    protected AbstractMappingSerializer(LineSerializer chosenSerializer) {
        this.chosenSerializer = chosenSerializer;
    }

    /**
     * Serializes mappings from the given {@link URI} using a specified {@link LineSerializer}.
     *
     * @param uri    The input source {@link URI}.
     * @param parser The line parser to process the input.
     * @return A {@link Mappings} object containing serialized mappings.
     */
    @Override
    public abstract Mappings serialize(@NotNull URI uri, @NotNull LineSerializer parser);

    /**
     * Serializes mappings from the given {@link URI} using the default {@link LineSerializer}.
     *
     * @param uri The input source {@link URI}.
     * @return A {@link Mappings} object containing serialized mappings.
     */
    @Override
    public Mappings serialize(@NotNull URI uri) {
        return serialize(uri, chosenSerializer);
    }

    /**
     * Reads a single input file from the specified {@link URI} and parses it using the given {@link LineSerializer}.
     *
     * @param uri    The location of the input file.
     * @param parser The line parser to process each line.
     * @return A {@link Mappings} object containing the parsed mappings, or {@link #mappings} if parsing fails.
     */
    protected Mappings singleFileRead(@NotNull URI uri, @NotNull LineSerializer parser) {
        Objects.requireNonNull(uri, "URI cannot be NULL.");
        Objects.requireNonNull(parser, "LineSerializer cannot be NULL.");

        int lineNumber;

        try {
            InputStream inputStream;

            if ("file".equalsIgnoreCase(uri.getScheme())) {
                inputStream = Files.newInputStream(Path.of(uri));
            } else if ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) {
                URL url = uri.toURL();
                inputStream = url.openStream();
            } else {
                this.fail(FailedState.of("Unsupported URI scheme: " + uri.getScheme(), this.getClass()));
                return null;
            }

            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                 LineNumberReader reader = new LineNumberReader(new InputStreamReader(bufferedInputStream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    lineNumber = reader.getLineNumber();

                    if (!parser.serializeLine(line, mappings, getConsumer())) {
                        this.fail(FailedState.of("Failed to parse line: " + lineNumber, this.getClass()));
                    }
                }
            }

        } catch (IOException e) {
            this.fail(FailedState.of(e, this.getClass()));
            return null;
        }

        return mappings;
    }

    /**
     * Reads multiple input files from the specified array of {@link URI}s, parsing each using the given {@link LineSerializer}.
     *
     * @param uris   An array of input {@link URI}s to parse.
     * @param parser The line parser to process each file.
     * @return A {@link Mappings} object containing the combined mappings from all input files.
     */
    protected Mappings multiFileRead(@NotNull URI[] uris, @NotNull LineSerializer parser) {
        Objects.requireNonNull(uris, "uris cannot be NULL.");
        Objects.requireNonNull(parser, "Parser cannot be NULL.");

        for (@NotNull URI uri : uris) {
            singleFileRead(uri, parser);
        }

        return mappings;
    }
}