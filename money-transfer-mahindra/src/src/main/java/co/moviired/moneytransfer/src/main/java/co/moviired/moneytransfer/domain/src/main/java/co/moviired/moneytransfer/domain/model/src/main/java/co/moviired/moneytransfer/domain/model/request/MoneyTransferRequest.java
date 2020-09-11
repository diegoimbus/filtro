package co.moviired.moneytransfer.domain.model.request;

import co.moviired.moneytransfer.helper.ConstanHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MoneyTransferRequest implements Serializable {

    private String merchantId;
    private String posId;

    @Pattern(regexp=ConstanHelper.REGEXP_4, message="transactionId debe tener caracteres validos")
    @Size(min=ConstanHelper.NUMBER_20,  message="transactionId debe tener 20 caracteres minimo")
    @Size(max= ConstanHelper.NUMBER_20,  message="transactionId debe tener 20 caracteres maximo")
    private String transactionId;

    private String correlationId;

    @Pattern(regexp=ConstanHelper.REGEXP_1, message="eanCode debe ser numerico")
    @Size(min=ConstanHelper.NUMBER_6,  message="eanCode debe tener 6 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_9,  message="eanCode debe tener 9 caracteres maximo")
    private String eanCode;

    @Size(min=ConstanHelper.NUMBER_10,  message="issuerDate debe tener 10 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_25,  message="issuerDate debe tener 25 caracteres maximo")
    private String issuerDate;

    @Pattern(regexp=ConstanHelper.REGEXP_2, message="origin debe tener caracteres validos")
    @Size(min=ConstanHelper.NUMBER_5,  message="origin debe tener 5 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_6,  message="origin debe tener 6 caracteres maximo")
    private String origin;

    @Pattern(regexp=ConstanHelper.REGEXP_2, message="source debe tener caracteres validos")
    @Size(max=ConstanHelper.NUMBER_7,  message="source debe tener 7 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_7,  message="source debe tener 7 caracteres maximo")
    private String source;

    @Pattern(regexp=ConstanHelper.REGEXP_5, message="idTypeSender debe tener caracteres validos")
    @Size(min=ConstanHelper.NUMBER_2,  message="idTypeSender debe tener 2 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_3,  message="idTypeSender debe tener 3 caracteres maximo")
    private String idTypeSender;

    @Pattern(regexp=ConstanHelper.REGEXP_3, message="idSender debe tener caracteres validos")
    @Size(min=ConstanHelper.NUMBER_5,  message="idSender debe tener 5 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_15,  message="idSender debe tener 15 caracteres maximo")
    private String idSender;

    private String nameSender;

    @Pattern(regexp=ConstanHelper.REGEXP_1, message="phoneNumberSender debe ser numerico")
    @Size(min=ConstanHelper.NUMBER_10,  message="phoneNumberSender debe tener 10 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_10,  message="phoneNumberSender debe tener 10 caracteres maximo")
    private String phoneNumberSender;

    private String emailSender;

    @Pattern(regexp=ConstanHelper.REGEXP_5, message="idTypeReceiver debe tener caracteres validos")
    @Size(min=ConstanHelper.NUMBER_2,  message="idTypeReceiver debe tener 2 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_3,  message="idTypeReceiver debe tener 3 caracteres maximo")
    private String idTypeReceiver;

    @Pattern(regexp=ConstanHelper.REGEXP_3, message="idReceiver debe tener caracteres validos")
    @Size(min=ConstanHelper.NUMBER_5,  message="idReceiver debe tener 5 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_15,  message="idReceiver debe tener 15 caracteres maximo")
    private String idReceiver;

    private String nameReceiver;

    @Pattern(regexp=ConstanHelper.REGEXP_1, message="phoneNumberReceiver debe ser numerico")
    @Size(min=ConstanHelper.NUMBER_10,  message="phoneNumberReceiver debe tener 10 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_10,  message="phoneNumberReceiver debe tener 10 caracteres maximo")
    private String phoneNumberReceiver;

    private String emailReceiver;

    @Min(value = ConstanHelper.NUMBER_1, message = "amount debe tener un valor" )
    @Max(value = ConstanHelper.NUMBER_2000000, message = "amount debe ser maximo de 2000000" )
    private Integer amount;

    @Pattern(regexp=ConstanHelper.REGEXP_1, message="otp debe ser numerico")
    @Size(min=ConstanHelper.NUMBER_5,  message="otp debe tener 5 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_5,  message="otp debe tener 5 caracteres maximo")
    private String otp;

    @Pattern(regexp=ConstanHelper.REGEXP_4, message="moneyTransferId debe tener caracteres validos")
    @Size(min=ConstanHelper.NUMBER_20,  message="moneyTransferId debe tener 20 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_20,  message="moneyTransferId debe tener 20 caracteres maximo")
    private String moneyTransferId;

    private String txnId;
    private Integer freight;
    private Integer freightIva;
    private Integer amountTotal;
    private String user;
    private String mpin;

    @Pattern(regexp=ConstanHelper.REGEXP_4, message="transferId debe tener caracteres validos")
    @Size(min=ConstanHelper.NUMBER_20,  message="transferId debe tener 20 caracteres minimo")
    @Size(max=ConstanHelper.NUMBER_20,  message="transferId debe tener 20 caracteres maximo")
    private String transferId;

    private String userMerchant;

    @Pattern(regexp=ConstanHelper.REGEXP_6, message="detailCancel debe tener caracteres validos")
    @Size(min=ConstanHelper.NUMBER_1,  message="detailCancel debe tener 1 caracter minimo")
    private String detailCancel;

    @Override
    public String toString() {
        return "{" +
                "merchantId='" + merchantId + '\'' +
                ", posId='" + posId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", eanCode='" + eanCode + '\'' +
                ", issuerDate='" + issuerDate + '\'' +
                ", origin='" + origin + '\'' +
                ", source='" + source + '\'' +
                ", idTypeSender='" + idTypeSender + '\'' +
                ", idSender='" + idSender + '\'' +
                ", nameSender='" + nameSender + '\'' +
                ", phoneNumberSender='" + phoneNumberSender + '\'' +
                ", emailSender='" + emailSender + '\'' +
                ", idTypeReceiver='" + idTypeReceiver + '\'' +
                ", idReceiver='" + idReceiver + '\'' +
                ", nameReceiver='" + nameReceiver + '\'' +
                ", phoneNumberReceiver='" + phoneNumberReceiver + '\'' +
                ", emailReceiver='" + emailReceiver + '\'' +
                ", amount=" + amount +
                ", otp='*****'" +
                ", moneyTransferId='" + moneyTransferId + '\'' +
                ", txnId='" + txnId + '\'' +
                ", freight=" + freight +
                ", freightIva=" + freightIva +
                ", amountTotal=" + amountTotal +
                ", user='" + user + '\'' +
                ", mpin= '****'" +
                ", transferId='" + transferId + '\'' +
                ", userMerchant='" + userMerchant + '\'' +
                ", detailCancel='" + detailCancel + '\'' +
                '}';
    }

}

