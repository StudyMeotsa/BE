package com.example.growingstudy.auth.entity;

import com.example.growingstudy.auth.enums.SexEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // JPA의 권장 사항에 따라 id에 원시 자료형 대신 래퍼 클래스 사용
    private Long id; // H2에서 AtomicLong 오류 나서 long으로 뒀음

    private String email;
    private String password;
    private String name;
    @Enumerated(EnumType.STRING) private SexEnum sex;

    @Column(name = "image_path")
    private String imagePath;
}
