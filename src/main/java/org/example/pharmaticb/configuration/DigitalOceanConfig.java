package org.example.pharmaticb.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.net.URI;
import java.time.Duration;

@Configuration
public class DigitalOceanConfig {
    @Value("${do.spaces.key}")
    private String accessKey;

    @Value("${do.spaces.secret}")
    private String secretKey;

    @Value("${do.spaces.endpoint}")
    private String endpoint;

    @Value("${do.spaces.region}")
    private String region;

    @Bean
    public S3AsyncClient s3Client() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3AsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .httpClientBuilder(NettyNioAsyncHttpClient.builder()
                        .maxConcurrency(100)
                        .readTimeout(Duration.ofSeconds(30)))
                .build();
    }

}
