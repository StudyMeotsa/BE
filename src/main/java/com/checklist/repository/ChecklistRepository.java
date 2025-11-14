package com.checklist.repository;

import com.checklist.entity.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    
    /**
     * 특정 사용자(userId)가 생성한 모든 Checklist 목록을 조회합니다.
     * @param userId Checklist를 생성한 사용자 ID
     * @return 해당 사용자가 생성한 Checklist 목록
     */
    List<Checklist> findByUserId(Long userId);
    
    /**
     * ID를 사용하여 단일 Checklist 엔티티를 조회합니다.
     * JpaRepository에 이미 정의되어 있지만, 명시적으로 선언하여 가독성을 높였습니다.
     * @param id 조회할 Checklist ID
     * @return 해당 ID를 가진 Checklist (Optional)
     */
    Optional<Checklist> findById(Long id);
    
    // 이외에도 ChecklistService에서 권한 확인을 위해 사용되는 유용한 메서드를 추가할 수 있습니다:
    // Optional<Checklist> findByIdAndUserId(Long id, Long userId); 
}