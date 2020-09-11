package co.moviired.digitalcontent.business.conf;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

//@Component
public class StaticWebFilter implements WebFilter {

    @Override
    public final Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        if (exchange.getRequest().getURI().getPath().equals("/")) {
            return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().path("/index.jsp").build()).build());
        }

        return chain.filter(exchange);
    }
}
