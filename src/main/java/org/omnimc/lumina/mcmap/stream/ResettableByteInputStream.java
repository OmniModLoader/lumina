package org.omnimc.lumina.mcmap.stream;

import java.io.ByteArrayInputStream;

/**
 * @author <a href=https://github.com/CadenCCC>Caden</a>
 * @since 1.0.0
 */
public class ResettableByteInputStream extends ByteArrayInputStream {

    public ResettableByteInputStream(byte[] buf) {
        super(buf);
    }

    public ResettableByteInputStream() {
        super(new byte[0]);
    }

    /**
     * Reset the stream to use a new buffer.
     *
     * @param buf the new byte buffer
     */
    public void reset(byte[] buf) {
        reset(buf, 0, buf.length);
    }

    /**
     * Reset the stream to use a portion of a new buffer.
     *
     * @param buf the byte buffer
     * @param offset the start offset in the buffer
     * @param length the number of bytes to read
     */
    public void reset(byte[] buf, int offset, int length) {
        this.buf = buf;
        this.pos = offset;
        this.mark = offset;
        this.count = Math.min(offset + length, buf.length);
    }
}