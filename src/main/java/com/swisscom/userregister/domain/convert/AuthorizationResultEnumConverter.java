package com.swisscom.userregister.domain.convert;

import com.swisscom.userregister.domain.entity.AuthorizationLog;
import com.swisscom.userregister.domain.enums.AuthorizationResultEnum;
import org.json.JSONObject;

public class AuthorizationResultEnumConverter {

    private static final String DECISION_ID_FIELD = "decision_id";
    private static final String PATH_FIELD = "path";
    private static final String ACTION_FIELD = "action";
    private static final String TOKEN_FIELD = "token";
    private static final String RESULT_FIELD = "result";
    private static final String INPUT_FILED = "input";

    private AuthorizationResultEnumConverter() {}

    public static AuthorizationLog convertLogAuthorizationRequest(JSONObject body) {
        var inputObject = body.getJSONObject(INPUT_FILED);
        var result = body.getBoolean(RESULT_FIELD);

        return new AuthorizationLog(
                body.getString(DECISION_ID_FIELD),
                body.getString(PATH_FIELD),
                inputObject.getString(ACTION_FIELD),
                inputObject.getString(TOKEN_FIELD),
                AuthorizationResultEnum.getByBoolean(result)
        );
    }

}
