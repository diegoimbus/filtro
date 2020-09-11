package co.moviired.gateway.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Component
public final class RouteConfiguration {
    private static final Long NUMBER_8000 = 8000L;

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList(CorsConfiguration.ALL));
        corsConfig.setMaxAge(RouteConfiguration.NUMBER_8000);
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("PATCH");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedMethod("HEAD");
        corsConfig.addAllowedMethod("OPTIONS");

        corsConfig.addAllowedHeader("Host");
        corsConfig.addAllowedHeader("User-Agent");
        corsConfig.addAllowedHeader("X-Requested-With");
        corsConfig.addAllowedHeader("Accept");
        corsConfig.addAllowedHeader("Accept-Language");
        corsConfig.addAllowedHeader("Accept-Encoding");
        corsConfig.addAllowedHeader("Authorization");
        corsConfig.addAllowedHeader("Authentication");
        corsConfig.addAllowedHeader("Referer");
        corsConfig.addAllowedHeader("Connection");
        corsConfig.addAllowedHeader("Content-Type");
        corsConfig.addAllowedHeader("correlationId");
        corsConfig.addAllowedHeader("merchantId");
        corsConfig.addAllowedHeader("posId");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

}

