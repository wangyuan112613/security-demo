package com.example.securitydemo.security.jwt;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;


/**
 * jwtToken生成的工具类
 * claim:声称，宣称
 * JWT token的格式：header,payload,signature
 * header的格式（算法、token的类型）
 * {"alg": "HS512","typ": "JWT"}
 * payload的格式（用户名、创建时间、生成时间）：
 * {"sub":"wang","created":1489079981393,"exp":1489684781}
 * signature的生成算法：
 * HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 *
 */
@Slf4j
@Component
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";

    private String  key = "demo";
    private long tokenValidityInMilliseconds = 86400*1000;
    private long tokenValidityInMillisecondsForRememberMe = 259200000;

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
                                           .map(GrantedAuthority::getAuthority)
                                           .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;

        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        return Jwts.builder()
                   // 获取用户登录时输入的用户名
                   .setSubject(authentication.getName())
                   // auth :
                   .claim(AUTHORITIES_KEY, authorities)
                   .signWith(SignatureAlgorithm.HS512, key)
                   .setExpiration(validity)
                   .compact();
    }

    public Authentication getAuthentication(String token) {
        var claims = Jwts.parser()
                         .setSigningKey(key)
                         .parseClaimsJws(token)
                         .getBody();

        var authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

        var principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }
}
