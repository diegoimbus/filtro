package co.moviired.transpiler.integration.iso.model;

import co.moviired.connector.domain.iso.enumeration.ISOFieldDef;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TopUpResponse implements Serializable {

    private static final long serialVersionUID = 7353787682700892273L;

    @ISOFieldDef(fieldNumber = 2, length = 19)
    private String accountNumber;

    @ISOFieldDef(fieldNumber = 3, length = 6)
    private String processingCode;

    @ISOFieldDef(fieldNumber = 4, length = 12, pattern = "%010d00")
    private Integer amount;

    @ISOFieldDef(fieldNumber = 7, pattern = "%tm%td%tH%tM%tS", length = 10)
    private Date dateTime;

    @ISOFieldDef(fieldNumber = 11, pattern = "%06d", length = 6)
    private Integer transactionCode;

    @ISOFieldDef(fieldNumber = 32, length = 99)
    private String nit;

    @ISOFieldDef(fieldNumber = 35, length = 37)
    private String trackNumber;

    @ISOFieldDef(fieldNumber = 37, length = 12, pattern = "%012d")
    private Long retrievalReferenceNumber;

    @ISOFieldDef(fieldNumber = 38, length = 6)
    private String authorizationNumber;

    @ISOFieldDef(fieldNumber = 39, length = 2)
    private String responseCode;

    @ISOFieldDef(fieldNumber = 41, length = 8)
    private String cardAcceptor;

    @ISOFieldDef(fieldNumber = 42, length = 15)
    private String terminalID;

    @ISOFieldDef(fieldNumber = 43, length = 40)
    private String rechargeNumber;

    @ISOFieldDef(fieldNumber = 54, length = 99)
    private String additionalAmount;

    @ISOFieldDef(fieldNumber = 63, length = 999)
    private String reservedPrivate;

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime != null ? (Date) dateTime.clone() : null;
    }
}

