package com.example.growingstudy.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MyCoffeeResponseDto {

    // 분류 위해 사용하되 응답엔 포함하지 않음
    @JsonIgnore private final String status;

    private final Long groupId;
    private final String groupName;
    private final CoffeeInfo coffee;

    @Getter
    @AllArgsConstructor
    public class CoffeeInfo {

        private final String type;
        private final int level;
        private final String imageUrl;
    }

    public MyCoffeeResponseDto(String status, Long groupId, String groupName, String type, int Level, String imageUrl) {
        this.status = status;
        this.groupId = groupId;
        this.groupName = groupName;
        this.coffee = new CoffeeInfo(type, Level, imageUrl);
    }
}
