package trix.hierarchy.provider;

import org.omnimc.asm.manager.thread.SafeClassManager;
import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.deserialization.compressed.CompressedDeserializer;
import org.omnimc.lumina.mcmap.McMap;
import trix.TrixRemapper;
import trix.hierarchy.HierarchyChange;
import trix.hierarchy.HierarchyManager;

import java.io.File;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public class HierarchyProvider {

    private final TrixRemapper trixRemapper;

    private final File minecraftJar;

    private HierarchyManager hierarchyManager;

    private Mappings mappings;

    public HierarchyProvider(File minecraftJar, Mappings mappings) {
        this.minecraftJar = minecraftJar;
        this.mappings = mappings;
        this.trixRemapper = new TrixRemapper(mappings);
    }

    public void init() {
        HierarchyManager hierarchyManager = new HierarchyManager();
        System.out.println(mappings.getClasses().size());

        SafeClassManager classManager = new SafeClassManager();
        classManager.readJarFile(minecraftJar);
        classManager.applyChanges(new HierarchyChange(hierarchyManager, trixRemapper));
        classManager.close();
        hierarchyManager.populateClassFiles();

        System.out.println(hierarchyManager.getMappings().getClasses().size());
        this.hierarchyManager = hierarchyManager;
    }

    public McMap write() {
        CompressedDeserializer deserializer = CompressedDeserializer.getInstance();

        return deserializer.deserialize(
                new File("C:\\Users\\CryroByte\\Desktop\\omnimc-project\\Universal-Mappings\\mappings\\hierarchy\\hierarchy.mcmap"), hierarchyManager.getMappings(), McMap.Version.V2);
    }

    public Mappings getMappings() {
        return mappings;
    }

}