package org.devzen.nio_json;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * TODO: 这里要写注释的!
 */
public class JsonHelper {
    /**
     * 提取json块
     *
     * @param buf 缓冲区
     * @param out 输出
     * @throws UnsupportedEncodingException
     */
    public static void extractJsonBlocks(ByteBuf buf, List<Object> out) throws UnsupportedEncodingException {
        // 总共要处理的字符数量
        int readable = buf.readableBytes();
        // 括号深度
        int braceDepth = 0;
        // 偏移值, 每找到一个完整的json快, 偏移量更新一次.
        int offset = 0;
        // 当前字符是否在字符串类型的值里面
        boolean inStr = false;
        // 用来保存数据的临时缓存区
        byte[] data = new byte[readable];
        // 循环所有数据
        for (int i = 0; i < readable; i++) {
            // 读取一个
            byte b = buf.readByte();
            // 放入临时缓冲区, 注意需要减去偏移量, 也就是如果前面找到一个json块, 就从头开始放数据
            data[i - offset] = b;
            if (b == '{' && !inStr) {
                // 遇到括号, 并且不在字符串类型的值里面
                braceDepth++;
            } else if (b == '}' && !inStr) {
                // 遇到括号, 并且不在字符串类型的值里面
                if (braceDepth == 1) {
                    // 如果当前深度是1, 说明找到一个完整的块了
                    out.add(new String(data, "utf-8").trim());
                    // 新建临时缓冲区
                    data = new byte[readable - offset];
                    // 偏移量更新
                    offset = i;
                    // 重置括号深度
                    braceDepth = 0;
                } else {
                    braceDepth--;
                }
            } else if (b == '"') {
                // 看前一个字符是否为转义符
                byte prev = i == 0 ? 0 : data[i - 1 - offset];
                if (prev != '\\') {
                    // 不是转移符的情况才算做引号
                    inStr = !inStr;
                }
            }

        }
        // 有一部分数据是没有用到的, 需要把读索引重置回去
        buf.readerIndex(offset == 0 ? offset : offset + 1);
        buf.discardReadBytes();
    }
}
