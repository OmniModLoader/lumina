package temp;

import org.objectweb.asm.commons.Remapper;
import org.omnimc.lumina.Mappings;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
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
    public String mapMethodName(String owner, String name, String descriptor) {
        if (descriptor != null) {
            descriptor = mapMethodDesc(descriptor);
        }

        return mappings.getMethodName(owner, name, descriptor);
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        if (descriptor != null) {
            descriptor = mapDesc(descriptor);
        }

        return mappings.getFieldName(owner, name, descriptor);
    }
}