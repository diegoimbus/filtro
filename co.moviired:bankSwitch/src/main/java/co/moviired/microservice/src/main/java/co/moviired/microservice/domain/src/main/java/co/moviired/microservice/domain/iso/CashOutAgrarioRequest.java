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

import java.io.Serializable;

@Data
public class CashOutAgrarioRequest implements Serializable {

    //@ISOFieldDef(fieldNumber = 2, length = 4, mandatory = true)
    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_2, length = ConstantSwitch.LENGTH_4, mandatory = true)
    private String productCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_3, length = ConstantSwitch.LENGTH_6, mandatory = true)
    private String processingCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_4, length = ConstantSwitch.LENGTH_12, mandatory = true)
    private String amount;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_7, length = ConstantSwitch.LENGTH_10, mandatory = true)
    private String transmisionDateTime;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_11, length = ConstantSwitch.LENGTH_6, mandatory = true)
    private String traceAuditNumberYML;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_35, length = ConstantSwitch.LENGTH_12, mandatory = true)
    private String referenceNumber;


    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_41, length = ConstantSwitch.LENGTH_8, mandatory = true)
    private String agentCode;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_42, length = ConstantSwitch.LENGTH_15, mandatory = true)
    private String cellphoneNumber;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_43, length = ConstantSwitch.LENGTH_40, mandatory = true)
    private String codeHomologated;

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_52, length = ConstantSwitch.LENGTH_16, mandatory = true)
    private String blankField; //aplica BANCO AGRARIO

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_62, length = ConstantSwitch.LENGTH_999, mandatory = true)
    private String nameTercDeviceGestorCorrelation; //lastName|tercId|dispositivohomologado|gestorId|correlationId

    @ISOFieldDef(fieldNumber = ConstantSwitch.POSITION_63, length = ConstantSwitch.LENGTH_999, mandatory = true)
    private String referenceAccountypeOtp;  // AGRARIO - Referencenumber|typeAccount|otp


}

