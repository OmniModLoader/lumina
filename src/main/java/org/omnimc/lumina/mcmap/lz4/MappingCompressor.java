package org.omnimc.lumina.mcmap.lz4;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import org.omnimc.lumina.data.deserialization.compressed.CompressedDeserializer;
import org.omnimc.lumina.data.types.ClassData;

import java.nio.charset.StandardCharsets;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 2.0.0
 */
public final class MappingCompressor {

    private static final LZ4Compressor LZ_4_COMPRESSOR = LZ4Factory.fastestInstance().fastCompressor();
    private static final CompressedDeserializer DESERIALIZER = CompressedDeserializer.getInstance();

    private final ClassData classData;

    private final String fields;
    private final String methods;

    public MappingCompressor(ClassData classData) {
        this.classData = classData;

        this.fields = DESERIALIZER.deserializeFieldData(classData.getFields()) + DESERIALIZER.deserializeFieldData(classData.getPrivateFields());
        this.methods = DESERIALIZER.deserializeMethodData(classData.getMethods()) + DESERIALIZER.deserializeMethodData(classData.getPrivateMethods());
    }

    public byte[] getCompressedFields() {
        return compress(fields);
    }

    public byte[] getCompressedMethods() {
        return compress(methods);
    }

    public byte[] getAllDataCompressed() {
        return compress(DESERIALIZER.deserializeClassData(classData));
    }

    public int getUncompressedFieldLength() {
        return fields.length();
    }

    public int getUncompressedMethodLength() {
        return methods.length();
    }

    public int getAllDataUncompressedLength() {
        return getUncompressedFieldLength() + getUncompressedMethodLength();
    }

    private byte[] compress(CharSequence data) {
        return LZ_4_COMPRESSOR.compress(data.toString().getBytes(StandardCharsets.UTF_8));
    }
}