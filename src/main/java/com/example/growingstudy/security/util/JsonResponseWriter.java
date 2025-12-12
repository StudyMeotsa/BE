package com.example.growingstudy.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

// JSON 형태의 응답을 서블릿 응답 객체에 작성
public class JsonResponseWriter {

    public static void writeResponseWithDto(HttpServletResponse response, Object dto) throws IOException {
        writeResponseWithDto(response, dto, HttpStatus.OK); // 별도로 상태를 지정하지 않으면 기본값 OK
    }

    // status와 함께 객체를 서블릿 응답 본문으로 작성
    public static void writeResponseWithDto(HttpServletResponse response, Object dto, HttpStatus status) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status.value());

        mapper.writeValue(response.getWriter(), dto);
    }
}
