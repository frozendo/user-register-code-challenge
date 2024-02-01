package com.swisscom.userregister.domain.convert;

import com.swisscom.userregister.domain.enums.AuthorizationResultEnum;
import jakarta.persistence.AttributeConverter;

public class AuthorizationResultConvert implements AttributeConverter<AuthorizationResultEnum, String> {

    @Override
    public String convertToDatabaseColumn(AuthorizationResultEnum attribute) {
        return AuthorizationResultEnum.getKey(attribute);
    }

    @Override
    public AuthorizationResultEnum convertToEntityAttribute(String dbData) {
        return AuthorizationResultEnum.getEnumValue(dbData);
    }

}
