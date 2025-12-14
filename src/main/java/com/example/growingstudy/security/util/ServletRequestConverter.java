package com.example.growingstudy.security.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * HTTP 서블릿 요청 객체 내 요청 본문 추출 및 JSON 문자열의 객체 매핑 기능을 제공하는 컨버터
 */
public class ServletRequestConverter {

    /**
     * HTTP 서블릿 요청 객체에서 요청 본문을 추출함
     * @param request HTTP 서블릿 요청 객체
     * @return 문자열 형태로 추출된 요청 본문
     */
    public static String convertRequestToString(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        return messageBody;
    }

    /**
     * JSON 문자열을 객체로 매핑
     * @param json JSON 문자열
     * @param dtoClass 매핑 대상 객체 클래스
     * @return 매핑된 객체
     * @param <T> 매핑 대상 객체 타입
     */
    public static <T> T mapJsonToDto(String json, Class<T> dtoClass) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        T dto = mapper.readValue(json, dtoClass);
        return dto;
    }
}
