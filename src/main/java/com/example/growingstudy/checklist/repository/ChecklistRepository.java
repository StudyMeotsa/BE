package com.example.growingstudy.checklist.repository;

import com.example.growingstudy.checklist.entity.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    List<Checklist> findByGroupId(Long groupId);
}