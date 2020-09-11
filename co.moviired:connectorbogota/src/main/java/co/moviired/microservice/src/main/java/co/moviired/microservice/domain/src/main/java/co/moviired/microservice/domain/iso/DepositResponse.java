package co.moviired.microservice.domain.iso;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

import co.moviired.connector.domain.iso.enumeration.ISOFieldDef;
import co.moviired.microservice.constants.ConstantSwitch;
import lombok.Data;


@Data
public class DepositResponse {

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_3, length = ConstantSwitch.LENGTH_6)
    private String processingCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_4, length = ConstantSwitch.LENGTH_12)
    private String amount;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_7, length = ConstantSwitch.LENGTH_10)
    private String transmisionDateTime;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_11, length = ConstantSwitch.LENGTH_6)
    private String traceAuditNumber;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_12, length = ConstantSwitch.LENGTH_6)
    private String localHour;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_13, length = ConstantSwitch.LENGTH_4)
    private String localDate;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_17, length = ConstantSwitch.LENGTH_4)
    private String captureDate;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_22, length = ConstantSwitch.LENGTH_3)
    private String pointServiceEntryMode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_32, length = ConstantSwitch.LENGTH_11)
    private String acquiringCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_35, length = ConstantSwitch.LENGTH_37)
    private String trackData;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_37, length = ConstantSwitch.LENGTH_12)
    private String retrievalReference;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_38, length = ConstantSwitch.LENGTH_6)
    private String authorizationResponse;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_39, length = ConstantSwitch.LENGTH_2)
    private String responseCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_41, length = ConstantSwitch.LENGTH_16)
    private String acquiringIdentification;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_43, length = ConstantSwitch.LENGTH_40)
    private String codeHomologated;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_48, length = ConstantSwitch.LENGTH_44)
    private String aditionalData;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_49, length = ConstantSwitch.LENGTH_3)
    private String currencyCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_54, length = ConstantSwitch.LENGTH_36)
    private String additionalAmounts;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_60, length = ConstantSwitch.LENGTH_15)
    private String acquiringInformation;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_61, length = ConstantSwitch.LENGTH_13)
    private String aceptorInformation;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_62, length = ConstantSwitch.LENGTH_150)
    private String additionalData;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_102, length = ConstantSwitch.LENGTH_21)
    private String originAccountNumber;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_103, length = ConstantSwitch.LENGTH_24)
    private String accountNumber;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_104, length = ConstantSwitch.LENGTH_18)
    private String accountIdentification;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_105, length = ConstantSwitch.LENGTH_25)
    private String verificationCart;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_128, length = ConstantSwitch.LENGTH_16)
    private String mac2;

}

