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

    // Works ~6 times longer than vice-versa
    public static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong(); // works faster than explicit algorithm during on 2000000000 operations
    }
}
