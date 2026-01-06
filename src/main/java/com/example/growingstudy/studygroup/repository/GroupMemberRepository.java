package com.example.growingstudy.studygroup.repository;

import com.example.growingstudy.studygroup.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
}
