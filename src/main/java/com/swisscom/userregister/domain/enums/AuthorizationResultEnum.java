package com.swisscom.userregister.domain.enums;

public enum AuthorizationResultEnum {
    SUCCESS("S"), FAIL("F");

    private final String key;

    AuthorizationResultEnum(String key) {
        this.key = key;
    }

    public static AuthorizationResultEnum getEnumValue(String key) {
        if (FAIL.key.equals(key)) {
            return FAIL;
        }
        return SUCCESS;
    }

    public static String getKey(AuthorizationResultEnum roleEnum) {
        if (FAIL.equals(roleEnum)) {
            return FAIL.key;
        }
        return SUCCESS.key;
    }

    public static AuthorizationResultEnum getByBoolean(boolean result) {
        if (result) {
            return SUCCESS;
        }
        return FAIL;
    }

}
