package org.omnimc.lumina.util;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
 */
public final class LittleEndian {

    public static int INT4_BYTE = 4;

    public static void writeShortLE(RandomAccessFile raf, short value) throws IOException {
        raf.writeByte(value & 0xFF);
        raf.writeByte((value >>> 8) & 0xFF);
    }

    public static void writeIntLE(RandomAccessFile raf, int value) throws IOException {
        raf.writeByte(value & 0xFF);
        raf.writeByte((value >>> 8) & 0xFF);
        raf.writeByte((value >>> 16) & 0xFF);
        raf.writeByte((value >>> 24) & 0xFF);
    }

    public static void writeLongLE(RandomAccessFile raf, long value) throws IOException {
        raf.writeByte((int) (value & 0xFF));
        raf.writeByte((int) ((value >>> 8) & 0xFF));
        raf.writeByte((int) ((value >>> 16) & 0xFF));
        raf.writeByte((int) ((value >>> 24) & 0xFF));
        raf.writeByte((int) ((value >>> 32) & 0xFF));
        raf.writeByte((int) ((value >>> 40) & 0xFF));
        raf.writeByte((int) ((value >>> 48) & 0xFF));
        raf.writeByte((int) ((value >>> 56) & 0xFF));
    }

    public static void writeBytes(RandomAccessFile raf, byte[] data) throws IOException {
        raf.write(data);
    }

    public static long toLong5LE(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFFL)) |
                ((bytes[offset + 1] & 0xFFL) << 8) |
                ((bytes[offset + 2] & 0xFFL) << 16) |
                ((bytes[offset + 3] & 0xFFL) << 24) |
                ((bytes[offset + 4] & 0xFFL) << 32);
    }

    public static int toInt4LE(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF) |
                ((bytes[offset + 1] & 0xFF) << 8) |
                ((bytes[offset + 2] & 0xFF) << 16) |
                ((bytes[offset + 3] & 0xFF) << 24);
    }

    public static short toShort2(byte[] bytes, int offset) {
        return (short) (((bytes[offset] & 0xFF)) |
                ((bytes[offset + 1] & 0xFF) << 8));
    }

    public static short readShort2(RandomAccessFile raf, byte[] bytes, int offset) throws IOException {
        int bytesRead = raf.read(bytes);
        if (bytesRead != 2) {
            return -1;
        }
        return LittleEndian.toShort2(bytes, offset);
    }

    public static int readInt4(RandomAccessFile raf, byte[] bytes, int offset) throws IOException {
        int bytesRead = raf.read(bytes);
        if (bytesRead != INT4_BYTE) {
            return -1;
        }
        return LittleEndian.toInt4LE(bytes, offset);
    }
}