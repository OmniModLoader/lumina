package trix.remap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.omnimc.asm.changes.IClassChange;
import org.omnimc.asm.file.ClassFile;

public class MappingChange implements IClassChange {

    private final Remapper remapper;

    public MappingChange(Remapper remapper) {
        this.remapper = remapper;
    }

    @Override
    public ClassFile applyChange(String name, byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        MappingClassVisitor remappingVisitor = new MappingClassVisitor(writer, remapper);
        reader.accept(remappingVisitor, ClassReader.EXPAND_FRAMES);

        if (name.contains(".class")) {
            name = name.replace(".class", "");
        }

        return new ClassFile(remapper.mapType(name), writer.toByteArray());
    }
}