package com.example.growingstudy.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

    @Bean
    public S3Client s3Client(
//            @Value("${aws.access-key-id}") String accessKey,    // 로컬용
//            @Value("${aws.secret-access-key}") String secretKey     // 로컬용
    ) {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
//                .credentialsProvider(
//                        StaticCredentialsProvider.create(
//                                AwsBasicCredentials.create(accessKey, secretKey)
//                                )
//                )   // 로컬용
                // 로컬: ~/.aws/credentials
                // EC2: IAM Role
                .build();
    }


}
