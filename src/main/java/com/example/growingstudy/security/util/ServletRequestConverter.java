package com.example.growingstudy.security.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// HttpServletRequest 객체 내 요청 문자열 추출 및 JSON 변환
public class ServletRequestConverter {

    public String convertRequestToString(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        return messageBody;
    }

    public <T> T mapJsonToDto(String json, Class<T> dtoClass) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        T dto = mapper.readValue(json, dtoClass);
        return dto;
    }
}
