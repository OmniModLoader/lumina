/*
 * MIT License
 *
 * Copyright (c) 2024 OmniMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package temp.hierarchy;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.omnimc.lumina.Mappings;
import temp.TrixRemapper;
import temp.hierarchy.info.ClassInfo;

import static org.omnimc.asm.access.AccessFlagChecker.isPrivatePresent;

/**
 * {@code HierarchyClassVisitor} is a custom {@linkplain ClassVisitor} that works with the {@linkplain HierarchyManager}
 * to gather information about classes, including their fields and methods. It uses a {@linkplain MappingContainer} to
 * map names and descriptors from obfuscated to readable forms.
 *
 * <p>This visitor collects class details as it processes them, including fields and methods, and updates
 * the {@linkplain HierarchyManager} with this information. It also uses the {@linkplain TrixRemapper} to translate
 * obfuscated names and descriptors into more understandable forms.</p>
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
public class HierarchyClassVisitor extends ClassVisitor {

    private final HierarchyManager hierarchyManager;
    private final Mappings mappings;

    private final Remapper remapper;

    private String originalClassName;
    private ClassInfo classInfo;

//    private final int allFlags = Opcodes.ACC_PUBLIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED;


    /**
     * <h6>Constructs a new {@code HierarchyClassVisitor} that will use the given {@linkplain HierarchyManager} and
     * {@linkplain MappingContainer}.
     *
     * @param classVisitor     The parent {@linkplain ClassVisitor} to use.
     * @param hierarchyManager The {@linkplain HierarchyManager} to update with class information.
     * @param container        The {@linkplain MappingContainer} for mapping names and descriptors.
     */
    public HierarchyClassVisitor(ClassVisitor classVisitor, HierarchyManager hierarchyManager, Mappings mappings) {
        super(Opcodes.ASM9, classVisitor);
        this.mappings = mappings;
        this.hierarchyManager = hierarchyManager;
        this.remapper = new TrixRemapper(mappings);
    }

    /**
     * <h6>Visits a class, collecting its information and updating the {@linkplain HierarchyManager}.
     *
     * @param name       The name of the class.
     * @param version    The class version.
     * @param superName  The name of the superclass.
     * @param interfaces The names of the interfaces implemented by the class.
     * @param access     The access flags of the class.
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.originalClassName = name;
        classInfo = new ClassInfo(remapper.mapType(name));

        classInfo.addDependentClass(superName);

        if (interfaces != null) {
            for (String anInterface : interfaces) {
                classInfo.addDependentClass(anInterface);
            }
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    /**
     * <h6>Visits a field in the class, collecting information about it.
     *
     * @param access     The access flags of the field.
     * @param name       The name of the field.
     * @param descriptor The field descriptor.
     * @param signature  The signature of the field.
     * @param value      The constant value of the field, if applicable.
     */
    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        String mappedDescriptor = remapper.mapDesc(descriptor);

        if (isPrivatePresent(access)) {
            classInfo.addPrivateField(name, mappings.getFieldName(originalClassName, name, mappedDescriptor), mappedDescriptor);
            return super.visitField(access, name, descriptor, signature, value);
        }

        classInfo.addField(name, mappings.getFieldName(originalClassName, name, mappedDescriptor), mappedDescriptor);
        return super.visitField(access, name, descriptor, signature, value);
    }

    /**
     * <h6>Visits a method in the class, collecting information about it.
     *
     * @param access     The access flags of the method.
     * @param name       The name of the method.
     * @param descriptor The method descriptor.
     * @param signature  The signature of the method.
     * @param exceptions The names of the exceptions thrown by the method.
     * @return A {@linkplain MethodVisitor} to visit the method instructions.
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        String mappedDescriptor = remapper.mapMethodDesc(descriptor);
        if (isPrivatePresent(access)) {
            classInfo.addPrivateMethod(name, mappings.getMethodName(originalClassName, name, mappedDescriptor), mappedDescriptor);
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        classInfo.addMethod(name, mappings.getMethodName(originalClassName, name, mappedDescriptor), mappedDescriptor);
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    /**
     * <h6>Visits the end of the class, finalizing the collection of class information.
     */
    @Override
    public void visitEnd() {
        hierarchyManager.addClassFile(originalClassName, classInfo);
        super.visitEnd();
    }
}