package co.movii.auth.server.service;

import co.movii.auth.server.exception.BirthdayNullException;
import co.movii.auth.server.exception.PasswordFormatInvalidException;
import co.movii.auth.server.helper.UtilHelper;
import co.movii.auth.server.properties.ExtraValidationsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public final class ValidationPasswordService implements Serializable {

    private static final String INVALID_CLV = "Contraseña no permitida.";
    private static final String TWO_DIGITS = "%02d";
    private static final int NUM_4 = 4;
    private static final int NUM_10 = 10;
    private static final int NUM_100 = 100;

    private final ExtraValidationsProperties extraValidations;

    public ValidationPasswordService(ExtraValidationsProperties pextraValidations) {
        super();
        this.extraValidations = pextraValidations;
    }

    private void validateRepeatedDigits(String password) throws PasswordFormatInvalidException {
        if (extraValidations.isRepeatedDigits()) {
            Pattern pattern = Pattern.compile("^([0-9])\\1{" + (password.length() - 1) + ",}$");
            Matcher matcher = pattern.matcher(password);
            if (matcher.find()) {
                throw new PasswordFormatInvalidException(INVALID_CLV);
            }
        }
    }

    private void validateAscendentDescendentRepeatedDigits(String password) throws PasswordFormatInvalidException {
        if (extraValidations.isConsecutiveDigits()) {
            char[] p = password.toCharArray();
            String firstDigit = password.substring(0, 1);
            List<Integer> listPassword = new ArrayList<>();
            List<Integer> consecutivePassword = new ArrayList<>();
            for (int i = 0; i < password.length(); i++) {
                listPassword.add(Integer.parseInt(String.valueOf(p[i])));
                consecutivePassword.add((Integer.parseInt(firstDigit) + i) % ValidationPasswordService.NUM_10);
            }
            if (listPassword.equals(consecutivePassword)) {
                throw new PasswordFormatInvalidException(INVALID_CLV);
            }

            //Validate descendent repeated digits
            p = password.toCharArray();
            String lastDigit = password.substring(password.length() - 1);
            listPassword = new ArrayList<>();
            consecutivePassword = new ArrayList<>();
            for (int i = 0; i < password.length(); i++) {
                listPassword.add(Integer.parseInt(String.valueOf(p[i])));
                consecutivePassword.add((Integer.parseInt(lastDigit) + i) % ValidationPasswordService.NUM_10);
            }
            Collections.reverse(consecutivePassword);
            if (listPassword.equals(consecutivePassword)) {
                throw new PasswordFormatInvalidException(INVALID_CLV);
            }
        }
    }

    private void validateLastDocumentNumberDigits(String password, String documentNumber) throws PasswordFormatInvalidException {
        // Si viene número de documento, validarlo, si no viene, lo deja continuar.
        if ((extraValidations.isLastDocumentNumberDigits()) &&
                (documentNumber != null) &&
                (!documentNumber.trim().isEmpty()) &&
                (password.equals(documentNumber.substring(documentNumber.length() - ValidationPasswordService.NUM_4)))
        ) {
            throw new PasswordFormatInvalidException(INVALID_CLV);
        }
    }

    private void validateLastCellPhoneDigits(String password, String cellPhone) throws PasswordFormatInvalidException {
        // Si viene número de celular, validarlo, si no viene, lo deja continuar.
        if ((extraValidations.isLastCellPhoneDigits()) &&
                (null != cellPhone) &&
                (!cellPhone.trim().isEmpty()) &&
                (password.equals(cellPhone.substring(cellPhone.length() - ValidationPasswordService.NUM_4)))) {
            throw new PasswordFormatInvalidException(INVALID_CLV);
        }
    }

    private void validateDateValid(String password, String birthdayDate) throws PasswordFormatInvalidException, BirthdayNullException {
        // Validar año actual
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        if (currentYear.equals(password)) {
            throw new PasswordFormatInvalidException(INVALID_CLV);
        }

        // Si no se encuentra fecha de cumpleaños, escribe log y lo deja continuar
        if (null == birthdayDate || "".equals(birthdayDate)) {
            throw new BirthdayNullException("No se pudo validar el password.");
        }


        // Si la contraseña es igual a alguna de las no permitidas, escribe en log, y retorna False
        for (String invalidFormat : extraValidations.getInvalidPasswords()) {
            if (invalidFormat.equals(password)) {
                throw new PasswordFormatInvalidException(INVALID_CLV);
            }
        }


    }

    private void validateDateValidPrt2(String password, String birthdayDatePattern, String birthdayDate) throws PasswordFormatInvalidException {


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(birthdayDatePattern);
        int year;
        String yearTwoDigits;
        String month;
        String day;

        if (birthdayDatePattern.equals(UtilHelper.DATE_FORMAT_DOB_QUERY_USER_INFO_MAHINDRA)) {
            LocalDate date = LocalDate.parse(birthdayDate, formatter);
            year = date.getYear();
            yearTwoDigits = new DecimalFormat("00").format(year % ValidationPasswordService.NUM_100);
            month = String.format(ValidationPasswordService.TWO_DIGITS, date.getMonthValue());
            day = String.format(ValidationPasswordService.TWO_DIGITS, date.getDayOfMonth());
        } else {
            LocalDateTime date = LocalDateTime.parse(birthdayDate, formatter);
            year = date.getYear();
            yearTwoDigits = new DecimalFormat("00").format(year % ValidationPasswordService.NUM_100);
            month = String.format(ValidationPasswordService.TWO_DIGITS, date.getMonthValue());
            day = String.format(ValidationPasswordService.TWO_DIGITS, date.getDayOfMonth());
        }

        validateDateValidPrt3(password, year, yearTwoDigits, month, day);

    }

    private void validateDateValidPrt3(String password, int year, String yearTwoDigits, String month, String day) throws PasswordFormatInvalidException {


        for (String invalidFormat : extraValidations.getInvalidPasswordFormats()) {

            validatePasswordInvalidYear(password, invalidFormat, year);

            StringBuilder stb = new StringBuilder();
            if ("yy".equals(invalidFormat.substring(0, 2))) {
                stb.append(yearTwoDigits);
            }
            if ("MM".equals(invalidFormat.substring(0, 2))) {
                stb.append(month);
            }
            if ("dd".equals(invalidFormat.substring(0, 2))) {
                stb.append(day);
            }
            if ("yy".equals(invalidFormat.substring(2, ValidationPasswordService.NUM_4))) {
                stb.append(yearTwoDigits);
            }
            if ("MM".equals(invalidFormat.substring(2, ValidationPasswordService.NUM_4))) {
                stb.append(month);
            }
            if ("dd".equals(invalidFormat.substring(2, ValidationPasswordService.NUM_4))) {
                stb.append(day);
            }
            validatePasswordInvalid(password, stb);
        }

    }


    public void validatePasswordInvalid(String password, StringBuilder stb) throws PasswordFormatInvalidException {
        if (password.equals(stb.toString())) {
            throw new PasswordFormatInvalidException(INVALID_CLV);
        }
    }


    public void validatePasswordInvalidYear(String password, String invalidFormat, int year) throws PasswordFormatInvalidException {
        if ((invalidFormat.equals("yyyy")) && (password.equals(String.valueOf(year)))) {
            throw new PasswordFormatInvalidException(INVALID_CLV);
        }
    }



    public boolean isValidPasswordFormat(String password, String documentNumber, String cellPhone, String birthdayDate, String birthdayDatePattern) {
        boolean response = false;
        try {

            //Validate repeated digits
            validateRepeatedDigits(password);

            //Validate ascendent repeated digits
            validateAscendentDescendentRepeatedDigits(password);

            // Validate last documentNumber digits
            validateLastDocumentNumberDigits(password, documentNumber);

            // Validate last cellPhone digits
            validateLastCellPhoneDigits(password, cellPhone);

            // Validar año actual
            // Si no se encuentra fecha de cumpleaños, escribe log y lo deja continuar
            // Si la contraseña es igual a alguna de las no permitidas, escribe en log, y retorna False
            validateDateValid(password, birthdayDate);
            validateDateValidPrt2(password, birthdayDatePattern, birthdayDate);

            response = true;
        } catch (DateTimeParseException e) {
            log.error("Error al obtener fecha de nacimiento.");

        } catch (BirthdayNullException | PasswordFormatInvalidException e) {
            log.error("ERROR al validar la contraseña. {}", e.getMessage());

        }

        return response;
    }
}

