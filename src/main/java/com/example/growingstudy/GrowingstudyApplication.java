package com.example.growingstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// 아직 유저 기능 구현하지 않았기 때문에 SecurityAutoConfiguration.class를 exclude하였음
// 추후에 유저 기능 구현한 후에 exclude 부분은 삭제
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class GrowingstudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrowingstudyApplication.class, args);
	}

}
