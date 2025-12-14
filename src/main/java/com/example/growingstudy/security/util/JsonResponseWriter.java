package com.example.growingstudy.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * JSON 형태의 응답을 HTTP 서블릿 응답 객체에 작성하는 유틸리티 클래스
 */
public class JsonResponseWriter {

    /**
     * 해당 DTO의 컨텐츠로 HTTP 서블릿 응답 객체에 JSON 형태의 응답 본문을 작성하며, 상태 코드는 200(OK)으로 설정됨
     * @param response HTTP 서블릿 응답 객체
     * @param dto DTO
     */
    public static void writeResponseWithDto(HttpServletResponse response, Object dto) throws IOException {
        writeResponseWithDto(response, dto, HttpStatus.OK); // 별도로 상태를 지정하지 않으면 기본값 OK
    }

    // status와 함께 객체를 서블릿 응답 본문으로 작성

    /**
     * 해당 DTO의 컨텐츠로 HTTP 서블릿 응답 객체에 JSON 형태의 응답 본문을 작성 및 상태 코드 설정
     * @param response HTTP 서블릿 응답 객체
     * @param dto DTO
     * @param status 상태 코드 ({@link HttpStatus})
     */
    public static void writeResponseWithDto(HttpServletResponse response, Object dto, HttpStatus status) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status.value());

        mapper.writeValue(response.getWriter(), dto);
    }
}
