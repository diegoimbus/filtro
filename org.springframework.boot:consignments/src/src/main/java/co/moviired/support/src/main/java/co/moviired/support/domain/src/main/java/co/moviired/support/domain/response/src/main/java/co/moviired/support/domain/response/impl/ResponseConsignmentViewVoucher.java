package co.moviired.support.domain.response.impl;

import co.moviired.support.domain.response.IResponseConsignmentSearch;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseConsignmentViewVoucher implements IResponseConsignmentSearch {

    private static final long serialVersionUID = 4238333415815360590L;

    private String errorType;

    private String errorCode;

    private String errorMessage;

    private String voucher;

}

