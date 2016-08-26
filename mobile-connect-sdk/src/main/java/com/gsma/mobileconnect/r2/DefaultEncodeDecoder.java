package com.gsma.mobileconnect.r2;

import com.gsma.mobileconnect.r2.utils.JsonWebTokens;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by usmaan.dad on 26/08/2016.
 */
public class DefaultEncodeDecoder implements JsonWebTokens.IMobileConnectEncodeDecoder {

    @Override
    public String encodeToBase64(byte[] value) {
        return Base64.encodeBase64String(value);
    }

    @Override
    public byte[] decodeFromBase64(String value) {
        return Base64.decodeBase64(value);
    }
}
