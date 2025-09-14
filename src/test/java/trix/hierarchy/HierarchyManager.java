package trix.hierarchy;

import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.types.ClassData;

import java.util.*;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class HierarchyManager {

    private final Mappings mappings = new Mappings();

    public Mappings getMappings() {
        return mappings;
    }

    public void addClass(String obfuscatedName, ClassData data) {
        mappings.addClass(obfuscatedName, data);
    }

/*    public void populateClassFiles() {

        for (Map.Entry<String, List<String>> dep : mapDependencies.entrySet()) {
            String obfuscatedName = dep.getKey();

            ClassData originalClassData = mappings.getClass(obfuscatedName);

            List<String> dependencies = dep.getValue();

            System.out.println("First row for: " + originalClassData.getClassName());
            System.out.println(dependencies);
            while (!dependencies.isEmpty()) {
                List<String> nextDep = new ArrayList<>();
                for (String dependency : dependencies) {
                    if (dependency.isEmpty()) {
                        continue;
                    }

                    ClassData aClass = mappings.getClass(dependency);
                    if (aClass == null) {
                        continue;
                    }

                    originalClassData.getFields().putAll(aClass.getFields());
                    originalClassData.getMethods().putAll(aClass.getMethods());

                    nextDep.addAll(mapDependencies.getOrDefault(mappings.getClassNameByValue(aClass.getClassName()), new ArrayList<>()));
                    System.out.println(nextDep);
                }
                dependencies = nextDep;
            }
            System.out.println("last row for: " + originalClassData.getClassName() + "\r\n");

            mappings.addClass(obfuscatedName, originalClassData);
        }
    }*/

    public void populateClassFiles() {
        final HashMap<String, ClassData> classFileHashMap = new HashMap<>();

        for (Map.Entry<String, ClassData> entry : mappings.getClasses().entrySet()) {
            String className = entry.getKey();
            ClassData originalClassFile = entry.getValue();

            ArrayList<String> dependencies = new ArrayList<>(originalClassFile.getDependentClasses());
            while (!dependencies.isEmpty()) {
                ArrayList<String> nextDependencies = new ArrayList<>();
                for (String dependency : dependencies) {
                    ClassData file = mappings.getClass(dependency);
                    if (file != null) {
                        originalClassFile.getFields().putAll(file.getFields());
                        originalClassFile.getMethods().putAll(file.getMethods());
                        nextDependencies.addAll(file.getDependentClasses());
                    }
                }
                dependencies = nextDependencies;
            }

            classFileHashMap.put(className, originalClassFile);
        }

        mappings.getClasses().clear();
        mappings.getClasses().putAll(classFileHashMap);
    }
}