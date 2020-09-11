package co.moviired.support.helper;

/*
 * Copyright @2019. MOVIIRED, S.A.S. Todos los derechos reservados.
 *
 * @author Ronel Rivas
 * @version 1, 2019-10-25
 * @since 1.0
 */

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.support.exceptions.ManagerException;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthorizationFilter implements WebFilter {

    private static final String AUTHORIZATION = "Authorization";

    private final CryptoHelper cryptoHelper;

    public AuthorizationFilter(@NotNull CryptoHelper cryptoHelper) {
        super();
        this.cryptoHelper = cryptoHelper;
    }

    @Override
    public final Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // New Request
        ServerHttpRequest nRequest = null;

        // Verificar si viene el campo "Authorization"
        List<String> authorizations = exchange.getRequest().getHeaders().get(AUTHORIZATION);
        if (authorizations != null && !authorizations.isEmpty()) {
            try {
                String authorization = authorizations.get(0);
                if(!authorization.contains("bearer")) {
                    log.debug("Authorization = {}", authorization);
                    String[] credentials = authorization.split(":");

                    // Desincriptar y actualizar el Header
                    authorization = this.cryptoHelper.decoder(credentials[0]) + ":" + this.cryptoHelper.decoder(credentials[1]);

                    // Mutar el request
                    nRequest = exchange.getRequest()
                            .mutate()
                            .header(AUTHORIZATION, authorization)
                            .build();

                    log.debug("new Authorization: {}", authorization);
                }
            } catch (ParsingException e) {
                return Mono.error(new ManagerException(0, "401", "Header 'Authorization' is INVALID..."));
            }
        }

        // Sobrescribir los datos de intercambio
        ServerWebExchange nExchange;
        if (nRequest != null) {
            // Nuevo header
            nExchange = exchange.mutate().request(nRequest).build();
        } else {
            // Original
            nExchange = exchange.mutate().build();
        }

        return chain.filter(nExchange);
    }

}

