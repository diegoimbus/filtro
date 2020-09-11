package co.moviired.support.domain.response.impl;

import co.moviired.support.domain.dto.BankDetailDTO;
import co.moviired.support.domain.response.IResponseBankSearch;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBankSearch implements IResponseBankSearch {

    private static final long serialVersionUID = 2291159924602099683L;

    private String errorType;

    private String errorCode;

    private String errorMessage;

    private List<BankDetailDTO> banks;

}

