package com.checklist.repository;

import com.checklist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 필요한 경우 여기에 커스텀 쿼리 메소드를 추가할 수 있습니다.
}