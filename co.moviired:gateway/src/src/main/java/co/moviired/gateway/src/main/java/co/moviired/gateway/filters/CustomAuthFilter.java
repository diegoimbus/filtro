package co.moviired.gateway.filters;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.gateway.domain.dto.authentication.Request;
import co.moviired.gateway.domain.dto.authentication.Response;
import co.moviired.gateway.properties.AuthenticationProperties;
import co.moviired.gateway.properties.PathsProperties;
import co.moviired.gateway.provider.RedisProvider;
import co.moviired.gateway.utils.CorrelationIdGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public final class CustomAuthFilter implements GlobalFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String CREDENTIALS_NOT_FOUND = "Credenciales no encontradas";
    private final RedisProvider redisProvider;
    private final CorrelationIdGenerator correlationIdGenerator;
    private final ObjectMapper objectMapper;
    private final PathsProperties pathsProperties;
    private final AuthenticationProperties authenticationProperties;
    private final ReactiveConnector authenticationClient;

    public CustomAuthFilter(@NotNull AuthenticationProperties pauthenticationProperties,
                            @NotNull RedisProvider predisProvider,
                            @NotNull CorrelationIdGenerator pcorrelationIdGenerator,
                            @NotNull PathsProperties ppathsProperties,
                            @Qualifier("authenticationClient") @NotNull ReactiveConnector pauthenticationClient) {
        super();
        this.redisProvider = predisProvider;
        this.correlationIdGenerator = pcorrelationIdGenerator;
        this.pathsProperties = ppathsProperties;
        this.authenticationProperties = pauthenticationProperties;
        this.authenticationClient = pauthenticationClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Establecer el CorrelationID
        String[] sheaders = new String[]{asignarCorrelativo(exchange)};
        exchange.getRequest().mutate().header("correlationId", sheaders).build();

        // La ruta solicitada
        String requestPath = exchange.getRequest().getPath().toString();
        log.info("Requested path -----> {}", requestPath);

        // Rutas libres de autenticación
        for (String wpath : pathsProperties.getWhiteList()) {
            if (requestPath.contains(wpath)) {
                return chain.filter(exchange);
            }
        }

        // Rutas con autenticación requerida
        HttpHeaders headers = exchange.getRequest().getHeaders();
        StringBuilder authorizationHeader = new StringBuilder();
        List<String> authorizations = headers.get(AUTHORIZATION);
        if ((authorizations != null) && (!authorizations.isEmpty())) {
            authorizationHeader.append(authorizations.get(0));
        }

        if (!authorizationHeader.toString().toLowerCase().contains("bearer")) {
            if (authorizationHeader.toString().isEmpty() || (authorizationHeader.toString().split(":").length != 2)) {
                return customErrorResponse(exchange, HttpStatus.BAD_REQUEST, CREDENTIALS_NOT_FOUND);
            }
            return loginSpringAuth(exchange, chain, authorizationHeader, requestPath);
        }

        // Se busca el token en cache
        StringBuilder accessToken = new StringBuilder(authorizationHeader.toString().split(" ")[1]);
        String redisFetchToken = redisProvider.getBy("access:" + accessToken);
        String arrayCredentialsRole = redisProvider.getBy(accessToken.toString());
        log.info("Array credentials role ------------------> {}", arrayCredentialsRole);
        if (redisFetchToken == null || redisFetchToken.equals("")) {
            return customErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "The token: " + accessToken + " has expired. ");
        }

        if (arrayCredentialsRole.trim().isEmpty()) {
            return customErrorResponse(exchange, HttpStatus.BAD_REQUEST, CREDENTIALS_NOT_FOUND);
        }

        log.debug("In line 270 arrayCredentialsRole is not null");
        byte[] decodedToken = Base64.getDecoder().decode(arrayCredentialsRole);
        String tokenString = new String(decodedToken);
        String[] credentialsArray = tokenString.split(":");

        if (credentialsArray[2] == null) {
            return customErrorResponse(exchange, HttpStatus.BAD_REQUEST, "Users rol do not exist");
        }

        return filterEnd(exchange, chain, credentialsArray[2], credentialsArray, requestPath);
    }

    public Mono<Void> filterEnd(ServerWebExchange exchange,
                                GatewayFilterChain chain,
                                String role,
                                String[] credentialsArray,
                                String requestPath) {

        // Verificar Profile Activo
        String profileActive = "," + redisProvider.getBy("LOADED_MOVII_PROFILES");
        log.info("ProfileActive  -------> {}", profileActive.contains("," + role + ","));
        if (!profileActive.contains("," + role + ",")) {
            log.error("Perfil del usuario no activo");
            return customErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Perfil del usuario no activo");
        }

        // Busca PATH por profiles
        String authorizedPaths = redisProvider.getBy(role);
        log.info("Authorized paths ------------> {}", authorizedPaths);
        if (authorizedPaths == null) {
            return customErrorResponse(exchange, HttpStatus.BAD_REQUEST, "Not Paths For the role " + role);
        }

        Pattern pathPaterns = Pattern.compile(authorizedPaths);
        Matcher matcher = pathPaterns.matcher(requestPath);
        log.info("patternMatch  -------> {}", matcher.matches());
        if (!matcher.matches()) {
            log.error("The user  has no authorization for: " + requestPath);

            return customErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "The user  has no authorization for: " + requestPath);

        }

        String[] sheaders = new String[]{credentialsArray[3] + ":" + credentialsArray[4]};
        exchange.getRequest().mutate().header(AUTHORIZATION, sheaders).build();
        log.info("Authorization header after mutate before continue ------->");
        ServerHttpRequest request = exchange.getRequest().mutate().header(CustomAuthFilter.AUTHORIZATION, sheaders).build();
        return chain.filter(exchange.mutate().request(request).build());
    }


    public Mono<Void> customErrorResponse(ServerWebExchange exchange, HttpStatus httpStatus, String errorMesagge) {
        Map<String, String> mapError = new HashMap<>();
        mapError.put("message:", errorMesagge);

        try {
            DataBuffer db = new DefaultDataBufferFactory().wrap(objectMapper.writeValueAsBytes(mapError));
            exchange.getResponse().setStatusCode(httpStatus);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(Mono.just(db));

        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }

        return Mono.empty();
    }

    // Crear el CorrelationID
    String asignarCorrelativo(@NotNull ServerWebExchange exchange) {
        List<String> cId = exchange.getRequest().getHeaders().get("correlationId");
        String correlation;

        if (cId == null || cId.isEmpty()) {
            String requestOriginIp = "127.0.0.1";
            if (exchange.getRequest().getRemoteAddress() != null) {
                requestOriginIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            }
            correlation = correlationIdGenerator.generateCorrelationId(requestOriginIp);
        } else {
            correlation = cId.get(0);
        }

        MDC.putCloseable("correlation-id", correlation);
        MDC.putCloseable("component", "gateway");
        return correlation;
    }


    Mono<Void> loginSpringAuth(ServerWebExchange exchange,
                               GatewayFilterChain chain,
                               StringBuilder authorizationHeader,
                               String requestPath) {

        return Mono.just(new Request()).flatMap(request -> {
            request.setUserLogin(authorizationHeader.toString().split(":")[0]);
            request.setPin(authorizationHeader.toString().split(":")[1]);
            request.setImei("IMEI-GATEWAY");
            request.setSource("CHANNEL");
            request.setChannel("GATEWAY");

            Map<String, String> header = new HashMap<>();
            header.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);

            return authenticationClient.exchange(HttpMethod.POST, this.authenticationProperties.getUrl(), request, String.class, MediaType.APPLICATION_JSON, header);
        }).flatMap(resp -> {
            Response loginResponse;
            try {
                Gson gson = new Gson();
                loginResponse = gson.fromJson((String) resp, Response.class);
            } catch (Exception e) {
                loginResponse = new Response();
                loginResponse.setErrorCode("99");
            }
            if (!loginResponse.getErrorCode().equals("00")) {
                return customErrorResponse(exchange, HttpStatus.BAD_REQUEST, "Usuario y password incorrectos");
            }

            String[] credentialsArray = new String[5];
            credentialsArray[0] = authorizationHeader.toString().split(":")[0];
            credentialsArray[1] = authorizationHeader.toString().split(":")[1];
            credentialsArray[2] = loginResponse.getUser().getUserType();
            credentialsArray[3] = authorizationHeader.toString().split(":")[0];
            credentialsArray[4] = authorizationHeader.toString().split(":")[1];

            return filterEnd(exchange, chain, loginResponse.getUser().getUserType(), credentialsArray, requestPath);
        });
    }

}

