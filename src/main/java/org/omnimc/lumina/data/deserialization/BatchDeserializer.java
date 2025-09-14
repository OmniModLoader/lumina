package org.omnimc.lumina.data.deserialization;

import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.types.ClassData;
import org.omnimc.lumina.data.types.FieldData;
import org.omnimc.lumina.data.types.MethodData;
import org.omnimc.lumina.mcmap.McMap;

import java.io.File;
import java.util.Map;

/**
 * When implementing this class you must insure all methods work as intended and described in the javadocs or
 * the program might not function as intended.
 * <p>
 * This class acts as the main Deserializer for Lumina
 *
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public interface BatchDeserializer {

    /**
     * This method only deserializes {@link Mappings} to a {@link McMap} by using {@link McMap#create(Mappings, File, McMap.Version)}.
     *
     * @param saveLocation The location you want the {@link McMap} to be saved to.
     * @param mappings     The {@link Mappings} you want to populate the {@link McMap} with.
     * @param version      The {@link McMap.Version} you want the {@link McMap} to be.
     * @return An instance of {@link McMap}, populated with the provided {@link Mappings}.
     * @throws Exception Due to {@link McMap#create(Mappings, File, McMap.Version)}
     */
    McMap deserialize(File saveLocation, Mappings mappings, McMap.Version version) throws Exception;

    /**
     * This method deserializes the entire {@link ClassData} to a {@link String}.
     *
     * @param classData The {@link ClassData} you want turn into a {@link String}
     * @return A {@link String} with all {@link ClassData} information.
     */
    String deserializeClassData(ClassData classData);

    /**
     * This deserializes only {@link MethodData} hashmaps, like they are seen in {@link ClassData#getMethods()}.
     *
     * @param methods The {@link Map} with (obfuscatedName, {@link MethodData}) in that format.
     * @return A {@link String} with all {@link MethodData} information.
     */
    String deserializeMethodData(Map<String, MethodData> methods);

    /**
     * This deserializes only {@link FieldData} hashmaps, like they are seen in {@link ClassData#getFields()}}.
     *
     * @param fields The {@link Map} with (obfuscatedName, {@link FieldData}) in that format.
     * @return A {@link String} with all {@link FieldData} information.
     */
    String deserializeFieldData(Map<String, FieldData> fields);

}