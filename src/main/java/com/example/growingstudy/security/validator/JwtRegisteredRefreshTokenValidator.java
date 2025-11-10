package com.example.growingstudy.security.validator;

import com.example.growingstudy.auth.repository.RefreshTokenRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtRegisteredRefreshTokenValidator implements OAuth2TokenValidator<Jwt> {
    private final RefreshTokenRepository repository;

    public JwtRegisteredRefreshTokenValidator(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    OAuth2Error error = new OAuth2Error("invalid_token", "등록되지 않은 리프레쉬 토큰", null);

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (token.getClaim("type").equals("refresh")) {
            if (!repository.existsById(token.getId())) {
                return OAuth2TokenValidatorResult.failure(error);
            }
        }
        return OAuth2TokenValidatorResult.success();
    }
}
