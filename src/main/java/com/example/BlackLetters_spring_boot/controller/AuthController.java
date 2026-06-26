package com.example.BlackLetters_spring_boot.controller;

import com.example.BlackLetters_spring_boot.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        // 안드로이드 클라이언트에서 카카오 SDK를 통해 얻어온 사용자 정보를 넘겨준다고 가정
        String token = authService.loginOrRegisterWithKakao(request.getKakaoId(), request.getEmail(), request.getName());
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @Data
    static class KakaoLoginRequest {
        private String kakaoId;
        private String email;
        private String name;
    }
}
