package org.omnimc.lumina.data.types;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An {@link MethodData} is always linked to a {@link ClassData}.
 *
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class MethodData {

    private final String obfuscatedName;
    private final String methodName;
    private final String descriptor;

    /**
     * Not added yet
     */
    private final Map<Integer, ParameterData> parameters = new HashMap<>();

    public MethodData(String obfuscatedName,
                      String methodName, String descriptor) {
        this.obfuscatedName = obfuscatedName;
        this.methodName = methodName;
        this.descriptor = descriptor;
    }

    public String getObfuscatedName() {
        return obfuscatedName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    /**
     * Not added yet.
     * @throws UnsupportedOperationException
     */
    public Map<Integer, ParameterData> getParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        MethodData that = (MethodData) object;
        return Objects.equals(obfuscatedName, that.obfuscatedName) &&
                Objects.equals(methodName, that.methodName) &&
                Objects.equals(descriptor, that.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obfuscatedName, methodName, descriptor);
    }

    @Override
    public String toString() {
        return "MethodData{" +
                "obfuscatedName='" + obfuscatedName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}';
    }
}