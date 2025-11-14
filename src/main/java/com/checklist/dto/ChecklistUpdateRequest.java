package com.checklist.dto;

import lombok.Getter;

@Getter
public class ChecklistUpdateRequest {
    private String content;
    private String description;
}