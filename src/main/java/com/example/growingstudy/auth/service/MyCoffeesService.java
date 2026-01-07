package com.example.growingstudy.auth.service;

import com.example.growingstudy.auth.dto.MyCoffeeResponseDto;
import com.example.growingstudy.auth.repository.MyCoffeesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MyCoffeesService {

    private final MyCoffeesRepository myCoffeesRepository;

    @Autowired
    public MyCoffeesService(MyCoffeesRepository myCoffeesRepository) {
        this.myCoffeesRepository = myCoffeesRepository;
    }

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
