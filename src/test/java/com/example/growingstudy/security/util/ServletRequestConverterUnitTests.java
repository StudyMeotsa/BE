package com.example.growingstudy.security.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/**
 * ServletRequestConverter 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ServletRequestConverterUnitTests {

    @Mock
    private HttpServletRequest request;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== convertRequestToString 테스트 ====================

    @Test
    @DisplayName("convertRequestToString - 요청 본문 문자열로 추출 성공")
    void testConvertRequestToStringSuccess() throws IOException {
        // given
        String requestBody = "{\"username\":\"test@example.com\",\"password\":\"password123\"}";
        ServletInputStream inputStream = createServletInputStream(requestBody);
        given(request.getInputStream()).willReturn(inputStream);

        // when
        String result = ServletRequestConverter.convertRequestToString(request);

        // then
        assertThat(result).isEqualTo(requestBody);
    }

    @Test
    @DisplayName("convertRequestToString - 빈 요청 본문 추출")
    void testConvertRequestToStringEmpty() throws IOException {
        // given
        String requestBody = "";
        ServletInputStream inputStream = createServletInputStream(requestBody);
        given(request.getInputStream()).willReturn(inputStream);

        // when
        String result = ServletRequestConverter.convertRequestToString(request);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("convertRequestToString - 한글 포함 요청 본문 추출")
    void testConvertRequestToStringWithKorean() throws IOException {
        // given
        String requestBody = "{\"name\":\"테스트유저\",\"message\":\"안녕하세요\"}";
        ServletInputStream inputStream = createServletInputStream(requestBody);
        given(request.getInputStream()).willReturn(inputStream);

        // when
        String result = ServletRequestConverter.convertRequestToString(request);

        // then
        assertThat(result).isEqualTo(requestBody);
    }

    // ==================== mapJsonToDto 테스트 ====================

    @Test
    @DisplayName("mapJsonToDto - JSON 문자열을 DTO로 매핑 성공")
    void testMapJsonToDtoSuccess() throws JsonProcessingException {
        // given
        TestDto expectedDto = TestDto.builder()
                .message("테스트메시지")
                .code(200)
                .build();
        String json = objectMapper.writeValueAsString(expectedDto);

        // when
        TestDto result = ServletRequestConverter.mapJsonToDto(json, TestDto.class);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("테스트메시지");
        assertThat(result.getCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("mapJsonToDto - 복잡한 DTO 객체 매핑 성공")
    void testMapJsonToComplexDtoSuccess() throws JsonProcessingException {
        // given
        ComplexDto expectedDto = ComplexDto.builder()
                .name("복잡한객체")
                .value(999)
                .active(true)
                .items(new String[]{"a", "b", "c"})
                .build();
        String json = objectMapper.writeValueAsString(expectedDto);

        // when
        ComplexDto result = ServletRequestConverter.mapJsonToDto(json, ComplexDto.class);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("복잡한객체");
        assertThat(result.getValue()).isEqualTo(999);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getItems()).containsExactly("a", "b", "c");
    }

    @Test
    @DisplayName("mapJsonToDto - 잘못된 JSON 문자열로 예외 발생")
    void testMapJsonToDtoWithInvalidJson() {
        // given
        String invalidJson = "{ invalid json }";

        // when & then
        assertThatThrownBy(() -> ServletRequestConverter.mapJsonToDto(invalidJson, TestDto.class))
                .isInstanceOf(JsonProcessingException.class);
    }

    @Test
    @DisplayName("mapJsonToDto - 빈 JSON 객체 매핑")
    void testMapJsonToDtoWithEmptyObject() throws JsonProcessingException {
        // given
        String emptyJson = "{}";

        // when
        TestDto result = ServletRequestConverter.mapJsonToDto(emptyJson, TestDto.class);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isNull();
        assertThat(result.getCode()).isZero();
    }

    // ==================== 헬퍼 메소드 ====================

    private ServletInputStream createServletInputStream(String content) {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 테스트용이므로 구현하지 않음
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    // ==================== 테스트용 DTO 클래스 ====================

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class TestDto {
        private String message;
        private int code;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class ComplexDto {
        private String name;
        private int value;
        private boolean active;
        private String[] items;
    }
}
