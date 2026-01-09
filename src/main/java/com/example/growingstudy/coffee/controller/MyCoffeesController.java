package com.example.growingstudy.coffee.controller;

import com.example.growingstudy.coffee.dto.MyCoffeeResponseDto;
import com.example.growingstudy.coffee.service.MyCoffeesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MyCoffeesController {

    private final Logger logger = LoggerFactory.getLogger(MyCoffeesController.class);
    private final MyCoffeesService myCoffeesService;

    @Autowired
    public MyCoffeesController(MyCoffeesService myCoffeesService) {
        this.myCoffeesService = myCoffeesService;
    }

    @GetMapping("/api/auth/mycoffees")
    public ResponseEntity<Map<String, List<MyCoffeeResponseDto>>> listMyCoffees(@AuthenticationPrincipal Jwt token) {
        long accountId = Long.parseLong(token.getSubject());

        Map<String, List<MyCoffeeResponseDto>> response = myCoffeesService.getMyCoffees(accountId);

        return ResponseEntity.ok(response);
    }
}
