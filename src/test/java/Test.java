import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.omnimc.asm.changes.IClassChange;
import org.omnimc.asm.file.ClassFile;
import org.omnimc.asm.file.IOutputFile;
import org.omnimc.asm.manager.thread.SafeClassManager;
import org.omnimc.lumina.MappingType;
import org.omnimc.lumina.Mappings;
import org.omnimc.lumina.deserialization.compressed.CompressedDeserializer;
import org.omnimc.lumina.serialization.compressed.CompressedSerializer;
import org.omnimc.trix.ClassModifier;
import temp.ProguardReader;
import temp.hierarchy.HierarchyChange;
import temp.hierarchy.HierarchyManager;
import temp.hierarchy.HierarchyProvider;
import temp.remap.MappingChange;

import javax.print.attribute.standard.Compression;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class Test {

    public static void main(String[] args) throws URISyntaxException {
/*
        ProguardReader reader = new ProguardReader();
        Mappings serialize = reader.serialize(new URI("https://piston-data.mojang.com/v1/objects/0cf2a0b7f056da1a5a5dd99fc6dc752f33987150/client.txt"));

        CompressedDeserializer deserializer = new CompressedDeserializer();
        deserializer.setConsumer(System.out::println);
        File file = new File("C:\\Users\\CryroByte\\Desktop\\omnimc-project\\Universal-Mappings\\mappings\\" + "normalmapping.mmap");
        System.out.println(deserializer.deserializeToFile(serialize, file));

        HierarchyProvider provider = new HierarchyProvider("C:\\Users\\CryroByte\\AppData\\Roaming\\.minecraft\\versions\\1.21.4\\1.21.4.jar", file.toURI());
        try {
            provider.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            provider.write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
*/

        CompressedSerializer serializer = new CompressedSerializer();
        serializer.setConsumer(System.out::println);
        Mappings serialize = serializer.serialize(new File("C:\\Users\\CryroByte\\Desktop\\omnimc-project\\Universal-Mappings\\mappings\\hierarchy\\hierarchy.mmap").toURI());

        SafeClassManager classManager = new SafeClassManager();
        classManager.readJarFile(new File("C:\\Users\\CryroByte\\AppData\\Roaming\\.minecraft\\versions\\1.21.4\\1.21.4.jar"));

        classManager.applyChanges(new MappingChange(serialize));

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