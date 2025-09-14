package org.omnimc.lumina.mmp.file.data;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class ClassPatch {

    private final String originalClassName;
    private final String patchedClassName;

    public ClassPatch(String originalClassName, String patchedClassName) {
        this.originalClassName = originalClassName;
        this.patchedClassName = patchedClassName;
    }

    public String getOriginalClassName() {
        return originalClassName;
    }

    public String getPatchedClassName() {
        return patchedClassName;
    }
}