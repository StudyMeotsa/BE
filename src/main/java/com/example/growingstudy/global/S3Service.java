package com.example.growingstudy.global;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client; 

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${s3.path.assets}")
    private String assetPath;

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String fileName = createFileName(file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 중 오류가 발생했습니다.");
        }

        // 업로드된 파일의 전체 URL 반환
        return s3Client.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build()).toString();
    }

    private String createFileName(String originalFileName) {
        return assetPath + UUID.randomUUID().toString().concat("_").concat(originalFileName);
    }
}