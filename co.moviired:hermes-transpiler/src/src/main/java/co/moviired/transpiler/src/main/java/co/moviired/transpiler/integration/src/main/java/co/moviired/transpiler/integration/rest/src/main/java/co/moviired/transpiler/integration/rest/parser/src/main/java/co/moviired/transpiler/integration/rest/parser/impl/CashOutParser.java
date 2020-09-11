package co.moviired.transpiler.integration.rest.parser.impl;

import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@Service("cashOutParserRest")
public class CashOutParser implements IHermesParser {

    private static final long serialVersionUID = 2626608527218126568L;

    // SERVICE METHODS

    @Override
    public final IHermesRequest parseRequest(@NotBlank String request) {
        return null;
    }

    @Override
    public final String parseResponse(@NotNull IHermesResponse hermesResponse) {
        return null;
    }

}

