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

package org.omnimc.lumina.serialization.full;

import org.jetbrains.annotations.NotNull;
import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.consumer.FailedState;
import org.omnimc.lumina.serialization.AbstractMappingSerializer;
import org.omnimc.lumina.serialization.LineSerializer;
import org.omnimc.lumina.serialization.full.line.ClassLineSerializer;
import org.omnimc.lumina.serialization.full.line.FieldLineSerializer;
import org.omnimc.lumina.serialization.full.line.MethodLineSerializer;

import java.net.URI;
import java.util.Objects;

/**
 * A serializer implementation for handling full mappings stored in structured directories or files.
 *
 * <p>This class extends {@link AbstractMappingSerializer} and processes mappings
 * from files with extensions such as `.class.mmap`, `.method.mmap`, and `.field.mmap`.
 * It also allows handling structured directories with pre-defined paths for mapping files.</p>
 *
 * <p>Line serialization is delegated to the following line serializers:</p>
 * <ul>
 *   <li>{@link ClassLineSerializer} for class mappings.</li>
 *   <li>{@link FieldLineSerializer} for field mappings.</li>
 *   <li>{@link MethodLineSerializer} for method mappings.</li>
 * </ul>
 *
 * @see AbstractMappingSerializer
 * @see FullPathResolver
 * @see ClassLineSerializer
 * @see FieldLineSerializer
 * @see MethodLineSerializer
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class FullSerializer extends AbstractMappingSerializer {

    /**
     * Resolves the locations of class, method, and field mapping files in a directory.
     */
    private final FullPathResolver resolver;

    /**
     * Constructs a {@code FullSerializer} instance with the specified {@link FullPathResolver}.
     *
     * @param resolver The resolver for locating mapping files within a directory.
     */
    public FullSerializer(@NotNull FullPathResolver resolver) {
        super(LineSerializer.getEmptySerializer());
        Objects.requireNonNull(resolver);
        this.resolver = resolver;
    }

    /**
     * Serializes mappings from the specified URI using the correct line serializer.
     *
     * <p>This method determines the type of file (class, method, field) based on the file extension
     * or processes a directory containing structured mapping files. It ensures that only the
     *      * {@link LineSerializer#getEmptySerializer()} is used as a parser for the operation.</p>
     *
     * @param uri    The input URI representing the directory or mapping file.
     * @param parser The line parser, which must be {@link LineSerializer#getEmptySerializer()}.
     * @return A {@link Mappings} object containing the serialized mappings or {@code null} if an error occurs.
     */
    @Override
    public Mappings serialize(@NotNull URI uri, @NotNull LineSerializer parser) {
        if ((parser != LineSerializer.getEmptySerializer())) {
            this.fail(FailedState.of("Cannot accept any parser except `LineSerializer.getEmptySerializer`!", this.getClass()));
            return null;
        }

        String uriFull = uri.toString();

        try {
            if (uriFull.endsWith("/")) {
                return readDirectoryMappings(uriFull);
            } else if (uriFull.endsWith(".class.mmap")) {
                return singleFileRead(uri, new ClassLineSerializer());
            } else if (uriFull.endsWith(".method.mmap")) {
                return singleFileRead(uri, new MethodLineSerializer());
            } else if (uriFull.endsWith(".field.mmap")) {
                return singleFileRead(uri, new FieldLineSerializer());
            } else {
                this.fail(FailedState.of("Unsupported file format: " + uriFull, this.getClass()));
                return null;
            }
        } catch (IllegalArgumentException e) {
            this.fail(FailedState.of("Invalid URI syntax: " + uriFull, this.getClass()));
            return null;
        } catch (Exception e) {
            this.fail(FailedState.of("Unexpected error while reading mappings: " + e.getMessage(), this.getClass()));
            return null;
        }
    }

    /**
     * Reads mappings from a structured directory defined by the {@link FullPathResolver}.
     *
     * <p>The directory must contain files at the paths specified by the resolver:
     * {@code classLocation}, {@code methodLocation}, and {@code fieldLocation}.</p>
     *
     * @param uriFull A string representation of the URI pointing to the directory.
     * @return A {@link Mappings} object containing the combined serialized mappings from all files.
     */
    private Mappings readDirectoryMappings(String uriFull) {
        try {
            String classLocation = uriFull + resolver.classLocation();
            String methodLocation = uriFull + resolver.methodLocation();
            String fieldLocation = uriFull + resolver.fieldLocation();

            URI classURI = URI.create(classLocation);
            mappings.addAll(singleFileRead(classURI, new ClassLineSerializer()));

            URI methodURI = URI.create(methodLocation);
            mappings.addAll(singleFileRead(methodURI, new MethodLineSerializer()));

            URI fieldURI = URI.create(fieldLocation);
            mappings.addAll(singleFileRead(fieldURI, new FieldLineSerializer()));
        } catch (IllegalArgumentException e) {
            this.fail(FailedState.of("Invalid directory structure or URI syntax: " + uriFull, this.getClass()));
        } catch (Exception e) {
            this.fail(FailedState.of("Unexpected error while reading directory mappings: " + e.getMessage(), this.getClass()));
        }

        return mappings;
    }
}