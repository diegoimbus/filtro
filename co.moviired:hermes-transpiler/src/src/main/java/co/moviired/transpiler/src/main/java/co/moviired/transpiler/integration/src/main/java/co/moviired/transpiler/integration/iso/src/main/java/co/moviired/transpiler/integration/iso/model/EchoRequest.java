package co.moviired.transpiler.integration.iso.model;

import co.moviired.connector.domain.iso.enumeration.ISOFieldDef;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class EchoRequest implements Serializable {

    private static final long serialVersionUID = 7353787682700892273L;

    @ISOFieldDef(fieldNumber = 7, pattern = "%tm%td%tH%tM%tS", length = 10)
    private Date dateTime;

    @ISOFieldDef(fieldNumber = 11, pattern = "%06d", length = 6)
    private Integer transactionCode;

    @ISOFieldDef(fieldNumber = 32, length = 99)
    private String nit;

    @ISOFieldDef(fieldNumber = 70, pattern = "%03d", length = 3)
    private Integer networkCode;

    public Date getDateTime() {
        return dateTime != null ? (Date) dateTime.clone() : null;
    }

}

