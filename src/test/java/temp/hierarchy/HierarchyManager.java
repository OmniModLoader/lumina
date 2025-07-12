package temp.hierarchy;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.commons.Remapper;
import temp.hierarchy.info.ClassInfo;
import temp.hierarchy.info.FieldInfo;
import temp.hierarchy.info.MethodInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code HierarchyManager} is designed to keep track of class files and their details, making it easy to manage and
 * access information about classes, fields, and methods.
 *
 * <p>It helps you add new classes, retrieve information about them, and look up methods and fields, even if they
 * have obfuscated names. It can also handle class dependencies, so all related information stays updated.</p>
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
public class HierarchyManager {

    private final HashMap<String, ClassInfo> classFiles = new HashMap<>();

    /**
     * <h6>Adds a class file to the manager. If a class with the same name already exists, it won't be added again.
     *
     * @param name The name of the class.
     * @param file The {@linkplain ClassInfo} object containing details about the class.
     */
    public void addClassFile(@NotNull String name, @NotNull ClassInfo file) {
        if (classFiles.containsKey(name)) {
            return;
        }

        classFiles.put(name, file);
    }

    /**
     * <h6>Retrieves the {@linkplain ClassInfo} for a given class name.
     *
     * @param name The name of the class to retrieve.
     * @return The {@linkplain ClassInfo} for the class, or `null` if the class is not found.
     */
    @Nullable
    public ClassInfo getClassInfo(@NotNull String name) {
        return classFiles.get(name);
    }

    /**
     * <h6>Gets the human-readable name of a class based on its internal name.
     *
     * @param name The internal name of the class.
     * @return The readable name of the class, or the original name if no mapping is found.
     */
    @NotNull
    public String getClassName(@NotNull String name) {
        final ClassInfo classInfo = getClassInfo(name);
        if (classInfo == null) {
            return name;
        }

        return classInfo.getClassName();
    }

    /**
     * <h6>Returns all class files currently managed by this {@code HierarchyManager}.
     *
     * @return A map of class names to their corresponding {@linkplain ClassInfo} objects.
     */
    @NotNull
    public HashMap<String, ClassInfo> getClassFiles() {
        return classFiles;
    }

    /**
     * <h6>Looks up a method by its owner class, obfuscated name, and descriptor.
     *
     * @param owner          The name of the class that owns the method.
     * @param obfuscatedName The obfuscated name of the method.
     * @param descriptor     The method descriptor.
     * @return The {@linkplain MethodInfo} for the method, or {@code null} if not found.
     */
    @Nullable
    public MethodInfo getMethod(@NotNull String owner, @NotNull String obfuscatedName, @NotNull String descriptor) {
        final ClassInfo classInfo = classFiles.get(owner);
        if (classInfo == null) {
            return null;
        }

        return classInfo.getMethods().getOrDefault(obfuscatedName + descriptor, null);
    }

    /**
     * <h6>Gets the human-readable name of a method based on its owner class, obfuscated name, and descriptor.
     *
     * @param owner          The name of the class that owns the method.
     * @param obfuscatedName The obfuscated name of the method.
     * @param descriptor     The method descriptor.
     * @return The readable name of the method, or the original obfuscated name if no mapping is found.
     */
    @Nullable
    public String getMethodName(@NotNull String owner, @NotNull String obfuscatedName, @NotNull String descriptor) {
        final MethodInfo method = this.getMethod(owner, obfuscatedName, descriptor);
        if (method == null) {
            return obfuscatedName;
        }

        return method.getMethodName();
    }

    /**
     * <h6>Looks up a private method by its owner class, obfuscated name, and descriptor.
     *
     * @param owner          The name of the class that owns the method.
     * @param obfuscatedName The obfuscated name of the method.
     * @param descriptor     The method descriptor.
     * @return The {@linkplain MethodInfo} for the private method, or {@code null} if not found.
     */
    @Nullable
    public MethodInfo getPrivateMethod(@NotNull String owner, @NotNull String obfuscatedName, @NotNull String descriptor) {
        final ClassInfo classInfo = classFiles.get(owner);
        if (classInfo == null) {
            return null;
        }

        return classInfo.getPrivateMethods().getOrDefault(obfuscatedName + descriptor, null);
    }

    /**
     * <h6>Gets the human-readable name of a private method based on its owner class, obfuscated name, and descriptor.
     *
     * @param owner          The name of the class that owns the method.
     * @param obfuscatedName The obfuscated name of the method.
     * @param descriptor     The method descriptor.
     * @return The readable name of the method, or the original obfuscated name if no mapping is found.
     */
    @Nullable
    public String getPrivateMethodName(@NotNull String owner, @NotNull String obfuscatedName, @NotNull String descriptor) {
        final MethodInfo method = this.getPrivateMethod(owner, obfuscatedName, descriptor);
        if (method == null) {
            return obfuscatedName;
        }

        return method.getMethodName();
    }

    /**
     * <h6>Looks up a field by its owner class, obfuscated name, and descriptor.
     *
     * @param owner          The name of the class that owns the field.
     * @param obfuscatedName The obfuscated name of the field.
     * @param descriptor     The field descriptor.
     * @return The {@linkplain FieldInfo} for the field, or {@code null} if not found.
     */
    @Nullable
    public FieldInfo getField(@NotNull String owner, @NotNull String obfuscatedName, @NotNull String descriptor) {
        final ClassInfo classInfo = classFiles.get(owner);
        if (classInfo == null) {
            return null;
        }

        return classInfo.getFields().getOrDefault(obfuscatedName + descriptor, null);
    }

    /**
     * <h6>Gets the human-readable name of a field based on its owner class, obfuscated name, and descriptor.
     *
     * @param owner          The name of the class that owns the field.
     * @param obfuscatedName The obfuscated name of the field.
     * @param descriptor     The field descriptor.
     * @return The readable name of the field, or the original obfuscated name if no mapping is found.
     */
    @Nullable
    public String getFieldName(@NotNull String owner, @NotNull String obfuscatedName, @NotNull String descriptor) {
        final FieldInfo field = this.getField(owner, obfuscatedName, descriptor);
        if (field == null) {
            return obfuscatedName;
        }

        return field.getFieldName();
    }

    /**
     * <h6>Looks up a private field by its owner class, obfuscated name, and descriptor.
     *
     * @param owner          The name of the class that owns the field.
     * @param obfuscatedName The obfuscated name of the field.
     * @param descriptor     The field descriptor.
     * @return The {@linkplain FieldInfo} for the private field, or {@code null} if not found.
     */
    @Nullable
    public FieldInfo getPrivateField(@NotNull String owner, @NotNull String obfuscatedName, @NotNull String descriptor) {
        final ClassInfo classInfo = classFiles.get(owner);
        if (classInfo == null) {
            return null;
        }

        return classInfo.getPrivateFields().getOrDefault(obfuscatedName + descriptor, null);
    }

    /**
     * <h6>Gets the human-readable name of a private field based on its owner class, obfuscated name, and descriptor.
     *
     * @param owner          The name of the class that owns the field.
     * @param obfuscatedName The obfuscated name of the field.
     * @param descriptor     The field descriptor.
     * @return The readable name of the field, or the original obfuscated name if no mapping is found.
     */
    @Nullable
    public String getPrivateFieldName(@NotNull String owner, @NotNull String obfuscatedName, @NotNull String descriptor) {
        final FieldInfo field = this.getPrivateField(owner, obfuscatedName, descriptor);
        if (field == null) {
            return obfuscatedName;
        }

        return field.getFieldName();
    }

    /**
     * Updates the class files to include information from dependent classes. This ensures that all dependencies are
     * accounted for and their fields and methods are included.
     */
    public void populateClassFiles() {
        final HashMap<String, ClassInfo> classFileHashMap = new HashMap<>();

        for (Map.Entry<String, ClassInfo> entry : classFiles.entrySet()) {
            String className = entry.getKey();
            ClassInfo originalClassFile = entry.getValue();

            ArrayList<String> dependencies = new ArrayList<>(originalClassFile.getDependentClasses());
            while (!dependencies.isEmpty()) {
                ArrayList<String> nextDependencies = new ArrayList<>();
                for (String dependency : dependencies) {
                    ClassInfo file = classFiles.get(dependency);
                    if (file != null) {
                        originalClassFile.addFields(file.getFields(),file);
                        originalClassFile.getMethods().putAll(file.getMethods());
                        nextDependencies.addAll(file.getDependentClasses());
                    }
                }
                dependencies = nextDependencies;
            }

            classFileHashMap.put(className, originalClassFile);
        }

        classFiles.clear();
        classFiles.putAll(classFileHashMap);
    }

    /**
     * Provides a custom {@linkplain Remapper} that translates obfuscated names to their readable equivalents using
     * class, method, and field name mappings.
     *
     * @return A {@linkplain Remapper} instance that maps obfuscated names to their readable forms.
     */
    public Remapper getRemapper() {
        return new CustomRemapper(this);
    }

    static class CustomRemapper extends Remapper {

        private final HierarchyManager hierarchyManager;

        CustomRemapper(HierarchyManager hierarchyManager) {
            this.hierarchyManager = hierarchyManager;
        }

        /**
         * <h6>Maps the internal name of a class.
         *
         * <p>This method translates the internal name of a class (e.g., `java/lang/String`) to its
         * readable form using {@linkplain #mapType(String)}.</p>
         *
         * @param internalName The internal name of the class.
         * @return The mapped class name, or the original internal name if no mapping is found.
         */
        @Override
        public String map(String internalName) {
            return mapType(internalName);
        }

        /**
         * <h6>Maps the internal name of a class type.
         *
         * <p>This method uses {@linkplain HierarchyManager#getClassName(String)} to convert the internal name of a
         * class
         * (e.g., `java/lang/String`) to its readable form.</p>
         *
         * @param internalName The internal name of the class.
         * @return The mapped class name, or the original internal name if no mapping is found.
         */
        @Override
        public String mapType(String internalName) {
            return hierarchyManager.getClassName(internalName);
        }

        /**
         * <h6>Maps the name of a method.
         *
         * <p>This method translates an obfuscated method name to its readable form. It uses the method's
         * descriptor and name to find the readable method name through
         * {@linkplain HierarchyManager#getMethodName(String, String, String)}. If a readable name is not found, it
         * falls back to the original name. If the method is private, it checks for a private method name using
         * {@linkplain HierarchyManager#getPrivateMethodName(String, String, String)}.</p>
         *
         * @param owner      The internal name of the owner class of the method.
         * @param name       The obfuscated name of the method.
         * @param descriptor The method descriptor.
         * @return The mapped method name, or the original method name if no mapping is found.
         */
        @Override
        public String mapMethodName(String owner, String name, String descriptor) {
            if (descriptor != null) {
                descriptor = mapDesc(descriptor);
            }

            String methodName = hierarchyManager.getMethodName(owner, name, descriptor);
            if (methodName == null) {
                return name;
            }

            if (methodName.equals(name)) {
                String privateMethodName = hierarchyManager.getPrivateMethodName(owner, name, descriptor);
                if (privateMethodName == null) {
                    return name;
                }

                if (privateMethodName.equals(name)) {
                    return name;
                }

                return privateMethodName;
            }

            return methodName;
        }

        /**
         * <h6>Maps the name of a field.
         *
         * <p>This method translates an obfuscated field name to its readable form. It uses the field's
         * descriptor and name to find the readable field name through
         * {@linkplain HierarchyManager#getFieldName(String, String, String)}. If a readable name is not found, it falls
         * back to the original name. If the field is private, it checks for a private field name using
         * {@linkplain HierarchyManager#getPrivateFieldName(String, String, String)}.</p>
         *
         * @param owner      The internal name of the owner class of the field.
         * @param name       The obfuscated name of the field.
         * @param descriptor The field descriptor.
         * @return The mapped field name, or the original field name if no mapping is found.
         */
        @Override
        public String mapFieldName(String owner, String name, String descriptor) {
            descriptor = mapDesc(descriptor);

            String fieldName = hierarchyManager.getFieldName(owner, name, descriptor);
            if (fieldName == null) {
                return name;
            }

            if (fieldName.equals(name)) {
                String privateFieldName = hierarchyManager.getPrivateFieldName(owner, name, descriptor);
                if (privateFieldName == null) {
                    return name;
                }

                if (privateFieldName.equals(name)) {
                    return name;
                }
                return privateFieldName;
            }

            return fieldName;
        }
    }
}