package com.swisscom.userregister.domain.enums;

public enum RoleEnum {
    ADMIN("A"), COMMON("C");

    private final String key;

    RoleEnum(String key) {
        this.key = key;
    }

    public static RoleEnum getEnumValue(String key) {
        if (COMMON.key.equals(key)) {
            return COMMON;
        }
        return ADMIN;
    }

    public static String getKey(RoleEnum roleEnum) {
        if (COMMON.equals(roleEnum)) {
            return COMMON.key;
        }
        return ADMIN.key;
    }
}
