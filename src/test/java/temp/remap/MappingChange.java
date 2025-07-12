package temp.remap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.omnimc.asm.changes.IClassChange;
import org.omnimc.asm.file.ClassFile;
import org.omnimc.lumina.Mappings;
import temp.TrixRemapper;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
 */
public class MappingChange implements IClassChange {

    private final TrixRemapper remapper;

    public MappingChange(Mappings mappings) {
        this.remapper = new TrixRemapper(mappings);
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