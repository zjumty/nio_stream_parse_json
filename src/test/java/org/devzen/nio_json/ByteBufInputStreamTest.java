package org.devzen.nio_json;

import com.alibaba.fastjson.JSONReader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

/**
 * TODO: 这里需要写注释
 */
public class ByteBufInputStreamTest {

    @Test
    public void testRead() throws IOException {
        ByteBufAllocator allocator = new PooledByteBufAllocator();
        ByteBuf buf = allocator.buffer();
        Assert.assertEquals(0, buf.readerIndex());
        Assert.assertEquals(0, buf.writerIndex());
        Assert.assertEquals(0, buf.readableBytes());

        InputStream in = ByteBufInputStreamTest.class.getResourceAsStream("/stream.json");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(in, baos);
        byte[] data = baos.toByteArray();

        buf.writeBytes(data);
        Assert.assertEquals(0, buf.readerIndex());
        Assert.assertEquals(data.length, buf.writerIndex());
        Assert.assertEquals(data.length, buf.readableBytes());

        ByteBufInputStream bbis = new ByteBufInputStream(buf);
        byte[] b = new byte[10];
        Assert.assertEquals(1, bbis.read(b));
        Assert.assertThat(b[0], is(greaterThan((byte) 0)));
        Assert.assertThat(b[1], is(equalTo((byte) 0)));
        Assert.assertThat(b[9], is(equalTo((byte) 0)));
        Assert.assertEquals(1, buf.readerIndex());
        Assert.assertEquals(1, bbis.getReadBytes());

        b = new byte[10];
        Assert.assertEquals(1, bbis.read(b, 2, 5));
        Assert.assertThat(b[2], is(greaterThan((byte) 0)));
        Assert.assertThat(b[1], is(equalTo((byte) 0)));
        Assert.assertThat(b[3], is(equalTo((byte) 0)));
        Assert.assertThat(b[9], is(equalTo((byte) 0)));
        Assert.assertEquals(2, buf.readerIndex());
        Assert.assertEquals(2, bbis.getReadBytes());

        buf.readerIndex(0);

        JSONReader reader = new JSONReader(new InputStreamReader(bbis, "utf-8"));
        List<User> users = new ArrayList<User>();
        int readBytes=0;
        try {
            reader.startObject();
            User user = new User();
            while (reader.hasNext()) {
                String key = reader.readString();
                if (key.equals("id")) {
                    user.setId(reader.readInteger());
                } else if (key.equals("name")) {
                    user.setName(reader.readString());
                } else if (key.equals("age")) {
                    user.setAge(reader.readInteger());
                    reader.endObject();
                    users.add(user);
                    user = new User();
                    readBytes = bbis.getReadBytes();
                    reader.startObject();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertEquals(2, users.size());
            Assert.assertThat(readBytes, is(lessThan(data.length)));
            Assert.assertEquals(data.length, buf.readerIndex());
            Assert.assertEquals(data.length, buf.writerIndex());
            buf.readerIndex(readBytes);

            byte[] left = new byte[1024];
            buf.readBytes(left, 0, buf.readableBytes());
            String leftStr = new String(left, "utf-8");
            System.out.println(leftStr);
        }
    }
}
