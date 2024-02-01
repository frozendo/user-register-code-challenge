package com.swisscom.userregister.domain.convert;

import com.swisscom.userregister.domain.entity.AuthorizationLog;
import com.swisscom.userregister.domain.enums.AuthorizationResultEnum;
import org.json.JSONObject;

public class AuthorizationResultEnumConverter {

    private AuthorizationResultEnumConverter() {}

    public static AuthorizationLog convertLogAuthorizationRequest(JSONObject body) {
        var inputObject = body.getJSONObject("input");
        var result = body.getBoolean("result");

        return new AuthorizationLog(
                body.getString("decision_id"),
                body.getString("path"),
                inputObject.getString("action"),
                inputObject.getString("email"),
                AuthorizationResultEnum.getByBoolean(result)
        );
    }

}
