package co.moviired.acquisition.common.util;

import org.springframework.http.HttpStatus;

public final class StatusCodesHelper {

    public static final String SUCCESS_CODE_0 = "0";
    public static final String SUCCESS_CODE_00 = "00";

    public static final String SUCCESS_CODE = String.valueOf(HttpStatus.OK.value());
    public static final String SUCCESS_CODE_201 = String.valueOf(HttpStatus.CREATED.value());

    public static final String BAD_REQUEST_CODE = String.valueOf(HttpStatus.BAD_REQUEST.value());
    public static final String AUTHORIZATION_HEADER_INVALID_CODE = String.valueOf(HttpStatus.UNAUTHORIZED.value());
    public static final String REGISTER_ALTERED_CODE = String.valueOf(HttpStatus.FORBIDDEN.value());
    public static final String NOT_FOUND_CODE = String.valueOf(HttpStatus.NOT_FOUND.value());
    public static final String METHOD_IS_NOT_ENABLED_CODE = String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value());
    public static final String SERVER_ERROR_CODE = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());

    public static final String INSUFFICIENT_BALANCE_CODE = "901";
    public static final String UNAUTHORIZED = "94";
    public static final String METHOD_NOT_FOUND = "95";
    public static final String ERROR = "99";

    private StatusCodesHelper() {
        // Not is necessary this implementation
    }
}

