package co.moviired.transpiler.integration.iso.parser.impl;

import co.moviired.connector.helper.ISOMsgHelper;
import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.iso.model.EchoRequest;
import co.moviired.transpiler.integration.iso.model.EchoResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.EchoHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.EchoHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import co.moviired.transpiler.jpa.movii.domain.enums.Protocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.core.io.ClassPathResource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class EchoParser implements IHermesParser {

    private static final long serialVersionUID = 2626608527218126568L;

    // Iso8583 Packer definitions
    private transient ISOBasePackager packager;

    public EchoParser(@NotNull String xmlPackager) throws IOException, ISOException {
        super();
        this.packager = getPackager(xmlPackager);
    }

    // UTILS METHOD
    private ISOBasePackager getPackager(@NotNull String xmlPackager) throws ISOException, IOException {
        if (this.packager == null) {
            // Cargar el ISO packager
            this.packager = new GenericPackager(new ClassPathResource(xmlPackager).getInputStream());
        }

        return packager;
    }

    // SERVICE METHODS

    @Override
    public final IHermesRequest parseRequest(@NotBlank String request) throws ParseException {
        try {
            // Crear IsoMsg
            EchoRequest isoRequest = ISOMsgHelper.resolve("0800", request, this.packager, EchoRequest.class);

            // Armar el EchoHermesRequest
            EchoHermesRequest echo = new EchoHermesRequest();
            echo.setProtocol(Protocol.ISO);
            echo.setOriginalRequest(request);
            echo.setDate(isoRequest.getDateTime());
            echo.setClientTxnId(isoRequest.getTransactionCode().toString());
            echo.setNit(isoRequest.getNit());
            echo.setRed(isoRequest.getNetworkCode());

            return echo;

        } catch (Exception e) {
            log.error("\n{}\n", e.getMessage());
            throw new ParseException(e);
        }
    }

    @Override
    public final String parseResponse(@NotNull IHermesResponse hermesResponse) throws ParseException {
        try {
            // Transformar a la respuesta específica del parser
            EchoHermesResponse response = (EchoHermesResponse) hermesResponse;
            EchoResponse echoResponse = new EchoResponse();
            echoResponse.setDateTime(response.getDate());
            echoResponse.setTransactionCode(Integer.parseInt(response.getClientTxnId()));
            echoResponse.setNetworkCode(0);
            echoResponse.setResponseCode(Integer.parseInt(response.getResponse().getErrorCode()));

            // Generar la trama de respuesta
            ISOMsg isoMessage = ISOMsgHelper.of(OperationType.ECHO_RESPONSE.getCode(), echoResponse, this.packager);
            byte[] b = isoMessage.pack();
            String isoResponse = new String(b, StandardCharsets.UTF_8);

            return StringUtils.leftPad(Integer.toHexString(isoResponse.length()), 3, '0') + isoResponse;

        } catch (Exception e) {
            log.error("\n{}\n", e.getMessage());
            throw new ParseException(e);
        }
    }

}

