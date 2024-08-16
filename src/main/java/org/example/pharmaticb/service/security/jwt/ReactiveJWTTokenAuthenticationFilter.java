package org.example.pharmaticb.service.security.jwt;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.User;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class ReactiveJWTTokenAuthenticationFilter implements WebFilter {
    private List<String> whiteList;

    public ReactiveJWTTokenAuthenticationFilter() {
        log.info("ReactiveJWTTokenAuthenticationFilter");
        this.whiteList = new ArrayList<>();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (whiteList.contains(exchange.getRequest().getPath().toString()) || whiteList.contains(exchange.getRequest().getPath().pathWithinApplication().toString())) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (String)securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    if (ObjectUtils.isEmpty(user)) {
                        return chain.filter(decorate(exchange));
                    }
                    return chain.filter(exchange);
                });
    }

    private ServerWebExchange decorate(ServerWebExchange exchange) {
        final ServerHttpRequest decorated = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer>   getBody() {
                return super.getBody().collectList()
                        .flatMapMany(dataBuffers -> {
                            StringBuilder requestBody = new StringBuilder();
                            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                dataBuffers.forEach(dataBuffer -> {
                                        try {
                                            Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                                            requestBody.append(baos);
                                            baos.reset();
                                        } catch (IOException e) {
                                            log.error("Unable to log input stream", e);
                                        }
                                });
                            }catch (IOException e) {
                                log.error("Unable to close ByteArrayOutputStream", e);
                            }
                            //todo: map app user data
                            return Flux.fromIterable(dataBuffers);
                        });
            }
        };

        return new ServerWebExchangeDecorator(exchange) {
            @Override
            public ServerHttpRequest getRequest() {return decorated;}
        };
    }
}
