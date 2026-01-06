package com.example.growingstudy.coffee.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coffee_type",
        indexes = {
        @Index(
                name = "idx_coffee_type_name_level",
                columnList = "name, level"
        )
})
public class CoffeeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false)
    private Integer level;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    public CoffeeType(String name, Integer level, String imagePath) {
        this.name = name;
        this.level = level;
        this.imagePath = imagePath;
    }
}
