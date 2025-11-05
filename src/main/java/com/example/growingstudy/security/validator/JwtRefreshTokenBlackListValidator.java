package com.example.growingstudy.security.validator;

import com.example.growingstudy.auth.repository.RefreshTokenBlackListRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtRefreshTokenBlackListValidator implements OAuth2TokenValidator<Jwt> {
    private final RefreshTokenBlackListRepository repository;

    public JwtRefreshTokenBlackListValidator(RefreshTokenBlackListRepository repository) {
        this.repository = repository;
    }

    OAuth2Error error = new OAuth2Error("invalid_token", "토큰이 블랙리스트에 있음", null);

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (token.getClaim("type").equals("refresh")) {
            if (repository.existsById(token.getId())) {
                return OAuth2TokenValidatorResult.failure(error);
            }
        }
        return OAuth2TokenValidatorResult.success();
    }
}
