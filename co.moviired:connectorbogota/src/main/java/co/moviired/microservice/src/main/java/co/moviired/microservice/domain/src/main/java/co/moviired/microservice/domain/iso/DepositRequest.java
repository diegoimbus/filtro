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

import java.io.Serializable;


@Data
public class DepositRequest implements Serializable {

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_3, length = ConstantSwitch.LENGTH_6, mandatory = true)
    private String processingCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_4, length = ConstantSwitch.LENGTH_12, mandatory = true)
    private String amount;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_7, length = ConstantSwitch.LENGTH_10, mandatory = true)
    private String transmisionDateTime;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_11, length = ConstantSwitch.LENGTH_6, mandatory = true)
    private String traceAuditNumber;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_12, length = ConstantSwitch.LENGTH_6, mandatory = true)
    private String localHour;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_13, length = ConstantSwitch.LENGTH_4, mandatory = true)
    private String localDate;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_17, length = ConstantSwitch.LENGTH_4, mandatory = true)
    private String captureDate;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_22, length = ConstantSwitch.LENGTH_3, mandatory = true)
    private String pointServiceEntryMode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_32, length = ConstantSwitch.LENGTH_11, mandatory = true)
    private String acquiringCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_35, length = ConstantSwitch.LENGTH_37, mandatory = true)
    private String trackData;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_37, length = ConstantSwitch.LENGTH_12, mandatory = true)
    private String retrievalReference;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_41, length = ConstantSwitch.LENGTH_16, mandatory = true)
    private String acquiringIdentification;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_43, length = ConstantSwitch.LENGTH_40, mandatory = true)
    private String codeHomologated;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_48, length = ConstantSwitch.LENGTH_44, mandatory = true)
    private String aditionalData;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_49, length = ConstantSwitch.LENGTH_3, mandatory = true)
    private String currencyCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_52, length = ConstantSwitch.LENGTH_16, mandatory = true)
    private String pinData;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_54, length = ConstantSwitch.LENGTH_36, mandatory = true)
    private String additionalAmounts;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_60, length = ConstantSwitch.LENGTH_15, mandatory = true)
    private String acquiringInformation;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_62, length = ConstantSwitch.LENGTH_150, mandatory = true)
    private String referenceNumber;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_102, length = ConstantSwitch.LENGTH_21, mandatory = true)
    private String originAccountNumber;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_103, length = ConstantSwitch.LENGTH_24, mandatory = true)
    private String accountNumber;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_104, length = ConstantSwitch.LENGTH_18, mandatory = true)
    private String accountIdentification;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_128, length = ConstantSwitch.LENGTH_16, mandatory = true)
    private String mac2;

}
