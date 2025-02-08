package org.voidmirror.voicechat.misc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Slf4j
public class ByteUtils {
    private static final ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
    public static byte[] longToBytes(long l) {
        try {
            byteBuffer.putLong(l);
        } catch (BufferOverflowException e) {
            log.error("ByteBuffer overflow exception: {}", e.getMessage());
            byteBuffer.clear();
            return new byte[1024];
        }
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
            byteBuffer.clear();
        }
        return System.currentTimeMillis();
    }
}
