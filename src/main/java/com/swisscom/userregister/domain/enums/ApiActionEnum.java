package com.swisscom.userregister.domain.enums;

public enum ApiActionEnum {
    WRITE("write"), READ("read");

    private final String key;

    ApiActionEnum(String key) {
        this.key = key;
    }

    public String getKeyValue() {
        return this.key;
    }
}
