package trix;

import org.objectweb.asm.commons.Remapper;
import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.types.ClassData;
import org.omnimc.lumina.data.types.FieldData;
import org.omnimc.lumina.data.types.MethodData;

import java.util.Objects;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class TrixRemapper extends Remapper {

    private final Mappings mappings;

    public TrixRemapper(Mappings mappings) {
        this.mappings = mappings;
    }

    @Override
    public String map(String internalName) {
        return mapType(internalName);
    }

    @Override
    public String mapType(String internalName) {
        return mappings.getClassName(internalName);
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        ClassData classData = mappings.getClass(owner);
        if (classData == null) {
            return name;
        }

        if (descriptor != null) {
            descriptor = mapDesc(descriptor);
        }

        FieldData field = classData.getField(name, descriptor);

        return field.getFieldName();
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        ClassData classData = mappings.getClass(owner);
        if (classData == null) {
            return name;
        }

        if (descriptor != null) {
            descriptor = mapMethodDesc(descriptor);
        }

        MethodData method = classData.getMethod(name, descriptor);

        return method.getMethodName();
    }
}