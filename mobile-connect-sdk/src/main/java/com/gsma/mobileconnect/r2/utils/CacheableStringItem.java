package com.gsma.mobileconnect.r2.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gsma.mobileconnect.r2.cache.AbstractCacheable;

public class CacheableStringItem extends AbstractCacheable {

    @JsonProperty("nonce")
    private String nonce;

    @JsonProperty("sdkSession")
    private String sdkSession;

    public String getNonce() {
        return nonce;
    }

    public String getSdkSession() {
        return sdkSession;
    }

    public CacheableStringItem (String sdkSession, String nonce) {
        this.sdkSession = sdkSession;
        this.nonce = nonce;
    }

    public CacheableStringItem () {
        super();
    }
}