package com.example.growingstudy.groupsub.repository;

import com.example.growingstudy.groupsub.entity.GroupNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupNoticeRepository extends JpaRepository<GroupNotice, Integer> {

    // Todo: 가시성을 위해 native 쿼리로 작성하는 게 좋을 듯
    Optional<GroupNotice> findTopByMember_Group_IdOrderByCreatedAtDesc(Long groupId);
}
