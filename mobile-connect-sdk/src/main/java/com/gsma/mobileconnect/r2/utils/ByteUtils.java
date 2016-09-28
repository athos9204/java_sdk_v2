package com.gsma.mobileconnect.r2.utils;

/**
 * @since 2.0
 */
public final class ByteUtils
{
    public static byte[] addZeroPrefix(byte[] bytes)
    {
        byte[] updated = new byte[bytes.length + 1];
        updated[0] = (byte) 0;
        System.arraycopy(bytes, 0, updated, 1, bytes.length);
        return updated;
    }
}
