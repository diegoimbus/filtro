package com.moviired.security;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;


@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthorizationFilter implements Filter {

    private static final String AUTHORIZATION = "Authorization";

    private final CryptoHelper cryptoHelperAuthorization;

    public AuthorizationFilter(@NotNull CryptoHelper pCryptoHelperAuthorization) {
        super();
        this.cryptoHelperAuthorization = pCryptoHelperAuthorization;
    }

    /**
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) {
        log.info("Iniciando: AuthorizationFilter");
    }

    /**
     * @param servletRequest
     * @param servletResponse
     * @param chain
     */
    @Override
    public void doFilter(
            @NotNull ServletRequest servletRequest,
            @NotNull ServletResponse servletResponse,
            @NotNull FilterChain chain) throws IOException, ServletException {

        MutableHttpServletRequest request = new MutableHttpServletRequest((HttpServletRequest) servletRequest);
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Verificar si viene el campo "Authorization"

        log.info("Iniciando: AuthorizationFilter");

        String authorization = request.getHeader(AUTHORIZATION);
        if (authorization != null && !authorization.trim().isEmpty()) {
            try {
                // Verificar formato Username:Password
                String[] credentials = authorization.split(":");
                if (credentials.length == 2) {
                    // Desincriptar y actualizar el Header
                    authorization = this.cryptoHelperAuthorization.decoder(credentials[0]) + ":" + this.cryptoHelperAuthorization.decoder(credentials[1]);
                    request.addHeader(AUTHORIZATION, authorization);
                }
            } catch (ParsingException e) {
                throw new ServletException("Header \"Authorization\" is INVALID...");
            }
        } else {
            throw new ServletException("Header \"Authorization\" is NECESSARY...");
        }

        // CALL next filter in the filter chain
        chain.doFilter(request, response);
    }

    /**
     *
     */
    @Override
    public void destroy() {
        log.debug("Finalizando: AuthorizationFilter");
    }
}


