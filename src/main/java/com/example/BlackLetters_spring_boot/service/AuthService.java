package com.example.BlackLetters_spring_boot.service;

import com.example.BlackLetters_spring_boot.config.security.JwtTokenProvider;
import com.example.BlackLetters_spring_boot.domain.User;
import com.example.BlackLetters_spring_boot.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String loginOrRegisterWithKakao(String kakaoId, String email, String name) {
        // DB에서 카카오 ID로 조회, 없으면 새로 회원가입 처리
        User user = userRepository.findByKakaoId(kakaoId).orElseGet(() -> {
            User newUser = User.builder()
                    .kakaoId(kakaoId)
                    .email(email)
                    .name(name)
                    .build();
            return userRepository.save(newUser);
        });

        // JWT 발급 (권한은 기본적으로 ROLE_USER 부여)
        return jwtTokenProvider.createToken(user.getUserId(), "ROLE_USER");
    }
}
