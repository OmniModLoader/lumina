package temp.hierarchy;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.omnimc.asm.changes.IClassChange;
import org.omnimc.asm.file.ClassFile;
import org.omnimc.lumina.Mappings;

public class HierarchyChange implements IClassChange {

    private final HierarchyManager hierarchyManager;
    private final Mappings mappings;

    public HierarchyChange(HierarchyManager hierarchyManager, Mappings mappings) {
        this.hierarchyManager = hierarchyManager;
        this.mappings = mappings;
    }

    @Override
    public ClassFile applyChange(String name, byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        HierarchyClassVisitor hierarchyVisitor = new HierarchyClassVisitor(writer, hierarchyManager, mappings);
        reader.accept(hierarchyVisitor, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

        return new ClassFile(name.replace(".class", ""), classBytes);
    }
}