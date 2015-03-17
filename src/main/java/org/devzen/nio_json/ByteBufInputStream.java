package org.devzen.nio_json;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO: 这里需要写注释
 */
public class ByteBufInputStream extends InputStream {
    private ByteBuf buf;
    private int readBytes = 0;

    public ByteBufInputStream(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public int read() throws IOException {
        if (buf.readableBytes() > 0) {
            readBytes++;
            return buf.readByte();
        }
        return -1;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (b == null || b.length == 0) {
            return 0;
        } else if (buf.readableBytes() == 0) {
            return -1;
        } else {
            b[0] = buf.readByte();
            readBytes++;
            return 1;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null || b.length == 0 || len <= 0) {
            return 0;
        } else if (buf.readableBytes() == 0) {
            return -1;
        } else {
            b[off] = buf.readByte();
            readBytes++;
            return 1;
        }
    }

    public int getReadBytes() {
        return readBytes;
    }
}
