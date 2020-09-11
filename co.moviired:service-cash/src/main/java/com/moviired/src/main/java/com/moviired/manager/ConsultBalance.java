package com.moviired.manager;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import com.moviired.client.balance.Response;
import com.moviired.model.request.CashOutRequest;
import com.moviired.properties.ConsultBalanceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ConsultBalance implements IConsultBalance {

    private static final String LOG_ERROR_PATTERN = " [Error:{}]";

    private final CryptoHelper cryptoHelperAuthorization;
    private final ConsultBalanceProperties consultBalanceProperties;


    public ConsultBalance(@NotNull CryptoHelper pCryptoHelperAuthorization,
                          @NotNull ConsultBalanceProperties pConsultBalanceProperties) {
        this.cryptoHelperAuthorization = pCryptoHelperAuthorization;
        this.consultBalanceProperties = pConsultBalanceProperties;
    }

    /**
     * metodo consultBalance (retorna el balance del usuario pasado por authorization)
     *
     * @param request
     * @return Response
     */
    @Override
    public Response consultBalance(CashOutRequest request) {

        Response response = Response.builder().errorCode("400").errorType("0").build();

        try {
            String authorizateUser = cryptoHelperAuthorization.encoder(request.getUserLogin());
            String authorizatePass = cryptoHelperAuthorization.encoder(request.getPin());
            String authorization = authorizateUser + ":" + authorizatePass;

            List<MediaType> medias = new ArrayList<>();
            medias.add(MediaType.APPLICATION_JSON);

            // Creacion cabecera
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(medias);
            headers.add("Authorization", authorization);
            headers.add("correlationId", request.getCorrelationId());

            // Invocar al servicio
            String url = consultBalanceProperties.getUrl();
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Object> entity = new HttpEntity<>(headers);

            ResponseEntity<Response> responseConsultBalance = restTemplate.exchange(url, HttpMethod.GET, entity, Response.class);
            response = responseConsultBalance.getBody();

        } catch (Exception e) {
            log.info(LOG_ERROR_PATTERN, e.getMessage());
            response.setErrorMessage(e.getMessage());

        } catch (ParsingException e) {
            log.error(e.getMessage(), e);
            response.setErrorMessage(e.getMessage());
            response.setErrorCode(e.getCode());
        }

        return response;

    }

}

