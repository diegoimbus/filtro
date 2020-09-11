package co.moviired.transpiler.hermes.parser.impl;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.hermes.parser.IMahindraParser;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class CashOutMahindraParser implements IMahindraParser {

    private static final long serialVersionUID = -7190607322439408836L;

    @Override
    public final ICommandRequest parseRequest(@NotNull IHermesRequest hermesRequest) throws ParseException {
        throw new ParseException("Método no implementado");
    }

    @Override
    public final IHermesResponse parseResponse(@NotNull IHermesRequest hermesRequest, @NotNull ICommandResponse command) throws ParseException {
        throw new ParseException("Método no implementado");
    }
}

