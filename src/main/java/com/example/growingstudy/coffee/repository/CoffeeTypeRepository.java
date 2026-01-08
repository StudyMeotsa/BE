package com.example.growingstudy.coffee.repository;

import com.example.growingstudy.coffee.entity.CoffeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CoffeeTypeRepository extends JpaRepository<CoffeeType, Long> {

    /**
     * 레벨 컬럼이 1인 커피 종류들의 id 리스트 반환
     * @return 커피 종류 id 리스트
     */
    @Query("select ct.id from CoffeeType ct where ct.level = 1")
    List<Long> findAllIdsLevel1CoffeeType();
}
