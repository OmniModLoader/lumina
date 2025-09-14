package org.omnimc.lumina.mcmap.file;

import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.types.ClassData;
import org.omnimc.lumina.mcmap.lz4.MappingCompressor;
import org.omnimc.lumina.mcmap.McMap;
import org.omnimc.lumina.mcmap.McMap.Version;
import org.omnimc.lumina.data.serialization.LineSerializer;
import org.omnimc.lumina.util.LittleEndian;
import org.omnimc.lumina.util.SerializationHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
 */
public final class FileFactory {

    private final List<EntryBlock> entries = new ArrayList<>();

    private final File location;
    private final Version version;

    public FileFactory(InputStream stream, File location, LineSerializer serializer, Version version) {
        this.location = location;
        this.version = version;

        Mappings mappings = new Mappings();
        Mappings populatedMappings = SerializationHelper.getMappingsFromInputStream(stream, serializer, mappings);
        populateEntries(populatedMappings);
    }

    public FileFactory(Mappings mappings, File location, Version version) {
        this.location = location;
        this.version = version;

        populateEntries(mappings);
    }

    public void writeFile() {
        if (location.exists() && !location.delete()) {
            throw new RuntimeException("Failed to delete existing file: " + location);
        }

        try {
            if (!location.createNewFile()) {
                throw new IOException("Failed to create new file: " + location);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize file: " + location, e);
        }

        try (RandomAccessFile raf = new RandomAccessFile(location, "rw")) {
            raf.setLength(0);
            raf.write(McMap.TOF_MAGIC_AS_BYTES);
            raf.write(version.getVersionByte());
            LittleEndian.writeIntLE(raf, entries.size());

            for (EntryBlock entry : entries) {
                entry.writeTo(raf);
            }
            for (EntryBlock entry : entries) {
                entry.writeData(raf);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + location, e);
        }
    }

    public McMap finish() throws IOException {
        return new McMap(location);
    }

    private void populateEntries(Mappings mappings) {
        for (Map.Entry<String, ClassData> entry : mappings.getClasses().entrySet()) {
            String unmapped = entry.getKey();
            ClassData classData = entry.getValue();

            MappingCompressor compressor = new MappingCompressor(classData);
            EntryBlock block = new EntryBlock(unmapped, classData.getClassName(), version);

            if (version == Version.V1) {
                block.setFieldCompressedData(compressor.getAllDataCompressed());
                block.setUncompressedFieldLength(compressor.getAllDataUncompressedLength());
            } else if (version == Version.V2) {
                block.setFieldCompressedData(compressor.getCompressedFields());
                block.setMethodCompressedData(compressor.getCompressedMethods());
                block.setUncompressedFieldLength(compressor.getUncompressedFieldLength());
                block.setUncompressedMethodLength(compressor.getUncompressedMethodLength());
            } else {
                throw new UnsupportedOperationException("Unsupported version: " + version);
            }
            entries.add(block);
        }
    }

}