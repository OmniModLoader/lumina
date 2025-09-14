package org.omnimc.lumina.mmp.file.data;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class ParameterPatch {

    private final String owner;
    private final String methodName;
    private final String descriptor;
    private final int index;
    private final String patchedParamName;

    public ParameterPatch(String owner, String methodName, String descriptor, int index, String patchedParamName) {
        this.owner = owner;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.index = index;
        this.patchedParamName = patchedParamName;
    }

    public String getOwner() {
        return owner;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public int getIndex() {
        return index;
    }

    public String getPatchedParamName() {
        return patchedParamName;
    }
}