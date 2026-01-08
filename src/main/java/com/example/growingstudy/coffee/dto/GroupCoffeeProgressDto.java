package com.example.growingstudy.coffee.dto;

public record GroupCoffeeProgressDto(
        String type,
        Integer level,
        Integer requiredPerLevel,
        Integer current
) {
}
