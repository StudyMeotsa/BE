package com.example.growingstudy.group.service;

import com.example.growingstudy.group.entity.Group;
import com.example.growingstudy.group.repository.JpaGroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupService {

    private final JpaGroupRepository jpaGroupRepository;

    public GroupService(JpaGroupRepository jpaGroupRepository) {this.jpaGroupRepository = jpaGroupRepository;}

    /**
     * 회원 가입
     */
    public Long join(Group group) {
        validateDuplicateMember(group); // 같은 이름이 있는 중복 회원X
        jpaGroupRepository.save(group);
        return group.getId();
    }

    private void validateDuplicateMember(Group group) {
        jpaGroupRepository.findByName(group.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 그룹입니다.");
                });
    }

    /**
     * 전체 그룹 조회
     */
    public List<Group> findGroups(){
        return jpaGroupRepository.findAll();
    }

    public Optional<Group> findOne(Long Id) {
        return jpaGroupRepository.findById(Id);
    }
}
