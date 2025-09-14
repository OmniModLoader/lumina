package org.omnimc.lumina.util;

import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.serialization.LineSerializer;
import org.omnimc.lumina.data.types.ClassData;

import java.io.*;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
 */
public final class SerializationHelper {

    private SerializationHelper() {
        throw new UnsupportedOperationException("SerializationHelper cannot be instantiated");
    }

    public static ClassData populateClassData(InputStream stream, LineSerializer lineSerializer, ClassData data) {
        getMappingsFromInputStream(stream, null,
                                   ((line, mappings) -> {
                                       if (!lineSerializer.serializeMethods(line, data)) {
                                           return lineSerializer.serializeFields(line, data);
                                       }
                                       return true;
                                   }));
        return data;
    }

    public static ClassData populateClassDataField(InputStream stream, LineSerializer serializer, ClassData data) {
        getMappingsFromInputStream(stream, null,
                                   (s, mappings1) -> serializer.serializeFields(s, data));
        return data;
    }

    public static ClassData populateClassDataMethod(InputStream stream, LineSerializer serializer, ClassData data) {
        getMappingsFromInputStream(stream, null,
                                   (s, mappings1) -> serializer.serializeMethods(s, data));
        return data;
    }

    public static Mappings getMappingsFromInputStream(InputStream inputStream, LineSerializer serializer, Mappings mappings) {
        return getMappingsFromInputStream(inputStream, mappings, serializer::serialize);
    }

    private static Mappings getMappingsFromInputStream(InputStream inputStream, Mappings mappings, BiPredicate<String, Mappings> predicate) {
        Objects.requireNonNull(inputStream, "Supplier cannot be NULL.");
        int lineNumber;

        try {
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                 LineNumberReader reader = new LineNumberReader(new InputStreamReader(bufferedInputStream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    lineNumber = reader.getLineNumber();

                    if (!predicate.test(line, mappings)) {
                        System.out.println("Failed to parse line " + lineNumber + "." + line);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize with supplier.", e);
        }

        return mappings;
    }
}