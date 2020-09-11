package co.movii.auth.server.properties;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "extra-validations")
public class ExtraValidationsProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    private boolean enable;

    // Password
    private String[] invalidPasswordFormats;
    private String[] invalidPasswords;
    private boolean lastDocumentNumberDigits;
    private boolean lastCellPhoneDigits;
    private boolean repeatedDigits;
    private boolean consecutiveDigits;

    // Merchant: Día, Dispositivo y MAC
    private boolean merchantImei;
    private boolean merchantDate;
    private int merchantMaxDevices;

    // Suscriber: Día, Dispositivo y MAC
    private boolean suscriberImei;
    private boolean suscriberDate;
    private int suscriberMaxDevices;

    // Lista blanca
    private List<String> whiteList;

}

