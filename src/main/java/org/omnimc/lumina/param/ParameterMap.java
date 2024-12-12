package org.omnimc.lumina.param;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class ParameterMap {

    // ClassName unobf, methodName unobf - ParamNode
    private final Map<String, Map<String, CopyOnWriteArrayList<String>>> parameters = new ConcurrentHashMap<>();

    public Map<String, Map<String, CopyOnWriteArrayList<String>>> getParameters() {
        return new ConcurrentHashMap<>(parameters);
    }

    public boolean addParameterName(String className, String methodName, String paramName, int paramIndex) {
        Map<String, CopyOnWriteArrayList<String>> methods = parameters.computeIfAbsent(className, k -> new ConcurrentHashMap<>());

        CopyOnWriteArrayList<String> params = methods.computeIfAbsent(methodName, k -> new CopyOnWriteArrayList<>());

        while (params.size() <= paramIndex) {
            params.add(null);
        }

        boolean isNewEntry = params.get(paramIndex) == null || !params.get(paramIndex).equals(paramName);

        params.set(paramIndex, paramName);
        methods.put(methodName, params);
        parameters.put(className, methods);

        return isNewEntry;
    }

    public String getParameterName(String className, String methodName, int index) {
        Map<String, CopyOnWriteArrayList<String>> methodNames = parameters.get(className);
        if (methodNames == null) {
            return null;
        }

        CopyOnWriteArrayList<String> paramNames = methodNames.get(methodName);
        if (paramNames == null) {
            return null;
        }

        return paramNames.get(index);
    }

    public void addAll(@NotNull Map<String, Map<String, CopyOnWriteArrayList<String>>> other) {
        this.parameters.putAll(other);
    }

    public void addAll(ParameterMap parameterMap) {
        this.parameters.putAll(parameterMap.parameters);
    }

    @Override
    public String toString() {
        return "ParameterMap{" +
                "parameters=" + parameters +
                '}';
    }
}