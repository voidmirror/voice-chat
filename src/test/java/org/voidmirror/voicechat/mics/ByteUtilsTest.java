package org.voidmirror.voicechat.mics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.voidmirror.voicechat.misc.ByteUtils;

public class ByteUtilsTest {

    @Test
    public void longToBytesTest() {
        Assertions.assertArrayEquals(new byte[]{-1, -1, -1, -1, -1, -1, -1, -128}, ByteUtils.longToBytes(-128L));
        Assertions.assertArrayEquals(new byte[]{0, 0, 0, 0, 0, 0, 4, 59}, ByteUtils.longToBytes(1083L));
    }

    @Test
    public void bytesToLongTest() {
        Assertions.assertEquals(15L, ByteUtils.bytesToLong(new byte[]{0, 0, 0, 0, 0, 0, 0, 15}));
        Assertions.assertEquals(15L, ByteUtils.bytesToLong(new byte[]{0, 0, 0, 0, 0, 0, 0, 15}));
        Assertions.assertEquals(1083L, ByteUtils.bytesToLong(new byte[]{0, 0, 0, 0, 0, 0, 4, 59}));
    }

}
