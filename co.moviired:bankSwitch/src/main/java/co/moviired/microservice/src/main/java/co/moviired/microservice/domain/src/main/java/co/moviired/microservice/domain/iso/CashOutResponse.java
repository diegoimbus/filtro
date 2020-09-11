package co.moviired.microservice.domain.iso;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.connector.domain.iso.enumeration.ISOFieldDef;
import co.moviired.microservice.domain.constants.ConstantSwitch;
import lombok.Data;

@Data
public class CashOutResponse {

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_2)
    private String productCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_3)
    private String processingCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_4)
    private String amount;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_39)
    private String statusCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_37)
    private String authorizationNumber;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_63, length = ConstantSwitch.LENGTH_999)
    private String messageResponse;  // BBVA - codCof|comission|saldo|cuenta
    // AGRARIO - saldo|nombre|comission|AAAA-MM-dd|hh:mm:ss

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_73, length = ConstantSwitch.LENGTH_999)
    private String campo73;
}

