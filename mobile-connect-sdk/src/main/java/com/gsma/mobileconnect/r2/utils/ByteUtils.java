package com.gsma.mobileconnect.r2.utils;

/**
 * @since 2.0
 */
public final class ByteUtils
{
    /**
     * Private Constructor
     */
    ByteUtils()
    {
        /*
        Empty default constructor
        */
    }
    /**
     * Helper method to assure 0x00 byte at start of byte array
     *
     * @param bytes     byte array to prepend zero prefix
     * @return          byte array with zero prepended
     */
    public static byte[] addZeroPrefix(byte[] bytes)
    {
        byte[] updated = new byte[bytes.length + 1];
        updated[0] = (byte) 0;
        System.arraycopy(bytes, 0, updated, 1, bytes.length);
        return updated;
    }
}
