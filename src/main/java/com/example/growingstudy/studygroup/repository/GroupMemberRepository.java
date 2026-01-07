package com.example.growingstudy.studygroup.repository;

import com.example.growingstudy.studygroup.entity.GroupMember;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByAccountIdAndGroupId(long accountId, long groupId);
}
