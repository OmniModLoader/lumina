package org.omnimc.lumina.mcmap;

import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.types.ClassData;
import org.omnimc.lumina.mcmap.file.FileFactory;
import org.omnimc.lumina.mcmap.lz4.MappingDecompressor;
import org.omnimc.lumina.data.serialization.LineSerializer;
import org.omnimc.lumina.data.serialization.compressed.CompressedLineSerializer;
import org.omnimc.lumina.mcmap.stream.ResettableByteInputStream;
import org.omnimc.lumina.mmp.Patch;
import org.omnimc.lumina.util.LittleEndian;
import org.omnimc.lumina.util.SerializationHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import static org.omnimc.lumina.util.LittleEndian.INT4_BYTE;

/**
 * This class is the implementation of our custom solution to store mappings.
 * <p>
 * We use a {@link RandomAccessFile} to randomly access the file and give some performance benefits.
 * </p>
 *
 * <p>
 * An McMap file consists of 2 blocks of information, we have the TOF (Top of File) and the Entry Block.
 * The structure look like this:
 *
 * <pre><code>
 * // TOF (Top of File Index)
 * (0x50414D434DL) { // Total of 10 bytes big
 * 	0x00 | 5 | Magic
 * 	0x05 | 1 | Version
 * 	0x06 | 4 | EntryTableCount // This will count down from the TOF Header
 * }
 *
 * EntryBlock
 * v1 {
 * 	(0xD2E1) { // Total of 18 + (n + m) bytes big
 * 		0x00 | 2 | Magic
 * 		0x02 | 2 | MappedNameLength (n)
 * 		0x04 | 2 | UnmappedNameLength (m)
 * 		0x06 | 4 | CompressedLength
 * 		0x0A | 4 | UncompressedLength
 * 		0x0E | 4 | DataOffset
 * 		0x12 | n | MappedName
 * 		0x12 + n | m | UnmappedName
 *   }
 * }
 * v2 {
 * 	(0xD2E1) { // Total of 30 + (n + m) bytes big
 * 		0x00 | 2 | Magic
 * 		0x02 | 2 | MappedNameLength (n)
 * 		0x04 | 2 | UnmappedNameLength (m)
 * 		0x06 | 4 | FieldCompressedLength
 * 		0x0A | 4 | MethodCompressedLength
 * 		0x0E | 4 | FieldUncompressedLength
 * 		0x12 | 4 | MethodUncompressedLength
 * 		0x16 | 4 | FieldOffset
 * 		0x1A | 4 | MethodOffset
 * 		0x1E | n | MappedName
 * 		0x1E + n | m | UnmappedName
 *    }
 * }
 * </code></pre>
 * <p>
 * Every <b>multibyte</b> value is put into {@link LittleEndian Little Endian} format.
 * </p>
 *
 * <p>
 * McMap files only use LZ4 compression when compressing their data. This is to ensure speed without compromising compression,
 * and supporting multiple compression mediums would be hard to keep track of while keeping the minimalist design it already has.
 * <p>
 * We try to keep performance in check, the way we do this is once an McMap file is created it will read all the {@code EntryBlocks} and map out the offset with the {@code mappedName} and {@code unmappedName}.
 * This creates a slow initialization however when searching it is blazingly fast because what's taking the most time is decompressing and parsing.
 * </p>
 *
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public final class McMap implements Closeable {

    /**
     * The version of the {@code EntryBlock}s provided by the instance of a {@code McMap} file.
     */
    public enum Version {
        /**
         * Version one contains all the data (methods and fields) in one big block of data. It is then all populated at the same time.
         */
        V1((byte) 0),
        /**
         * Version two contains 2 separate offsets for methods and fields. This allows you to choose what you want to populate at the time.
         */
        V2((byte) 1);

        final byte versionByte;

        Version(byte versionByte) {
            this.versionByte = versionByte;
        }

        public byte getVersionByte() {
            return versionByte;
        }

        public static Version fromByte(byte b) {
            for (Version v : values()) {
                if (v.versionByte == b) {
                    return v;
                }
            }
            throw new IllegalArgumentException("Unknown version byte: " + b);
        }
    }

    /**
     * Population is what we want to populate our {@link ClassData} with.
     * <p>
     * If we choose {@code FIELD}, we only populate the fields map. Vice versa for {@code METHOD}. {@code BOTH} is clearly populating both maps respectfully.
     */
    public enum Population {
        FIELD,
        METHOD,
        BOTH
    }

    public static final Version DEFAULT_VERSION = Version.V1;

    /**
     * TOF (Top Of File) Header magic is MCMAP but reversed due to {@link LittleEndian Little Endian}.
     */
    public static final long TOF_MAGIC = 0x50414D434DL; // PAMCM this is the reverse of MCMAP.
    public static final byte[] TOF_MAGIC_AS_BYTES = {0x4D, 0x43, 0x4D, 0x41, 0x50};

    /**
     * EntryBlock Header Magic once again reversed due to {@link LittleEndian Little Endian}.
     */
    public static final short ENTRY_BLOCK_MAGIC = (short) 0xD2E1;

    /**
     * The {@code create} method for making a {@link McMap}.
     * <p>
     * This method creates a new instance of {@link FileFactory}, and then it attempts to parse and populate a Mappings.
     * Then it will create the file using the mappings, and return the {@link McMap} instance connected to the file created.
     *
     * @param stream       The {@link InputStream} that will be parsed using {@link LineSerializer} and then populated into a {@link Mappings}.
     * @param saveLocation The file you want to save it too.
     * @param serializer   Is the {@link LineSerializer} that will be doing the parsing needed to populate a {@link Mappings}.
     * @param version      The {@link Version} of the {@code EntryBlocks} you wish the instance of the {@link McMap} file is to have.
     * @return The instance of {@link McMap} which you have created.
     */
    public static McMap create(InputStream stream, File saveLocation, LineSerializer serializer, Version version) {
        FileFactory factory = new FileFactory(stream, saveLocation, serializer, version);
        factory.writeFile();
        try {
            return factory.finish();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create an McMap file.", e);
        }
    }

    /**
     * The {@code create} method for making a {@link McMap}.
     * <p>
     * This method creates a new instance of {@link FileFactory}, and then it takes the {@link Mappings} and create an {@link McMap} file.
     *
     * @param mappings     The {@link Mappings} you want the {@link McMap} file to be populated with.
     * @param saveLocation The file you want to save it too.
     * @param version      The {@link Version} of the {@code EntryBlocks} you wish the instance of the {@link McMap} file is to have.
     * @return The instance of {@link McMap} which you have created.
     */
    public static McMap create(Mappings mappings, File saveLocation, Version version) {
        FileFactory factory = new FileFactory(mappings, saveLocation, version);
        factory.writeFile();
        try {
            return factory.finish();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create an McMap file.", e);
        }
    }

    /**
     * An overloaded method of {@link McMap#create(Mappings, File, Version)} that completes the {@code version} parameter.
     * <p>
     * The {@code version} parameter is set to {@link McMap#DEFAULT_VERSION}.
     *
     * @param mappings     The {@link Mappings} you want the {@link McMap} file to be populated with.
     * @param saveLocation The file you want to save it too.
     * @return The instance of {@link McMap} which you have created.
     */
    public static McMap create(Mappings mappings, File saveLocation) {
        return create(mappings, saveLocation, DEFAULT_VERSION);
    }

    /**
     * An overloaded method of {@link McMap#create(InputStream, File, LineSerializer, Version)} that completes the {@code version} parameter.
     * <p>
     * The {@code version} parameter is set to {@link McMap#DEFAULT_VERSION}.
     *
     * @param stream       The {@link InputStream} that will be parsed using {@link LineSerializer} and then populated into a {@link Mappings}.
     * @param saveLocation The file you want to save it too.
     * @param serializer   Is the {@link LineSerializer} that will be doing the parsing needed to populate a {@link Mappings}.
     * @return The instance of {@link McMap} which you have created.
     */
    public static McMap create(InputStream stream, File saveLocation, LineSerializer serializer) {
        return create(stream, saveLocation, serializer, DEFAULT_VERSION);
    }

    /**
     * An overloaded method of {@link McMap#create(InputStream, File, LineSerializer, Version)} that completes the {@code serializer} parameter.
     * <p>
     * The {@code serializer} parameter is set to {@link CompressedLineSerializer}.
     *
     * @param stream       The {@link InputStream} that will be parsed using {@link CompressedLineSerializer} and then populated into a {@link Mappings}.
     * @param saveLocation The file you want to save it too.
     * @param version      The {@link Version} of the {@code EntryBlocks} you wish the instance of the {@link McMap} file is to have.
     * @return The instance of {@link McMap} which you have created.
     */
    public static McMap create(InputStream stream, File saveLocation, Version version) {
        return create(stream, saveLocation, CompressedLineSerializer.getInstance(), version);
    }

    /**
     * An overloaded method of {@link McMap#create(InputStream, File, LineSerializer, Version)} that completes the {@code serializer} parameter and the {@code version} parameter.
     * <p>
     * The {@code serializer} parameter is set to {@link CompressedLineSerializer}, and the {code version} parameter is set to {@link McMap#DEFAULT_VERSION}.
     *
     * @param stream       The {@link InputStream} that will be parsed using {@link CompressedLineSerializer} and then populated into a {@link Mappings}.
     * @param saveLocation The file you want to save it too.
     * @return The instance of {@link McMap} which you have created.
     */
    public static McMap create(InputStream stream, File saveLocation) {
        return create(stream, saveLocation, CompressedLineSerializer.getInstance(), DEFAULT_VERSION);
    }

    /* All data collection */

    private final Map<String, MappingBlock> entryBlocksUnmapped = new HashMap<>();
    private final Map<String, MappingBlock> entryBlocksMapped = new HashMap<>();

    /* Byte and I/O fields */

    private final RandomAccessFile raf;
    private final ResettableByteInputStream resettableByteInputStream = new ResettableByteInputStream();

    private final byte[] shortBuf = new byte[2];
    private final byte[] intBuf = new byte[INT4_BYTE];

    /* All Mapping required fields */

    private final Mappings mappings = new Mappings();
    private final MappingDecompressor mappingDecompressor = new MappingDecompressor();
    private final LineSerializer lineSerializer = CompressedLineSerializer.getInstance();

    private Version version;
    private int amountOfEntries;
    private int amountOfEntriesLeft = amountOfEntries;

    /**
     * The constructor for {@link McMap}.
     * <p>
     * When called it takes a file (assumed to be a {@link McMap} file) and tries to read the TOF (Top Of File).
     * After finding the TOF, it will then parse it to get the {@link Version} number and the amount of entries there are.
     * <p>
     * Due to the way {@link McMap} files are, the entries are right AFTER the TOF. This is done to make locating the first entry block offset as fast and as easy as possible.
     * <p>
     * After getting all of that information it then goes through and caches the offset and the unmapped and mapped name of each {@code EntryBlock}.
     *
     * @param mcmapFile The file assumed to be in the {@link McMap} file format.
     * @throws IOException A reading error from the {@link RandomAccessFile}.
     */
    public McMap(File mcmapFile) throws IOException {
        this.raf = new RandomAccessFile(mcmapFile, "r");

        cacheAllEntries(getFirstEntryBlockOffset());
    }

    public McMap applyPatch(Patch patch) {
        throw new UnsupportedOperationException("McMap does not support applyPatch right now.");
    }

    public void loadAllClassData() {
        for (Map.Entry<String, MappingBlock> unmappedEntry : entryBlocksUnmapped.entrySet()) {
            try {
                getClassData(unmappedEntry.getKey());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ClassData getClassData(String className) throws IOException {
        return getClassData(className, String::equals);
    }

    public ClassData getClassData(String className, Population population) throws IOException {
        return getClassData(className, String::equals, version == Version.V1 ? null : population);
    }

    public ClassData getClassData(String name, BiPredicate<String, String> filter) throws IOException {
        return getClassData(name, filter, version == Version.V1 ? null : Population.BOTH);
    }

    public ClassData getClassData(String obfuscatedName, BiPredicate<String, String> filter, Population population) throws IOException {
        if (version == Version.V2 && population == null) {
            throw new IllegalArgumentException("Population cannot be null with version: " + version);
        }

        ClassData possibleClassData = mappings.getClass(obfuscatedName);
        if (possibleClassData != null) {
            return possibleClassData;
        }

        MappingBlock block = getEntryBlockOffset(obfuscatedName, filter);
        if (block == null) {
            return null;
            //throw new FileNotFoundException(obfuscatedName);
        }

        raf.seek(block.getOffset());

        raf.skipBytes(INT4_BYTE); // Skipping the unmappedNameLength and mappedNameLength.

        int compressedFL = -1, uncompressedFL = -1, fOffset = -1;
        int compressedML = -1, uncompressedML = -1, mOffset = -1;

        if (version.equals(Version.V1)) {
            compressedFL = LittleEndian.readInt4(raf, intBuf, 0);
            uncompressedFL = LittleEndian.readInt4(raf, intBuf, 0);
            fOffset = LittleEndian.readInt4(raf, intBuf, 0);
        } else {
            switch (population) {
                case FIELD -> {
                    compressedFL = LittleEndian.readInt4(raf, intBuf, 0);
                    raf.skipBytes(INT4_BYTE);
                    uncompressedFL = LittleEndian.readInt4(raf, intBuf, 0);
                    raf.skipBytes(INT4_BYTE);
                    fOffset = LittleEndian.readInt4(raf, intBuf, 0);
                    raf.skipBytes(INT4_BYTE);
                }
                case METHOD -> {
                    raf.skipBytes(INT4_BYTE);
                    compressedML = LittleEndian.readInt4(raf, intBuf, 0);
                    raf.skipBytes(INT4_BYTE);
                    uncompressedML = LittleEndian.readInt4(raf, intBuf, 0);
                    raf.skipBytes(INT4_BYTE);
                    mOffset = LittleEndian.readInt4(raf, intBuf, 0);
                }
                case BOTH -> {
                    compressedFL = LittleEndian.readInt4(raf, intBuf, 0);
                    compressedML = LittleEndian.readInt4(raf, intBuf, 0);
                    uncompressedFL = LittleEndian.readInt4(raf, intBuf, 0);
                    uncompressedML = LittleEndian.readInt4(raf, intBuf, 0);
                    fOffset = LittleEndian.readInt4(raf, intBuf, 0);
                    mOffset = LittleEndian.readInt4(raf, intBuf, 0);
                }
            }
        }

        ClassData classData = createClassData(block.getMappedClassName(),
                                              fOffset, compressedFL, uncompressedFL,
                                              mOffset, compressedML, uncompressedML);

        mappings.addClass(obfuscatedName, classData);

        return classData;
    }

    private void cacheAllEntries(long offset) throws IOException {
        if (raf.getFilePointer() != offset) {
            raf.seek(offset);
        }

        for (int i = 0; i <= amountOfEntries; i++) {
            if (amountOfEntriesLeft-- <= 0) {
                break;
            }

            short possibleMagic = LittleEndian.readShort2(raf, shortBuf, 0);
            if (possibleMagic != ENTRY_BLOCK_MAGIC) {
                throw new RuntimeException("Invalid magic number: " + possibleMagic);
            }

            long currentOffset = raf.getFilePointer();

            short mappedNameLength = LittleEndian.readShort2(raf, shortBuf, 0);
            short unmappedNameLength = LittleEndian.readShort2(raf, shortBuf, 0);

            // These are the byte amounts that are AFTER unmappedNameLength
            int skipToNames = version == Version.V1 ? 12 : 24;
            raf.skipBytes(skipToNames);

            byte[] mappedNameBuffer = new byte[mappedNameLength];
            raf.read(mappedNameBuffer);
            String mappedName = new String(mappedNameBuffer, StandardCharsets.UTF_8);

            byte[] unmappedNameBuffer = new byte[unmappedNameLength];
            raf.read(unmappedNameBuffer);
            String unmappedName = new String(unmappedNameBuffer, StandardCharsets.UTF_8);

            MappingBlock value = new MappingBlock(currentOffset, mappedName, unmappedName);
            entryBlocksUnmapped.put(unmappedName, value);
            entryBlocksMapped.put(mappedName, value);
        }
    }

    private ClassData createClassData(String mappedName,
                                      long fieldOffset, int compressedFieldLength, int uncompressedFieldLength,
                                      long methodOffset, int compressedMethodLength, int uncompressedMethodLength) throws IOException {
        if (fieldOffset == -1 && methodOffset == -1) {
            return null;
        }

        ClassData classData = new ClassData(mappedName);

        if (version == Version.V1) {
            byte[] data = getData(fieldOffset, compressedFieldLength, uncompressedFieldLength);
            resettableByteInputStream.reset(data);
            return SerializationHelper.populateClassData(resettableByteInputStream, lineSerializer, classData);
        }

        if (fieldOffset != -1) {
            byte[] data = getData(fieldOffset, compressedFieldLength, uncompressedFieldLength);

            if (data.length > 0) {
                resettableByteInputStream.reset(data);
                SerializationHelper.populateClassDataField(resettableByteInputStream, lineSerializer, classData);
            }
        }

        if (methodOffset != -1) {
            byte[] data = getData(methodOffset, compressedMethodLength, uncompressedMethodLength);

            if (data.length > 0) {
                resettableByteInputStream.reset(data);
                SerializationHelper.populateClassDataMethod(resettableByteInputStream, lineSerializer, classData);
            }
        }

        return classData;
    }

    private byte[] getData(long offset, int compressedLength, int uncompressedLength) throws IOException {
        if (uncompressedLength == 0) {
            return new byte[0];
        }

        raf.seek(offset);

        byte[] compressedData = new byte[compressedLength];
        raf.readFully(compressedData, 0, compressedLength);

        return mappingDecompressor.decompress(compressedData, uncompressedLength);
    }

    private long getFirstEntryBlockOffset() throws IOException {
        byte[] magic = new byte[5];
        raf.read(magic);
        if (LittleEndian.toLong5LE(magic, 0) != TOF_MAGIC) {
            throw new IOException("Invalid TOF magic");
        }

        byte[] infoBuffer = new byte[5];
        raf.read(infoBuffer);

        this.version = Version.fromByte(infoBuffer[0]);
        this.amountOfEntries = LittleEndian.toInt4LE(infoBuffer, 1);
        this.amountOfEntriesLeft = amountOfEntries;
        return raf.getFilePointer();
    }

    private MappingBlock getEntryBlockOffset(String name, BiPredicate<String, String> filter) {
        MappingBlock block = entryBlocksUnmapped.get(name);
        if (block != null && filter.test(name, block.getUnmappedClassName())) {
            return block;
        }

        block = entryBlocksMapped.get(name);
        if (block != null && filter.test(name, block.getMappedClassName())) {
            return block;
        }

        return null;
    }

    public Version getVersion() {
        return version;
    }

    public Mappings getMappings() {
        return mappings;
    }

    @Override
    public void close() throws IOException {
        entryBlocksMapped.clear();
        entryBlocksUnmapped.clear();
        raf.close();
    }
}