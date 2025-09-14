package org.omnimc.lumina.mcmap.file;

import org.omnimc.lumina.mcmap.McMap;
import org.omnimc.lumina.mcmap.McMap.Version;
import org.omnimc.lumina.util.LittleEndian;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
 */
public class EntryBlock {

    private final String unMappedName;
    private final String mappedName;
    private final Version version;

    private byte[] fieldCompressedData;
    private int uncompressedFieldLength;
    private byte[] methodCompressedData;
    private int uncompressedMethodLength;

    private long fieldOffsetPos = -1;
    private long methodOffsetPos = -1;

    public EntryBlock(String unMappedName, String mappedName, Version version) {
        this.unMappedName = unMappedName;
        this.mappedName = mappedName;
        this.version = version;
    }

    public void setFieldCompressedData(byte[] fieldCompressedData) {
        this.fieldCompressedData = fieldCompressedData;
    }

    public void setMethodCompressedData(byte[] methodCompressedData) {
        this.methodCompressedData = methodCompressedData;
    }

    public void setUncompressedFieldLength(int uncompressedFieldLength) {
        this.uncompressedFieldLength = uncompressedFieldLength;
    }

    public void setUncompressedMethodLength(int uncompressedMethodLength) {
        this.uncompressedMethodLength = uncompressedMethodLength;
    }

    public void writeTo(RandomAccessFile raf) throws IOException {
        LittleEndian.writeShortLE(raf, McMap.ENTRY_BLOCK_MAGIC);

        if (mappedName.length() > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Mapped name too long: " + mappedName.length());
        }
        LittleEndian.writeShortLE(raf, (short) mappedName.length());

        if (unMappedName.length() > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Unmapped name too long: " + unMappedName.length());
        }
        LittleEndian.writeShortLE(raf, (short) unMappedName.length());

        switch (version) {
            case V1 -> {
                LittleEndian.writeIntLE(raf, fieldCompressedData.length);
                LittleEndian.writeIntLE(raf, uncompressedFieldLength);
                fieldOffsetPos = raf.getFilePointer();
                LittleEndian.writeIntLE(raf, 0);
            }
            case V2 -> {
                LittleEndian.writeIntLE(raf, fieldCompressedData.length);
                LittleEndian.writeIntLE(raf, methodCompressedData.length);
                LittleEndian.writeIntLE(raf, uncompressedFieldLength);
                LittleEndian.writeIntLE(raf, uncompressedMethodLength);

                fieldOffsetPos = raf.getFilePointer();
                LittleEndian.writeIntLE(raf, 0);

                methodOffsetPos = raf.getFilePointer();
                LittleEndian.writeIntLE(raf, 0);
            }
            default -> throw new IllegalArgumentException("Unsupported version: " + version);
        }

        raf.write(mappedName.getBytes(StandardCharsets.UTF_8));
        raf.write(unMappedName.getBytes(StandardCharsets.UTF_8));
    }

    public void writeData(RandomAccessFile raf) throws IOException {
        int fieldOffset;

        switch (version) {
            case V1 -> {
                fieldOffset = (int) raf.getFilePointer();
                raf.write(fieldCompressedData);

                raf.seek(fieldOffsetPos);
                LittleEndian.writeIntLE(raf, fieldOffset);

                raf.seek(fieldOffset + fieldCompressedData.length);
            }
            case V2 -> {
                fieldOffset = (int) raf.getFilePointer();
                raf.write(fieldCompressedData);

                int methodOffset = (int) raf.getFilePointer();
                raf.write(methodCompressedData);

                raf.seek(fieldOffsetPos);
                LittleEndian.writeIntLE(raf, fieldOffset);

                raf.seek(methodOffsetPos);
                LittleEndian.writeIntLE(raf, methodOffset);

                raf.seek(methodOffset + methodCompressedData.length);
            }
            default -> throw new IllegalArgumentException("Unsupported version: " + version);
        }
    }

    @Override
    public String toString() {
        return "EntryBlock{" +
                "unMappedName='" + unMappedName + '\'' +
                ", mappedName='" + mappedName + '\'' +
                ", version=" + version +
                ", fieldCompressedData=" + Arrays.toString(fieldCompressedData) +
                ", uncompressedFieldLength=" + uncompressedFieldLength +
                ", methodCompressedData=" + Arrays.toString(methodCompressedData) +
                ", uncompressedMethodLength=" + uncompressedMethodLength +
                ", fieldOffsetPos=" + fieldOffsetPos +
                ", methodOffsetPos=" + methodOffsetPos +
                '}';
    }
}