import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.serialization.proguard.ProguardLineSerializer;
import org.omnimc.lumina.data.types.ClassData;
import org.omnimc.lumina.mcmap.McMap;
import org.omnimc.lumina.util.SerializationHelper;

import java.io.File;
import java.net.URI;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
 */
public class MappingTest {

    private static final File progaurdMappingsFile = new File("C:\\Users\\CryroByte\\Desktop\\omnimc-project\\Universal-Mappings\\mappings\\original.mcmap");
    private static final File minecraftJar = new File("C:\\Users\\CryroByte\\AppData\\Roaming\\.minecraft\\versions\\1.21.4\\1.21.4.jar");
    private static final File hierarchyMappingsFile = new File("C:\\Users\\CryroByte\\Desktop\\omnimc-project\\Universal-Mappings\\mappings\\hierarchy\\hierarchy.mcmap");

    public static void main(String[] args) throws Exception {
        Mappings progaurdMappings = new Mappings();
        URI location = new URI("https://piston-data.mojang.com/v1/objects/0cf2a0b7f056da1a5a5dd99fc6dc752f33987150/client.txt");
        SerializationHelper.getMappingsFromInputStream(location.toURL().openStream(), new ProguardLineSerializer(), progaurdMappings);

    }
}