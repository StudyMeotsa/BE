package com.example.growingstudy.coffee.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoffeeLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long level;
    private Integer requiredBeans;
    private String imagePath;
}
