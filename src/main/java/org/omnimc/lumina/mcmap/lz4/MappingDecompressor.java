package org.omnimc.lumina.mcmap.lz4;

import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
 */
public final class MappingDecompressor {

    private static final LZ4FastDecompressor SHARED_LZ4DECOMPRESSOR = LZ4Factory.fastestInstance().fastDecompressor();

    public MappingDecompressor() {
    }

    public byte[] decompress(byte[] compressed, int decompressedLength) {
        byte[] decompressed = new byte[decompressedLength];
        SHARED_LZ4DECOMPRESSOR.decompress(compressed, 0, decompressed, 0 , decompressedLength);
        return decompressed;
    }
}