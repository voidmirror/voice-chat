package org.voidmirror.voicechat.misc;

import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtils {
    private static final ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
    public static byte[] longToBytes(long l) {
        byteBuffer.putLong(l);
        byte[] bytes = byteBuffer.array();
        byteBuffer.clear();
        return bytes;
    }
    public static long bytesToLong(byte[] bytes) {
        try {
            byteBuffer.put(bytes, 0, Long.BYTES); // get first 'long' bytes
            byteBuffer.flip();
            long res = byteBuffer.getLong();
            byteBuffer.clear();
            return res;
        } catch (Exception e) {
            System.out.println(Arrays.toString(bytes));
        }
        return System.currentTimeMillis();
    }
}
