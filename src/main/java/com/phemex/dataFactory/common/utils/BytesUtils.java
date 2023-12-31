package com.phemex.dataFactory.common.utils;

public class BytesUtils {
    public BytesUtils() {
    }

    public static byte[] long2LittleEndian(long l) {
        byte[] result = new byte[8];

        for(int i = 0; i < 8; ++i) {
            result[i] = (byte)((int)(l & 255L));
            l >>= 8;
        }

        return result;
    }

    public static long littleEndian2Long(byte[] b) {
        long res = 0L;

        for(int i = 7; i >= 0; --i) {
            res <<= 8;
            res |= b[i] & 255;
        }

        return res;
    }
}
