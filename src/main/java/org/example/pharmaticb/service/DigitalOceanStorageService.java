package org.example.pharmaticb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class DigitalOceanStorageService {
    private final S3AsyncClient s3AsyncClient;
    private static final int MAX_RETRIES = 3;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(1);

    public Mono<String> uploadFile(String key, Flux<DataBuffer> content, long contentLength, String contentType, String bucketName, String cdnEndPoint) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .contentLength(contentLength)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        return content
                .collectList()
                .flatMap(dataBuffers -> {
                    ByteBuffer combined = ByteBuffer.allocate((int) contentLength);
                    dataBuffers.forEach(dataBuffer -> {
                        ByteBuffer byteBuffer = dataBuffer.toByteBuffer();
                        combined.put(byteBuffer);
                        DataBufferUtils.release(dataBuffer);
                    });
                    combined.flip();

                    String md5 = calculateMD5(combined);
                    log.debug("Calculated MD5: {}", md5);

                    AsyncRequestBody requestBody = AsyncRequestBody.fromByteBuffer(combined);

                    return Mono.fromFuture(() -> s3AsyncClient.putObject(request, requestBody))
                            .retryWhen(Retry.backoff(MAX_RETRIES, RETRY_DELAY)
                                    .filter(throwable -> isRetryableException(throwable))
                                    .doBeforeRetry(retrySignal ->
                                            log.warn("Retrying upload. Attempt: {}", retrySignal.totalRetries() + 1))
                            )
                            .doOnError(error -> log.error("Error uploading file: {}", error.getMessage()))
                            .thenReturn(getCDNEndPoint(cdnEndPoint,key));
                });
    }

    private String getPublicUrl(String endPoint, String bucketName, String key) {
        return String.format("https://%s.%s/%s", bucketName, endPoint, key);
    }

    private String getCDNEndPoint(String cdnEndPoint, String key) {
        return cdnEndPoint + key;
    }


    private boolean isRetryableException(Throwable throwable) {
        return throwable instanceof Exception &&
                !(throwable.getMessage().contains("checksum") ||
                        throwable.getMessage().contains("Data read has a different checksum"));
    }

    private String calculateMD5(ByteBuffer buffer) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(buffer);
            byte[] digest = md.digest();
            buffer.rewind();  // Reset buffer position for later use
            return "";
        } catch (Exception e) {
            log.error("Error calculating MD5: {}", e.getMessage());
            return "";
        }
    }

//    public Mono<String> uploadFile(String key, Flux<DataBuffer> content, long contentLength, String contentType, String bucketName, String endPoint) {
//        PutObjectRequest request = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .contentType(contentType)
//                .metadata(Map.of("Content-Length", String.valueOf(contentLength)))
//                .build();
//
//        Flux<ByteBuffer> byteBufferFlux = content
//                .map(dataBuffer -> {
//                        // Create a new ByteBuffer from the DataBuffer's content
//                        ByteBuffer byteBuffer = dataBuffer.toByteBuffer();
//                        ByteBuffer copy = ByteBuffer.allocate(dataBuffer.readableByteCount());
//                        copy.put(byteBuffer);
//                        copy.flip();
//                        DataBufferUtils.release(dataBuffer);
//                        return copy;
//                });
//
//        AsyncRequestBody requestBody = AsyncRequestBody.fromPublisher(byteBufferFlux);
//
//        return Mono.fromFuture(s3AsyncClient.putObject(request, requestBody))
//                .retryWhen(Retry.backoff(MAX_RETRIES, RETRY_DELAY)
//                        .filter(throwable -> throwable instanceof Exception)
//                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()))
//                .thenReturn(endPoint + "/" + bucketName + "/" + key);
//    }

    public Mono<byte[]> downloadFile(String key, String bucketName) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return Mono.fromFuture(s3AsyncClient.getObject(request, AsyncResponseTransformer.toBytes()))
                .map(ResponseBytes::asByteArray);
    }

    public Mono<Void> deleteFile(String key, String bucketName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();
        return Mono.fromFuture(s3AsyncClient.deleteObject(request)).then();
    }
}
