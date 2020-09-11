package co.moviired.microservice.domain.iso;

import co.moviired.connector.domain.iso.enumeration.ISOFieldDef;
import co.moviired.microservice.constants.ConstantNumbers;
import lombok.Data;

import java.io.Serializable;

@Data
public class GenericRequest implements Serializable {

    private static final long serialVersionUID = 4L;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_3, length = ConstantNumbers.LENGTH_6, mandatory = true)
    private String processingCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_4, length = ConstantNumbers.LENGTH_12, mandatory = true)
    private String amount;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_7, length = ConstantNumbers.LENGTH_10, mandatory = true)
    private String transmisionDateTime;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_11, length = ConstantNumbers.LENGTH_6, mandatory = true)
    private String traceAuditNumber;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_12, length = ConstantNumbers.LENGTH_6, mandatory = true)
    private String localHour;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_13, length = ConstantNumbers.LENGTH_4, mandatory = true)
    private String localDate;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_17, length = ConstantNumbers.LENGTH_4, mandatory = true)
    private String captureDate;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_22, length = ConstantNumbers.LENGTH_3, mandatory = true)
    private String pointServiceEntryMode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_32, length = ConstantNumbers.LENGTH_11, mandatory = true)
    private String acquiringCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_37, length = ConstantNumbers.LENGTH_12, mandatory = true)
    private String retrievalReferenceNumber;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_40, length = ConstantNumbers.LENGTH_3, mandatory = true)
    private String restrictionCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_41, length = ConstantNumbers.LENGTH_16, mandatory = true)
    private String cardAcceptorIdentification;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_42, length = ConstantNumbers.LENGTH_15, mandatory = true)
    private String cardAcceptorCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_43, length = ConstantNumbers.LENGTH_40, mandatory = true)
    private String cardAcceptorName;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_49, length = ConstantNumbers.LENGTH_3, mandatory = true)
    private String currencyCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_71, length = ConstantNumbers.LENGTH_6, mandatory = true)
    private String messageNumber;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_100, length = ConstantNumbers.LENGTH_11, mandatory = true)
    private String receivingIdentCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_102, length = ConstantNumbers.LENGTH_15, mandatory = true)
    private String accountIdentification;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_104, length = ConstantNumbers.LENGTH_99, mandatory = true)
    private String transactionDescription;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_105, length = ConstantNumbers.LENGTH_999, mandatory = true)
    private String reservedIsoUse;

}

