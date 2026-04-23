package com.kaizen.gym_api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(DigitalOceanSpacesProperties.class)
public class DigitalOceanSpacesConfig {

    @Bean
    public S3Client s3Client(DigitalOceanSpacesProperties spacesProperties) {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(
                spacesProperties.getAccessKey(),
                spacesProperties.getSecretKey()
        );

        return S3Client.builder()
                .endpointOverride(URI.create(spacesProperties.getEndpoint()))
                .region(Region.of(spacesProperties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }
}
