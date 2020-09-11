package co.moviired.support.endpoint.util.generics;

import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public final class Validation {

    private Validation() {
        super();
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNullOrEmpty(Object obj) {
        boolean result = Boolean.FALSE;
        if (obj == null) {
            result = Boolean.TRUE;
        } else if (obj instanceof String) {
            String objString = (String)obj;
            if (objString.trim().equals("")) {
                result = Boolean.TRUE;
            }
        } else if (obj instanceof String[]) {
            String[] objString = ((String[])obj);
            if (objString.length == 0) {
                result = Boolean.TRUE;
            }
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            if (list.isEmpty()) {
                result = Boolean.TRUE;
            }
        }

        return result;
    }

    public static boolean isNotEmpty(String value) {
        return isNotNull(value) && !value.isEmpty() && !value.equalsIgnoreCase("null");
    }

    public static void validateParam(String fieldName, String param, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        if (isNullOrEmpty(param)) {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORREQUIREDFIELD.getDescription(), fieldName);
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg);
        }
    }

    public static void valideParamInteger(String fieldName, String param, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        validateParam(fieldName, param, errorMessagesLoader);
        if (!isInteger(param)) {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORINTEGERFIELD.getDescription(), fieldName);
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg);
        }
    }

    public static boolean isInteger(String str) {
        return str == null ? Boolean.FALSE : str.matches("[0-9]+$");
    }

    public static void validateStringValue(String fieldName, String param, boolean ignoreCase, boolean requiredValueFlag, ErrorMessagesLoader errorMessagesLoader, String... validValues) throws BusinessException {
        if (requiredValueFlag) {
            validateParam(fieldName, param, errorMessagesLoader);
        }

        if (validValues != null && validValues.length > 0) {
            boolean valorEsperado = validateValidValues(param, ignoreCase, validValues);


            if (!valorEsperado) {
                StringBuilder descValues = new StringBuilder();

                for(int i = 0; i < validValues.length; ++i) {
                    descValues.append(validValues[i]);
                    if (i != validValues.length - 1) {
                        descValues.append(",");
                    }
                }
                String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORSTRINGFIELDVALUE.getDescription(), fieldName, descValues.toString());
                throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg, fieldName, descValues.toString());
            }
        }

    }

    public static boolean validateValidValues(String param, boolean ignoreCase, String... validValues){
        boolean valorEsperado = false;
        int i = validValues.length;

        for(int i$ = 0; i$ < i; ++i$) {
            String value = validValues[i$];
            if (!ignoreCase && value.equals(param) || ignoreCase && value.equalsIgnoreCase(param)) {
                valorEsperado = true;
                break;
            }
        }
        return valorEsperado;

    }

    public static void validateIntegerIgnoreEmpty(String fieldName, String param, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        if (isNotEmpty(param) && !isInteger(param)) {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORINTEGERFIELD.getDescription(), fieldName);
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg);
        }
    }

    public static void validateDateFormat(String fieldName, String param, String format, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            if (isNotEmpty(param)) {
                sdf.parse(param);
            }

        } catch (IllegalArgumentException var4) {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORINCORRECTFORMATDATE.getDescription(), fieldName, format);
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg, format);
        } catch (ParseException var5) {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORINCORRECTDATE.getDescription(), fieldName, format);
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg, fieldName, format);
        }
    }

    public static void validateDateFormatRequired(String fieldName, String param, String format, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        if (isNotEmpty(param)) {
            validateDateFormat(fieldName, param, format, errorMessagesLoader);
        } else {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORREQUIREDFIELD.getDescription(), fieldName);
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg);
        }
    }

    public static void validateRequiredFields(Object object, String[] requiredFields, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        Class targetClass = object.getClass();
        StringBuilder message = new StringBuilder();

        for (String nombre : requiredFields) {
            if (isNullOrEmpty(object, targetClass, nombre)) {
                message.append(nombre).append(",");
            }
        }

        if (!isNullOrEmpty(message.toString())) {
            message = new StringBuilder(message.substring(0, message.length() - 1));
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORREQUIREDALLFIELDS.getDescription(), message.toString());
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION.getCode(), CodeErrorEnum.ERRORREQUIREDALLFIELDS.getDescription(), msg, message.toString());
        }
    }

    private static boolean isNullOrEmpty(Object target, Class type, String name) {
        try {
            Method method = type.getMethod(getNameMethodGet(name));
            Object value = method.invoke(target);
            boolean result = Boolean.FALSE;
            if (value == null) {
                result = Boolean.TRUE;
            } else if (value instanceof String) {
                String objString = (String)value;
                if (objString.trim().equals("")) {
                    result = Boolean.TRUE;
                }
            } else if (value instanceof String[]) {
                String[] objString = ((String[])value);
                if (objString.length == 0) {
                    result = Boolean.TRUE;
                }
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                if (list.isEmpty()) {
                    result = Boolean.TRUE;
                }
            }

            return result;
        } catch (Exception var7) {
            return true;
        }
    }

    private static String getNameMethodGet(String property) {
        return "get" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
    }

    public static void validateRange(String fieldName, String number, int min, int max, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        if (!isNullOrEmpty(number) && (min > Integer.parseInt(number) || Integer.parseInt(number) > max)) {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_OUT_OF_RANGE.getDescription(), fieldName, Integer.toString(min), Integer.toString(max));
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg, fieldName, Integer.toString(min), Integer.toString(max));
        }
    }

    public static void validateMaxLength(String param, String value, Integer longitud, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        if (isNotEmpty(value) && value.length() > longitud) {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORMAXLENGTHNOTALLOWED.getDescription(), param, Integer.toString(longitud));
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg, param, Integer.toString(longitud));
        }
    }

    public static boolean isAlphabet(String str) {
        return str == null ? Boolean.FALSE : str.matches("([a-z]|[A-Z])+");
    }

    public static void validateAlphabetIgnoreEmpty(String fieldName, String param, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        if (isNotEmpty(param) && !isAlphabet(param)) {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORALPHABET.getDescription(), fieldName);
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg);
        }
    }

    public static void validateConsignmentChannel(String channel, String[] channelsAvailable, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {

        for (String c : channelsAvailable) {
            if (c.equals(channel)) {
                return;
            }
        }
        String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORWRONGCONSIGNMENTCHANNEL.getDescription());
        throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg);
    }

    public static void validateMinLength(String param, String value, Integer longitud, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        if (isNotEmpty(value) && value.length() < longitud) {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_MIN_LENGTH_NOT_ALLOWED.getDescription(), param, Integer.toString(longitud));
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg, param, Integer.toString(longitud));
        }
    }

    public static void validateLength(String param, String value, Integer min, Integer max, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        if (isNotEmpty(value)) {
            if (value.length() < min) {
                String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_MIN_LENGTH_NOT_ALLOWED.getDescription(), param, Integer.toString(min));
                throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg, param, Integer.toString(min));
            }

            if (value.length() > max) {
                throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, param, Integer.toString(max));
            }
        }

    }

    public static void validateStringValuesConfig(String nameParam, String value, String config, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        if (isNotEmpty(config)) {
            config = "," + config;
            validateStringValue(nameParam, value, false, false, errorMessagesLoader, config.concat(",").split(","));
        }

    }

    public static void valideLength(String param, String value, Integer longitud, ErrorMessagesLoader errorMessagesLoader) throws BusinessException {
        if (isNotEmpty(value) && value.trim().length() != longitud) {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORLENGTHINCORRECT.getDescription(), param, "" + longitud);
            throw new BusinessException(CodeErrorEnum.FAILED_NOTIFICATION, msg, param, "" + longitud);
        }
    }
}

