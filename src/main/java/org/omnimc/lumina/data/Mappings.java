package org.omnimc.lumina.data;

import org.omnimc.lumina.data.deserialization.compressed.CompressedDeserializer;
import org.omnimc.lumina.data.types.ClassData;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class Mappings {

    private final Map<String, ClassData> classes;

    public Mappings() {
        this(new HashMap<>());
    }

    public Mappings(Mappings mappings) {
        this(mappings.getClasses());
    }

    public Mappings(Map<String, ClassData> classes) {
        this.classes = classes;
    }

    public Map<String, ClassData> getClasses() {
        return classes;
    }

    public ClassData getClass(String obfuscatedName) {
        return classes.get(obfuscatedName);
    }

    public ClassData getClassByValue(String unObfuscatedName) {
        return classes.get(getClassNameByValue(unObfuscatedName));
    }

    public String getClassName(String obfuscatedName) {
        ClassData classData = getClass(obfuscatedName);
        if (classData == null) {
            return obfuscatedName;
        }

        return classData.getClassName();
    }

    public String getClassNameByValue(String unObfuscatedName) {
        Optional<Map.Entry<String, ClassData>> optional = classes.entrySet()
                .stream()
                .filter(e -> e.getValue().getClassName().equals(unObfuscatedName))
                .findFirst();

        return optional.map(Map.Entry::getKey).orElse(unObfuscatedName);
    }

    public ClassData addClass(String obfuscatedName, String unObfuscatedName) {
        ClassData classData = getClass(obfuscatedName);
        if (classData != null) {
            return null;
        }

        return addClass(obfuscatedName, new ClassData(unObfuscatedName));
    }

    public ClassData addClass(String obfuscatedName, ClassData classData) {
        classes.put(obfuscatedName, classData);
        return classData;
    }

}