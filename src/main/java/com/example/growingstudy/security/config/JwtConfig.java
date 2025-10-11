package com.example.growingstudy.security.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class JwtConfig {

    @Value("classpath:private_key.pem")
    private Resource privateKeyPem;

    @Value("classpath:public_key.pem")
    private Resource publicKeyPem;

    @Bean
    JwtEncoder jwtEncoder() throws IOException {
        RSAPrivateKey rsaPrivateKey = getRsaPrivateKey(privateKeyPem);
        RSAPublicKey rsaPublicKey = getRsaPublicKey(publicKeyPem);

        RSAKey rsaKey =
                new RSAKey
                        .Builder(rsaPublicKey)
                        .privateKey(rsaPrivateKey)
                        .keyID(UUID.randomUUID().toString())
                        .build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));

        return new NimbusJwtEncoder(jwkSource);
    }

    private RSAPrivateKey getRsaPrivateKey(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            return RsaKeyConverters.pkcs8().convert(is);
        }
    }

    private RSAPublicKey getRsaPublicKey(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            return RsaKeyConverters.x509().convert(is);
        }
    }
}
