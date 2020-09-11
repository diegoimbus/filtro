package co.moviired.microservice.domain.iso;

import co.moviired.connector.domain.iso.enumeration.ISOFieldDef;
import co.moviired.microservice.constants.ConstantNumbers;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
public class GenericResponse implements Serializable {

    private static final long serialVersionUID = 6L;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_3, length = ConstantNumbers.LENGTH_6)
    private String processingCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_4, length = ConstantNumbers.LENGTH_12)
    private String amount;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_7, length = ConstantNumbers.LENGTH_10)
    private String transmisionDateTime;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_11, length = ConstantNumbers.LENGTH_6)
    private String traceAuditNumber;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_12, length = ConstantNumbers.LENGTH_6)
    private String localHour;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_13, length = ConstantNumbers.LENGTH_4)
    private String localDate;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_17, length = ConstantNumbers.LENGTH_4)
    private String captureDate;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_22, length = ConstantNumbers.LENGTH_3)
    private String pointServiceEntryMode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_32, length = ConstantNumbers.LENGTH_11)
    private String acquiringCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_35, length = ConstantNumbers.LENGTH_37)
    private String trackData;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_37, length = ConstantNumbers.LENGTH_12)
    private String retrievalReference;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_41, length = ConstantNumbers.LENGTH_16)
    private String acquiringIdentification;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_43, length = ConstantNumbers.LENGTH_40)
    private String codeHomologated;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_48, length = ConstantNumbers.LENGTH_44)
    private String aditionalData;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_49, length = ConstantNumbers.LENGTH_3)
    private String currencyCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_52, length = ConstantNumbers.LENGTH_16)
    private String pinData;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_60, length = ConstantNumbers.LENGTH_15)
    private String acquiringInformation;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_62, length = ConstantNumbers.LENGTH_150)
    private String referenceNumber;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_102, length = ConstantNumbers.LENGTH_21)
    private String originAccountNumber;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_103, length = ConstantNumbers.LENGTH_24)
    private String accountNumber;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_104, length = ConstantNumbers.LENGTH_18)
    private String accountIdentification;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_128, length = ConstantNumbers.LENGTH_16)
    private String mac2;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_38, length = ConstantNumbers.LENGTH_6)
    private String authorizationResponse;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_39, length = ConstantNumbers.LENGTH_2)
    private String responseCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_54, length = ConstantNumbers.LENGTH_36)
    private String additionalAmounts;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_61, length = ConstantNumbers.LENGTH_13)
    private String aceptorInformation;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_105, length = ConstantNumbers.LENGTH_25)
    private String verificationCart;

}

