package trix.remap;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.Remapper;
import org.omnimc.trix.chain.ChainedClassVisitor;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class MappingClassVisitor extends ClassVisitor implements ChainedClassVisitor {

    private final Remapper remapper;

    private String obfuscatedClassName;

    public MappingClassVisitor(ClassVisitor classVisitor, Remapper remapper) {
        super(Opcodes.ASM9, classVisitor);
        this.remapper = remapper;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.obfuscatedClassName = name;
        super.visit(version, access, remapper.mapType(name), remapper.mapSignature(signature, false),
                    remapper.mapType(superName), interfaces == null ? null : remapper.mapTypes(interfaces));
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        String methodDesc = descriptor == null ? null : remapper.mapMethodDesc(descriptor);
        super.visitOuterClass(remapper.mapType(owner), remapper.mapMethodName(owner, name, methodDesc), methodDesc);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(remapper.mapType(name),
                              outerName == null ? null : remapper.mapType(outerName),
                              innerName == null ? null : remapper.mapType(innerName), access);
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
        super.visitPermittedSubclass(remapper.mapType(permittedSubclass));
    }

    @Override
    public void visitNestHost(String nestHost) {
        super.visitNestHost(remapper.mapType(nestHost));
    }

    @Override
    public void visitNestMember(String nestMember) {
        super.visitNestMember(remapper.mapType(nestMember));
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        return super.visitRecordComponent(
                name,
                remapper.mapDesc(descriptor),
                remapper.mapSignature(signature, true));
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access,
                                remapper.mapFieldName(obfuscatedClassName, name, descriptor),
                                remapper.mapDesc(descriptor),
                                remapper.mapSignature(signature, true), remapper.mapValue(value));
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        String mappedMethodDesc = remapper.mapMethodDesc(descriptor);
        MethodVisitor methodVisitor = super.visitMethod(access,
                                                        remapper.mapMethodName(obfuscatedClassName, name, mappedMethodDesc),
                                                        mappedMethodDesc,
                                                        remapper.mapSignature(signature, false),
                                                        exceptions == null ? null : remapper.mapTypes(exceptions));

        return new MappingMethodVisitor(methodVisitor, remapper);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return super.visitTypeAnnotation(typeRef, typePath, remapper.mapDesc(descriptor), visible);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(remapper.mapDesc(descriptor), visible);
    }

    @Override
    public ClassVisitor withNext(ClassVisitor next) {
        this.cv = next;
        return this;
    }
}