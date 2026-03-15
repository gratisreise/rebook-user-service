package com.example.rebookuserservice.service;

import com.example.rebookuserservice.exception.CMissingDataException;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String BUCKET_NAME;

    @Value("${aws.s3.region}")
    private String REGION;

    //이미지 업로드
    public String upload(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String contentType = file.getContentType();

        log.info("Uploading file " + fileName);
        log.info("Bucket Name: " + BUCKET_NAME);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(fileName)
            .contentType(contentType)
            .build();

        try{
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                file.getInputStream(), file.getSize()
            ));
            log.info("image upload success");
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            throw new CMissingDataException("s3 이미지 업로드에 실패했습니다.");
        }

        String result = String.format("https://%s.s3.%s.amazonaws.com/%s", BUCKET_NAME, REGION, fileName);
        if(result.isEmpty()) {
           throw new CMissingDataException("s3 이미지 url 생성 실패");
        }
        log.info("result: {}", result);
        return result;
    }

    // 이미지 삭제
    public void deleteImage(String url) {
        String fileKey = url.substring(url.lastIndexOf("/") + 1);
        log.info("fileKey: {}", fileKey);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(fileKey)
            .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

}
