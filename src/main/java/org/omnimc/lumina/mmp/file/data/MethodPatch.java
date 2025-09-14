package org.omnimc.lumina.mmp.file.data;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class MethodPatch {

    private final String owner;
    private final String originalName;
    private final String descriptor;
    private final String patchedName;

    public MethodPatch(String owner, String originalName, String descriptor, String patchedName) {
        this.owner = owner;
        this.originalName = originalName;
        this.descriptor = descriptor;
        this.patchedName = patchedName;
    }

    public String getOwner() {
        return owner;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public String getPatchedName() {
        return patchedName;
    }
}