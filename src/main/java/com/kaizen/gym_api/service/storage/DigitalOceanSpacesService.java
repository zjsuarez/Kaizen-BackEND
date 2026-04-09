package com.kaizen.gym_api.service.storage;

import com.kaizen.gym_api.config.DigitalOceanSpacesProperties;
import com.kaizen.gym_api.exception.CloudStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DigitalOceanSpacesService {

    private final S3Client s3Client;
    private final DigitalOceanSpacesProperties spacesProperties;

    public String uploadProgressPhoto(String userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Progress photo file is required");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Progress photo must be a valid image file");
        }

        String objectKey = buildObjectKey(userId, file.getOriginalFilename());
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(spacesProperties.getBucket())
                .key(objectKey)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType(contentType)
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
            return buildPublicUrl(objectKey);
        } catch (S3Exception | SdkClientException | IOException ex) {
            throw new CloudStorageException("Failed to upload progress photo to cloud storage", ex);
        }
    }

    private String buildObjectKey(String userId, String originalFilename) {
        String safeFilename = originalFilename == null ? "photo" : originalFilename.replaceAll("\\s+", "_");
        return "body-measurements/" + userId + "/" + LocalDate.now() + "/" + UUID.randomUUID() + "-" + safeFilename;
    }

    private String buildPublicUrl(String objectKey) {
        URI endpointUri = URI.create(spacesProperties.getEndpoint());
        String host = endpointUri.getHost();
        String scheme = endpointUri.getScheme() == null ? "https" : endpointUri.getScheme();
        String bucket = spacesProperties.getBucket();
        if (host != null && host.startsWith(bucket + ".")) {
            return scheme + "://" + host + "/" + objectKey;
        }
        return scheme + "://" + bucket + "." + host + "/" + objectKey;
    }
}
