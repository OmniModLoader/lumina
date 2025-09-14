

import org.omnimc.asm.file.IOutputFile;
import org.omnimc.asm.manager.thread.SafeClassManager;
import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.serialization.proguard.ProguardLineSerializer;
import org.omnimc.lumina.data.types.ClassData;
import org.omnimc.lumina.mcmap.McMap;
import org.omnimc.lumina.util.SerializationHelper;
import trix.McMapRemapper;
import trix.hierarchy.provider.HierarchyProvider;
import trix.remap.MappingChange;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
 */
public class LittenEndianTest {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Mappings mappings = new Mappings();
        URI location = new URI("https://piston-data.mojang.com/v1/objects/0cf2a0b7f056da1a5a5dd99fc6dc752f33987150/client.txt");
        SerializationHelper.getMappingsFromInputStream(location.toURL().openStream(), new ProguardLineSerializer(), mappings);

        ClassData aClass = mappings.getClassByValue("net/minecraft/client/Camera");
        System.out.println(aClass.getMethods());

        HierarchyProvider provider = new HierarchyProvider(new File("C:\\Users\\CryroByte\\AppData\\Roaming\\.minecraft\\versions\\1.21.4\\1.21.4.jar"), mappings);

        provider.init();

        McMap finishedMappings = provider.write();

        SafeClassManager classManager = new SafeClassManager();
        classManager.readJarFile(new File("C:\\Users\\CryroByte\\AppData\\Roaming\\.minecraft\\versions\\1.21.4\\1.21.4.jar"));

        classManager.applyChanges(new MappingChange(new McMapRemapper(finishedMappings)));

        IOutputFile iOutputFile = classManager.outputFile();
        byte[] fileInBytes = iOutputFile.getFileInBytes(1);

        try {
            FileOutputStream inputStream = new FileOutputStream("C:\\Users\\CryroByte\\Desktop\\omnimc-project\\Universal-Mappings\\mappings\\modded-1.21.4.jar");
            inputStream.write(fileInBytes);
            inputStream.flush();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}