package org.omnimc.lumina.data.deserialization.compressed;

import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.deserialization.BatchDeserializer;
import org.omnimc.lumina.data.types.ClassData;
import org.omnimc.lumina.data.types.FieldData;
import org.omnimc.lumina.data.types.MethodData;
import org.omnimc.lumina.mcmap.McMap;

import java.io.File;
import java.util.Map;

/**
 * This is the "Compressed" Deserializer for Lumina.
 * <p>
 * The format is this:
 * <pre><code>
 * // method example.
 * m obfuscatedName()V:unobfuscatedName
 * // field example (Z in a boolean.)
 * f obfuscatedNameZ:unobfuscatedName
 * </code></pre>
 *
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class CompressedDeserializer implements BatchDeserializer {

    private static final String METHOD_PREFIX = "m";
    private static final String FIELD_PREFIX = "f";

    public static CompressedDeserializer getInstance() {
        return new CompressedDeserializer();
    }

    /**
     * {@inheritDoc}
     *
     * @param saveLocation The location you want the {@link McMap} to be saved to.
     * @param mappings     The {@link Mappings} you want to populate the {@link McMap} with.
     * @param version      The {@link McMap.Version} you want the {@link McMap} to be.
     * @return An instance of {@link McMap}, populated with the provided {@link Mappings}.
     */
    @Override
    public McMap deserialize(File saveLocation, Mappings mappings, McMap.Version version) {
        return McMap.create(mappings, saveLocation, version);
    }

    /**
     * {@inheritDoc}
     *
     * @param classData The {@link ClassData} you want turn into a {@link String}
     * @return A {@link String} with all {@link ClassData} information.
     */
    @Override
    public String deserializeClassData(ClassData classData) {
        return deserializeFieldData(classData.getFields()) +
                deserializeFieldData(classData.getPrivateFields()) +
                deserializeMethodData(classData.getMethods()) +
                deserializeMethodData(classData.getPrivateMethods()) + "\n";
    }

    /**
     * {@inheritDoc}
     *
     * @param methods The {@link Map} with (obfuscatedName, {@link MethodData}) in that format.
     * @return A {@link String} with all {@link MethodData} information.
     */
    @Override
    public String deserializeMethodData(Map<String, MethodData> methods) {
        if (methods.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, MethodData> entry : methods.entrySet()) {
            builder.append(METHOD_PREFIX)
                    .append(" ")
                    .append(entry.getKey())
                    .append(":")
                    .append(entry.getValue().getMethodName())
                    .append("\n");
        }

        return builder.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @param fields The {@link Map} with (obfuscatedName, {@link FieldData}) in that format.
     * @return A {@link String} with all {@link FieldData} information.
     */
    @Override
    public String deserializeFieldData(Map<String, FieldData> fields) {
        if (fields.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, FieldData> entry : fields.entrySet()) {
            builder.append(FIELD_PREFIX)
                    .append(" ")
                    .append(entry.getKey())
                    .append(":")
                    .append(entry.getValue().getFieldName())
                    .append("\n");
        }

        return builder.toString();
    }
}