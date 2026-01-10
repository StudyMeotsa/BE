package com.example.growingstudy.session.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${s3.path.assets}")
    private String assetPath; 

    public String uploadFile(MultipartFile file) throws IOException {
        // 1. 파일명 중복 방지를 위한 UUID 생성 및 경로 설정 (assets/UUID_파일명)
        String fileName = assetPath + UUID.randomUUID() + "_" + file.getOriginalFilename();

        // 2. 메타데이터 설정 (파일 크기 및 타입)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // 3. S3 업로드 실행
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead)); // 외부에서 읽기 가능하도록 설정

        // 4. 업로드된 파일의 전체 URL 반환
        return amazonS3.getUrl(bucket, fileName).toString();
    }
}