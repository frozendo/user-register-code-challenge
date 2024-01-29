package com.swisscom.userregister.domain.convert;

import com.swisscom.userregister.domain.enums.RoleEnum;
import jakarta.persistence.AttributeConverter;

public class UserRoleConvert implements AttributeConverter<RoleEnum, String> {

    @Override
    public String convertToDatabaseColumn(RoleEnum attribute) {
        return RoleEnum.getKey(attribute);
    }

    @Override
    public RoleEnum convertToEntityAttribute(String dbData) {
        return RoleEnum.getEnumValue(dbData);
    }

}
