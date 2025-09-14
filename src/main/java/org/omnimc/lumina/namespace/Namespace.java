package org.omnimc.lumina.namespace;

import org.omnimc.lumina.mcmap.McMap;
import org.omnimc.lumina.mmp.Patch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class Namespace {

    private final String name;
    private McMap property;
    private final List<Patch> patches;

    public Namespace(String name, McMap property) {
        this.name = name;
        this.property = property;

        this.patches = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public McMap getProperty() {
        return property;
    }

    public void setProperty(McMap property) {
        this.property = property;
    }

    public List<Patch> getPatches() {
        return patches;
    }

    public void addPatch(Patch patch) {
        patches.add(patch);
    }
}