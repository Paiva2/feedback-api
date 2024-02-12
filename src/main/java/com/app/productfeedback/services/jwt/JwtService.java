package com.app.productfeedback.services.jwt;

import java.time.Instant;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.app.productfeedback.exceptions.BadRequestException;

@Service
public class JwtService {
    protected final int validationTime = 60 * 60 * 60 * 24 * 7; // 7 days
    protected final String issuer = "feedback-api";

    @Value("${api.security.token.secret}")
    protected String secretKey;

    public String sign(String subjectId) {
        String token = null;

        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secretKey);

            token = JWT.create().withIssuer(this.issuer).withSubject(subjectId)
                    .withExpiresAt(this.tokenValidationTime()).sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new BadRequestException(exception.getMessage());
        }

        return token;
    }

    public String verify(String token) {
        String decodedJWT;

        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secretKey);

            JWTVerifier verifier = JWT.require(algorithm).withIssuer(this.issuer).build();

            decodedJWT = verifier.verify(token.trim()).getSubject();
        } catch (JWTVerificationException exception) {
            throw new BadRequestException(exception.getMessage());
        }

        return decodedJWT;
    }

    protected Instant tokenValidationTime() {
        ZoneId zoneId = ZoneId.systemDefault();

        Instant expDate = Instant.now().plusMillis(validationTime).atZone(zoneId).toInstant();

        return expDate;
    }
}
