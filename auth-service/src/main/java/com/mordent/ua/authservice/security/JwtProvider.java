package com.mordent.ua.authservice.security;

import com.mordent.ua.authservice.model.body.response.AuthorizationResponse;
import com.mordent.ua.authservice.model.entity.User;
import com.mordent.ua.authservice.model.exception.AuthException;
import com.mordent.ua.authservice.model.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final SecurityAppConfig securityAppConfig;

    public AuthorizationResponse generateTokens(final User user) {
        Date accessTokenExpirationDate = Date.from(LocalDate.now()
                .plusDays(15)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        Date refreshTokenExpirationDate = Date.from(LocalDate.now()
                .plusDays(30)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("roles", user.getRoles());
        claims.put("enabled", user.isEnabled());
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(securityAppConfig.jwt().secret().getBytes()), SignatureAlgorithm.HS512);

        String accessToken = jwtBuilder.setExpiration(accessTokenExpirationDate).compact();
        String refreshToken = jwtBuilder.setExpiration(refreshTokenExpirationDate).compact();

        return new AuthorizationResponse(accessToken, refreshToken);
    }

    public Long getUserIdFromToken(final String bearerToken) {
        if (!hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            throw new AuthException(ErrorCode.TOKEN_NOT_VALID, "Token is not valid");
        }
        String token = bearerToken.substring(7);
        Claims claims = Jwts.parserBuilder().setSigningKey(securityAppConfig.jwt().secret().getBytes()).build()
                .parseClaimsJws(token).getBody();
        return claims.get("id", Long.class);
    }

    public boolean validateToken(final String bearerToken) {
        if (!hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            return false;
        }
        String token = bearerToken.substring(7);
        return Jwts.parserBuilder().setSigningKey(securityAppConfig.jwt().secret().getBytes()).build()
                .parseClaimsJws(token).getBody().getExpiration().after(new Date());
    }
}
