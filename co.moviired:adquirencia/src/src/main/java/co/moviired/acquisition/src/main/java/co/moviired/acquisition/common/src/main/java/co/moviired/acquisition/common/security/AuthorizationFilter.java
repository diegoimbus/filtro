package co.moviired.acquisition.common.security;

/*
 * Copyright @2019. MOVIIRED, S.A.S. Todos los derechos reservados.
 *
 * @author Ronel Rivas
 * @version 1, 2019-10-25
 * @since 1.0
 */

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.acquisition.common.config.StatusCodeConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.List;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;
import static co.moviired.acquisition.common.util.StatusCodesHelper.AUTHORIZATION_HEADER_INVALID_CODE;

@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthorizationFilter implements WebFilter {

    private final CryptoHelper cryptoHelper;
    private final StatusCodeConfig statusCodeConfig;

    public AuthorizationFilter(@Qualifier(value = CRYPTO_HELPER) @NotNull CryptoHelper cryptoHelperI, @NotNull StatusCodeConfig statusCodeConfigI) {
        super();
        this.cryptoHelper = cryptoHelperI;
        this.statusCodeConfig = statusCodeConfigI;
    }

    @Override
    public final Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest nRequest = null;
        List<String> authorizations = exchange.getRequest().getHeaders().get(AUTHORIZATION_HEADER);
        if (authorizations != null && !authorizations.isEmpty()) {
            try {
                String authorization = authorizations.get(0);

                if (authorization.contains(BASIC)) {
                    authorization = new String(Base64.decodeBase64(
                            authorization.replace(BASIC, EMPTY_STRING).trim()
                                    .getBytes()));
                } else {
                    String[] credentials = authorization.split(TWO_DOTS);
                    authorization = this.cryptoHelper.decoder(credentials[0]) + TWO_DOTS + this.cryptoHelper.decoder(credentials[1]);
                }

                nRequest = exchange.getRequest()
                        .mutate()
                        .header(AUTHORIZATION_HEADER, authorization)
                        .build();

            } catch (ParsingException e) {
                return Mono.error(new ServiceException(ErrorType.VALIDATOR, AUTHORIZATION_HEADER_INVALID_CODE,
                        statusCodeConfig.of(AUTHORIZATION_HEADER_INVALID_CODE).getMessage()));
            }
        }

        ServerWebExchange nExchange;
        if (nRequest != null) {
            nExchange = exchange.mutate().request(nRequest).build();
        } else {
            nExchange = exchange.mutate().build();
        }
        return chain.filter(nExchange);
    }
}
