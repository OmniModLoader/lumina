package temp;

import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.serialization.AbstractMappingSerializer;
import org.omnimc.lumina.serialization.LineSerializer;

import java.net.URI;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class ProguardReader extends AbstractMappingSerializer {

    public ProguardReader() {
        super(new ProguardParser());
    }

    @Override
    public Mappings serialize(URI uri, LineSerializer lineSerializer) {
        return singleFileRead(uri, lineSerializer);
    }
}