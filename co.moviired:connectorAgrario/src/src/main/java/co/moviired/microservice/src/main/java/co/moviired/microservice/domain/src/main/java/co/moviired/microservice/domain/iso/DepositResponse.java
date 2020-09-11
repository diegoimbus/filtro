package co.moviired.microservice.domain.iso;

import co.moviired.connector.domain.iso.enumeration.ISOFieldDef;
import co.moviired.microservice.constants.ConstantNumbers;
import lombok.Data;

import java.io.Serializable;

@Data
public class DepositResponse implements Serializable {

    private static final long serialVersionUID = 6L;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_3, length = ConstantNumbers.LENGTH_6)
    private String processingCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_4, length = ConstantNumbers.LENGTH_12)
    private String amount;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_7, length = ConstantNumbers.LENGTH_10)
    private String transmissionDateTime;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_11, length = ConstantNumbers.LENGTH_6)
    private String traceAuditNumber;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_12, length = ConstantNumbers.LENGTH_6)
    private String localHour;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_13, length = ConstantNumbers.LENGTH_4)
    private String localDate;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_17, length = ConstantNumbers.LENGTH_4)
    private String captureDate;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_32, length = ConstantNumbers.LENGTH_11)
    private String acquiringCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_37, length = ConstantNumbers.LENGTH_12)
    private String retrievalReferenceNumber;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_38, length = ConstantNumbers.LENGTH_6)
    private String authorizationResponse;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_39, length = ConstantNumbers.LENGTH_2)
    private String responseCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_41, length = ConstantNumbers.LENGTH_16)
    private String cardAcceptorIdentification;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_42, length = ConstantNumbers.LENGTH_15)
    private String cardAcceptorCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_44, length = ConstantNumbers.LENGTH_25)
    private String aditionalData;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_46, length = ConstantNumbers.LENGTH_999)
    private String aditionalDataIso;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_47, length = ConstantNumbers.LENGTH_999)
    private String aditionalDataNational;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_49, length = ConstantNumbers.LENGTH_3)
    private String currencyCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_100, length = ConstantNumbers.LENGTH_11)
    private String receivingIdentCode;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_102, length = ConstantNumbers.LENGTH_15)
    private String accountIdentification1;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_103, length = ConstantNumbers.LENGTH_12)
    private String accountIdentification2;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_126, length = ConstantNumbers.LENGTH_999)
    private String reservedPrivateUse;

    @ISOFieldDef(fieldNumber = ConstantNumbers.POSITION_128, length = ConstantNumbers.LENGTH_8)
    private String mac2;

}

