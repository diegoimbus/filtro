package co.moviired.auth.server.helper;

public final class ConstantsHelper {

    public static final String REGISTER_PROPERTIES_PREFIX = "providers.register";

    public static final String LOG_THIRD_REQUEST = "[{}] INVOKE {}: {URL: '{}', body : '{}'}";
    public static final String REGISTER_COMPONENT = "Register";
    public static final String LBL_RESPONSE_REGISTER = "[{}] {} {} RESPONSE - Value [{}]";
    public static final String LBL_ERROR_REGISTER = "[{}] {} {} ERROR RESPONSE - VALUE  [{}]";
    public static final String REGISTER_API = "registerApi";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TWO_DOTS = ":";

    private ConstantsHelper() {
        //Not is necessary this implementation
    }
}

