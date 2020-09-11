package co.moviired.support.domain.request.impl;

import co.moviired.support.domain.request.IConsignmentRegistry;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
public class ConsignmentRegistry implements IConsignmentRegistry {

    private static final long serialVersionUID = 5373212418386184168L;

    private Integer id;

    private String bankId;

    private String correlationId;

    private String amount;

    private String paymentDate;

    private String agreementNumber;

    private String paymentReference;

    private String state;

    private String city;

    private String branchOffice;

    private String authorization;

    private String voucher;

    private String nameClient;

    private String nameAlliance;

    private String usernamePortalRegistry;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "EST")
    private Date registryDate;

    private String usernamePortalEdit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "EST")
    private Date editDate;

    private String usernamePortalAuthorizer;

    private Date authorizerDate;

    @Override
    public String toString() {
        return "ConsignmentRegistry [bankId=".concat(bankId)
                .concat(", amount=").concat(amount)
                .concat(", paymentDate=").concat(paymentDate)
                .concat(", agreementNumber=").concat(agreementNumber)
                .concat(", paymentReference=").concat(paymentReference)
                .concat(", state=").concat(state)
                .concat(", city=").concat(city)
                .concat(", nameAlliance=").concat(nameAlliance)
                .concat(", userRegistry=").concat(usernamePortalRegistry)
                .concat(", branchOffice=").concat(branchOffice).concat("]");
    }

}

