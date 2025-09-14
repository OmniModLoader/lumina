package org.omnimc.lumina.mmp.file.data;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class FieldPatch {

    private final String owner;
    private final String originalName;
    private final String type;
    private final String patchedName;

    public FieldPatch(String owner, String originalName, String type, String patchedName) {
        this.owner = owner;
        this.originalName = originalName;
        this.type = type;
        this.patchedName = patchedName;
    }

    public String getOwner() {
        return owner;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getType() {
        return type;
    }

    public String getPatchedName() {
        return patchedName;
    }
}