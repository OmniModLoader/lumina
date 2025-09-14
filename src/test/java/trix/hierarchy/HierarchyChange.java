package trix.hierarchy;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.omnimc.asm.changes.IClassChange;
import org.omnimc.asm.file.ClassFile;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class HierarchyChange implements IClassChange {

    private final HierarchyManager hierarchyManager;
    private final Remapper remapper;

    public HierarchyChange(HierarchyManager hierarchyManager, Remapper remapper) {
        this.hierarchyManager = hierarchyManager;
        this.remapper = remapper;
    }

    @Override
    public ClassFile applyChange(String name, byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        HierarchyClassVisitor classVisitor = new HierarchyClassVisitor(writer, remapper, hierarchyManager);
        reader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

        return new ClassFile(name, classBytes);
    }
}