package com.moviired.model.response.impl;

import com.moviired.model.dto.BankDetailDTO;
import com.moviired.model.response.IResponseBankSearch;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author carlossaul.ramirez
 * @category srv-cash
 */
@Data
@Builder
public class ResponseBankSearch implements IResponseBankSearch {

    private static final long serialVersionUID = 2291159924602099683L;

    private String errorType;

    private String errorCode;

    private String errorMessage;

    private List<BankDetailDTO> banks;

}

