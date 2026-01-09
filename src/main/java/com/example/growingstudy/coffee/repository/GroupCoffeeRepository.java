package com.example.growingstudy.coffee.repository;

import com.example.growingstudy.coffee.entity.GroupCoffee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupCoffeeRepository extends JpaRepository<GroupCoffee, Long> {

    /**
     * 해당 그룹의 커피 현황 조회
     * @param groupId 조회할 그룹의 id
     * @return 그룹의 커피 현황
     */
    @Query("select gc from GroupCoffee gc where gc.group.id = :groupId")
    Optional<GroupCoffee> findByGroupId(Long groupId);
}
