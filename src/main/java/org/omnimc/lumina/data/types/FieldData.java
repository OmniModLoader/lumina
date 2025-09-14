package org.omnimc.lumina.data.types;

import java.util.Objects;

/**
 * An {@link FieldData} is always linked to a {@link ClassData}.
 *
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public final class FieldData {

    private final String obfuscatedName;
    private final String fieldName;
    private final String descriptor;

    public FieldData(String obfuscatedName,
                     String fieldName, String descriptor) {
        this.obfuscatedName = obfuscatedName;
        this.fieldName = fieldName;
        this.descriptor = descriptor;
    }

    public String getObfuscatedName() {
        return obfuscatedName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        FieldData fieldData = (FieldData) object;
        return Objects.equals(obfuscatedName, fieldData.obfuscatedName)
                && Objects.equals(fieldName, fieldData.fieldName)
                && Objects.equals(descriptor, fieldData.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obfuscatedName, fieldName, descriptor);
    }

    @Override
    public String toString() {
        return "FieldData{" +
                "obfuscatedName='" + obfuscatedName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}';
    }
}