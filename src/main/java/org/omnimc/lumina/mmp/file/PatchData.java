package org.omnimc.lumina.mmp.file;

import org.omnimc.lumina.mmp.file.data.ClassPatch;
import org.omnimc.lumina.mmp.file.data.FieldPatch;
import org.omnimc.lumina.mmp.file.data.MethodPatch;
import org.omnimc.lumina.mmp.file.data.ParameterPatch;
import org.omnimc.lumina.mmp.namespace.Namespace;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class PatchData {

    private Namespace namespace;

    private final List<ClassPatch> classPatches = new ArrayList<>();
    private final List<FieldPatch> fieldPatches = new ArrayList<>();
    private final List<MethodPatch> methodPatches = new ArrayList<>();
    private final List<ParameterPatch> parameterPatches = new ArrayList<>();

    public PatchData() {
        this(null);
    }

    public PatchData(Namespace namespace) {
        this.namespace = namespace;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public List<ClassPatch> getClassPatches() {
        return classPatches;
    }

    public List<FieldPatch> getFieldPatches() {
        return fieldPatches;
    }

    public List<MethodPatch> getMethodPatches() {
        return methodPatches;
    }

    public List<ParameterPatch> getParameterPatches() {
        return parameterPatches;
    }
}