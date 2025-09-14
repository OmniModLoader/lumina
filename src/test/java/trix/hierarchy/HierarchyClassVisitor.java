package trix.hierarchy;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.omnimc.lumina.data.types.ClassData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.omnimc.asm.access.AccessFlagChecker.isPrivatePresent;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
 */
public class HierarchyClassVisitor extends ClassVisitor {

    private final HierarchyManager hierarchyManager;
    private final Remapper remapper;

    private String obfuscatedClassName;
    private ClassData classData;

    public HierarchyClassVisitor(ClassVisitor classVisitor, Remapper remapper, HierarchyManager hierarchyManager) {
        super(Opcodes.ASM9, classVisitor);
        this.remapper = remapper;
        this.hierarchyManager = hierarchyManager;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        obfuscatedClassName = name;
        this.classData = new ClassData(remapper.map(name));

        classData.addDependentClass(superName);

        if (interfaces != null) {
            for (String anInterface : interfaces) {
                classData.addDependentClass(anInterface);
            }
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        String mappedDescriptor = remapper.mapDesc(descriptor);

        if (isPrivatePresent(access)) {
            this.classData.addPrivateField(name, remapper.mapFieldName(obfuscatedClassName, name, mappedDescriptor), mappedDescriptor);
            return super.visitField(access, name, descriptor, signature, value);
        }

        this.classData.addField(name, remapper.mapFieldName(obfuscatedClassName, name, mappedDescriptor), mappedDescriptor);
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        String mappedDescriptor = remapper.mapMethodDesc(descriptor);

        if (isPrivatePresent(access)) {
            this.classData.addPrivateMethod(name, remapper.mapMethodName(obfuscatedClassName, name, mappedDescriptor), mappedDescriptor);
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        this.classData.addMethod(name, remapper.mapMethodName(obfuscatedClassName, name, mappedDescriptor), mappedDescriptor);
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        hierarchyManager.addClass(obfuscatedClassName, this.classData);
        super.visitEnd();
    }
}