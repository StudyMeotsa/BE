package com.example.growingstudy.groupsub.repository;

import com.example.growingstudy.groupsub.entity.GroupNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupNoticeRepository extends JpaRepository<GroupNotice, Integer> {

    Optional<GroupNotice> findTopByMember_Group_IdOrderByCreatedAtDesc(Long groupId);
}
