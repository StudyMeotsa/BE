package com.example.growingstudy.session.repository;

import com.example.growingstudy.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByGroupId(Long groupId);
}
