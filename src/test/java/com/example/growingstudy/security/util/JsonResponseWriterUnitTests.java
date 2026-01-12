package com.example.growingstudy.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * JsonResponseWriter 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class JsonResponseWriterUnitTests {

    @Mock
    private HttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("writeResponseWithDto - DTO만 전달 시 상태 코드 200(OK)으로 설정")
    void testWriteResponseWithDtoDefaultStatus() throws Exception {
        // given
        TestDto dto = TestDto.builder()
                .message("테스트")
                .code(123)
                .build();
        given(response.getWriter()).willReturn(printWriter);

        // when
        JsonResponseWriter.writeResponseWithDto(response, dto);

        // then
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpStatus.OK.value());

        String expectedJson = objectMapper.writeValueAsString(dto);
        assertThat(stringWriter.toString()).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("writeResponseWithDto - 상태 코드 CREATED(201)로 설정")
    void testWriteResponseWithDtoCreatedStatus() throws Exception {
        // given
        TestDto dto = TestDto.builder()
                .message("생성됨")
                .code(456)
                .build();
        given(response.getWriter()).willReturn(printWriter);

        // when
        JsonResponseWriter.writeResponseWithDto(response, dto, HttpStatus.CREATED);

        // then
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpStatus.CREATED.value());

        String expectedJson = objectMapper.writeValueAsString(dto);
        assertThat(stringWriter.toString()).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("writeResponseWithDto - 상태 코드 BAD_REQUEST(400)로 설정")
    void testWriteResponseWithDtoBadRequestStatus() throws Exception {
        // given
        TestDto dto = TestDto.builder()
                .message("에러")
                .code(0)
                .build();
        given(response.getWriter()).willReturn(printWriter);

        // when
        JsonResponseWriter.writeResponseWithDto(response, dto, HttpStatus.BAD_REQUEST);

        // then
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());

        String expectedJson = objectMapper.writeValueAsString(dto);
        assertThat(stringWriter.toString()).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("writeResponseWithDto - 복잡한 DTO 객체 JSON 변환")
    void testWriteResponseWithComplexDto() throws Exception {
        // given
        ComplexDto dto = ComplexDto.builder()
                .name("복잡한객체")
                .value(999)
                .active(true)
                .items(new String[]{"a", "b", "c"})
                .build();
        given(response.getWriter()).willReturn(printWriter);

        // when
        JsonResponseWriter.writeResponseWithDto(response, dto, HttpStatus.OK);

        // then
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpStatus.OK.value());

        String expectedJson = objectMapper.writeValueAsString(dto);
        assertThat(stringWriter.toString()).isEqualTo(expectedJson);
    }

    // 테스트용 DTO 클래스
    @Getter
    @Builder
    static class TestDto {
        private String message;
        private int code;
    }

    @Getter
    @Builder
    static class ComplexDto {
        private String name;
        private int value;
        private boolean active;
        private String[] items;
    }
}
