package com.example.growingstudy.session.repository;

import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByIdAndGroup_Id(Long sessionId, Long groupId);
}
