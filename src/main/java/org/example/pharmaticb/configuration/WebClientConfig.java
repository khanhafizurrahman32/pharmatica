package org.example.pharmaticb.configuration;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient smsWebClient(@Value("${sms.url}") String smsUrl) {
        var httpClient = HttpClient
                .create(ConnectionProvider.builder("smsWebClient")
                        .maxConnections(600)
                        .pendingAcquireTimeout(Duration.ofSeconds(5))
                        .maxIdleTime(Duration.ofSeconds(1))
                        .build())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

        return WebClient.builder()
                .baseUrl(smsUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
