package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.dto.MyCoffeeResponseDto;
import com.example.growingstudy.auth.repository.MyCoffeesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 커피 도감에서 사용되는 서비스
 */
@Service
public class MyCoffeesService {

    private final MyCoffeesRepository myCoffeesRepository;

    @Autowired
    public MyCoffeesService(MyCoffeesRepository myCoffeesRepository) {
        this.myCoffeesRepository = myCoffeesRepository;
    }

    /**
     * 유저 id에 해당하는 진행중, 완료 스터디 목록과 그 스터디의 커피 현황 조회
     * @param accountId 유저 id
     * @return 진행중, 완료 스터디 목록 및 그 스터디의 커피 현황
     */
    public Map<String, List<MyCoffeeResponseDto>> getMyCoffees(long accountId) {
        List<MyCoffeeResponseDto> myCoffees = myCoffeesRepository.findMyCoffees(accountId);

        Map<String, List<MyCoffeeResponseDto>> myCoffeesMap = myCoffees.stream().collect(
                Collectors.groupingBy(
                        (element) -> element.getStatus()
                )
        );

        // 목록이 비었을 경우 빈 배열 반환
        myCoffeesMap.putIfAbsent("inProgress", List.of());
        myCoffeesMap.putIfAbsent("finished", List.of());

        return myCoffeesMap;
    }
}
