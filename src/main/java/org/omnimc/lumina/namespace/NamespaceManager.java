package org.omnimc.lumina.namespace;

import org.omnimc.lumina.mcmap.McMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public final class NamespaceManager {

    public static final Namespace INTERMEDIARY = new Namespace("intermediary", null);

    private final Map<String, Namespace> allNamespaces = new HashMap<>();

    public NamespaceManager(McMap intermediary) {
        INTERMEDIARY.setProperty(intermediary);
        addNamespace(INTERMEDIARY);
    }

    public void addNamespace(Namespace namespace) {
        allNamespaces.put(namespace.getName(), namespace);
    }

    public Namespace getNamespace(String namespace) {
        return allNamespaces.get(namespace);
    }

}