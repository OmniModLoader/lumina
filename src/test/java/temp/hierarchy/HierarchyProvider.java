package temp.hierarchy;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.omnimc.asm.changes.IClassChange;
import org.omnimc.asm.file.ClassFile;
import org.omnimc.asm.manager.thread.SafeClassManager;
import org.omnimc.lumina.MappingType;
import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.deserialization.compressed.CompressedDeserializer;
import org.omnimc.lumina.serialization.compressed.CompressedSerializer;
import org.omnimc.lumina.serialization.full.FullSerializer;
import temp.hierarchy.info.ClassInfo;
import temp.hierarchy.info.FieldInfo;
import temp.hierarchy.info.MethodInfo;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class HierarchyProvider {
    private final String minecraftJar;
    private final URI mappingLocation;

    private HierarchyManager hierarchyManager;

    public HierarchyProvider(String minecraftJar, URI mappingLocation) {
        this.minecraftJar = minecraftJar;
        this.mappingLocation = mappingLocation;
    }

    public void init() throws IOException {
        Mappings reader = getReader(mappingLocation, MappingType.COMPRESSED);
        HierarchyManager hierarchyManager = new HierarchyManager();
        System.out.println(reader.getClasses().size());

        SafeClassManager classManager = new SafeClassManager();
        classManager.readJarFile(new File(minecraftJar));
        classManager.applyChanges(((IClassChange) (name, classBytes) -> {
            ClassReader classReader = new ClassReader(classBytes);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classReader.accept(new HierarchyClassVisitor(writer, hierarchyManager, reader), ClassReader.EXPAND_FRAMES);

            return new ClassFile(name, classBytes);
        }));
        classManager.close();
        hierarchyManager.populateClassFiles();
        this.hierarchyManager = hierarchyManager;
        System.out.println(hierarchyManager.getClassFiles().size());

    }

    public void write() throws IOException {
        Mappings container = new Mappings();

        populateClassNames(hierarchyManager, container);
        populateMethodNames(hierarchyManager, container);
        populateFieldNames(hierarchyManager, container);

        CompressedDeserializer deserializer = new CompressedDeserializer();
        deserializer.setConsumer(System.out::println);
        File path = new File("C:\\Users\\CryroByte\\Desktop\\omnimc-project\\Universal-Mappings\\mappings\\hierarchy");
        path.mkdirs();
        System.out.println(deserializer.deserializeToFile(container, new File("C:\\Users\\CryroByte\\Desktop\\omnimc-project\\Universal-Mappings\\mappings" + "\\hierarchy\\hierarchy.mmap")));
    }

    private void populateMethodNames(HierarchyManager hierarchyManager, Mappings container) {
        for (Map.Entry<String, ClassInfo> entry : hierarchyManager.getClassFiles().entrySet()) {
            String obfuscatedClassName = entry.getKey();
            ClassInfo classInfo = entry.getValue();

            for (Map.Entry<String, MethodInfo> methodEntry : classInfo.getMethods().entrySet()) {
                MethodInfo methodInfo = methodEntry.getValue();

                container.addMethod(obfuscatedClassName, methodInfo.getObfuscatedName() + methodInfo.getDescriptor(), methodInfo.getMethodName());
            }

            for (Map.Entry<String, MethodInfo> privateMethodEntry : classInfo.getPrivateMethods().entrySet()) {
                MethodInfo methodInfo = privateMethodEntry.getValue();

                container.addMethod(obfuscatedClassName, methodInfo.getObfuscatedName() + methodInfo.getDescriptor(), methodInfo.getMethodName());
            }
        }
    }

    private void populateFieldNames(HierarchyManager hierarchyManager, Mappings container) {
        for (Map.Entry<String, ClassInfo> entry : hierarchyManager.getClassFiles().entrySet()) {
            String obfuscatedClassName = entry.getKey();
            ClassInfo classInfo = entry.getValue();

            for (Map.Entry<String, FieldInfo> fieldEntry : classInfo.getFields().entrySet()) {
                FieldInfo fieldInfo = fieldEntry.getValue();

                container.addField(obfuscatedClassName, fieldInfo.getObfuscatedName() + fieldInfo.getDescriptor(), fieldInfo.getFieldName());
            }

            for (Map.Entry<String, FieldInfo> privateFieldEntry : classInfo.getPrivateFields().entrySet()) {
                FieldInfo fieldInfo = privateFieldEntry.getValue();

                container.addField(obfuscatedClassName, fieldInfo.getObfuscatedName() + fieldInfo.getDescriptor(), fieldInfo.getFieldName());
            }
        }
    }

    private void populateClassNames(HierarchyManager hierarchyManager, Mappings container) {
        for (Map.Entry<String, ClassInfo> entry : hierarchyManager.getClassFiles().entrySet()) {
            String obfuscatedName = entry.getKey();
            ClassInfo classInfo = entry.getValue();

            container.addClass(obfuscatedName, classInfo.getClassName());
        }
    }


    private Mappings getReader(URI uri, MappingType type) {
        return switch (type) {
            case COMPRESSED -> {
                CompressedSerializer serializer = new CompressedSerializer();
                yield serializer.serialize(uri);
            }
            case FULL -> { // NOT WORKING TODO
                FullSerializer serializer = new FullSerializer(null);
                yield serializer.serialize(uri);
            }
            default -> null;
        };
    }
}