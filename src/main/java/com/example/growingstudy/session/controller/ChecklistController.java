//package com.example.growingstudy.session.controller;
//
//import com.example.growingstudy.session.dto.ChecklistCreateDto;
//import com.example.growingstudy.session.dto.ChecklistResponseDto;
//import com.example.growingstudy.session.service.ChecklistService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/checklists")
//@RequiredArgsConstructor
//public class ChecklistController {
//
//    private final ChecklistService checklistService;
//
//    // 할 일 생성
//    @PostMapping
//    public ChecklistResponseDto create(@RequestBody ChecklistCreateDto dto) {
//        return ChecklistResponseDto.from(checklistService.createChecklist(dto));
//    }
//
//    // 완료 체크
//    @PatchMapping("/{id}/complete")
//    public void markComplete(@PathVariable Long id) {
//        checklistService.markComplete(id);
//    }
//
//    // 진행률 계산
////    @GetMapping("/{id}/progress")
////    public int getProgressRate(@PathVariable Long id) {
////        return checklistService.calculateProgressRate(id);
////    }
//}