package co.moviired.microservice.domain.jpa.convenios.entity;

import co.moviired.microservice.domain.enums.GeneralStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ws_biller")
public class Biller implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;

    private static final int NAME_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(name = "ean_code", unique = true)
    private String eanCode;

    @Column(name = "biller_code", nullable = false)
    private String billerCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "product_code", unique = true)
    private String productCode;

    @Column(name = "product_description", length = NAME_LENGTH)
    private String productDescription;

    @Column(name = "min_value", nullable = false)
    private Integer minValue;

    @Column(name = "max_value", nullable = false)
    private Integer maxValue;

    @Column(name = "microservice_root", nullable = false)
    private String microserviceRoot;

    @Column(name = "url_bill_pay", nullable = false)
    private String urlBillPay;

    @Column(name = "bill_pay_connect_timeout")
    private Integer billPayConnectTimeout;

    @Column(name = "bill_pay_read_timeout")
    private Integer billPayReadTimeout;

    @Column(name = "url_bill_get", nullable = false)
    private String urlBillGet;

    @Column(name = "bill_get_connect_timeout")
    private Integer billGetConnectTimeout;

    @Column(name = "bill_get_read_timeout")
    private Integer billGetReadTimeout;

    @Column(name = "reg_exp")
    private String regExp;

    @Column(name = "send_sms")
    private Integer sendSms;

    @Column(name = "template_sms")
    private String templateSms;

    @Column(name = "url_sms")
    private String urlSms;

    @Column(name = "bill_sms_connect_timeout")
    private Integer billSmsConnectTimeout;

    @Column(name = "bill_sms_read_timeout")
    private Integer billSmsReadTimeout;

    @Column(name = "third_party_code")
    private String thirdPartyCode;

    @Column(name = "reference_length1")
    private int referenceLength1;

    @Column(name = "reference_position1")
    private int referencePosition1;

    @Enumerated
    @Builder.Default
    @Column(nullable = false)
    private GeneralStatus status = GeneralStatus.ENABLED;

}
