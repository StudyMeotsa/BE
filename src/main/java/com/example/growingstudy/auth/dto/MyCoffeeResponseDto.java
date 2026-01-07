package com.example.growingstudy.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyCoffeeResponseDto {

    private final String status;
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
}
