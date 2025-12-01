package com.example.growingstudy.group.dto;

import com.example.growingstudy.group.entity.Group;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupListDto {

    private Long id;

    private String name;
    private String description;
    private String code;
    private String maxMember;

    private Long ownerId;
    private String ownerUsername;

    /**
     * Group 엔티티를 GroupListDto로 변환하는 생성자
     */
    public GroupListDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.description = group.getDescription();
        this.code = group.getCode();
        this.maxMember = group.getMaxMember();

        if (group.getOwner() != null) {
            this.ownerId = group.getOwner().getId();
            this.ownerUsername = group.getOwner().getUsername();
        }
    }
}