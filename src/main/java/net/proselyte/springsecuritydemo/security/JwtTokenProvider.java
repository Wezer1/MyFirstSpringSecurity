package net.proselyte.springsecuritydemo.security;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

// Этот класс представляет собой поставщика JWT токенов
@Component
public class JwtTokenProvider {

    private final UserDetailsServiceImpl userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // при создании бина закодируем секретный ключ в Base64
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // В данном методе мы создаем токен
    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username); // Claims это своеобразная мапа для JWT
        claims.put("role", role); // кладем роль в мапу созданную на основе username
        Date now = new Date(); // получаем текущее время
        Date validity = new Date(now.getTime() + validityInMilliseconds * 1000); // расчитываем время валидности токена

        return Jwts.builder() // создаем токен
                .setClaims(claims) // добавляем claims
                .setIssuedAt(now) // указываем дату и время создания токена
                .setExpiration(validity) // указываем дату и время до которого токен валиден
                .signWith(SignatureAlgorithm.HS256, secretKey) // указываем алгоритм шифрования и секретный ключ
                .compact();
    }

    // В данном методе мы валидируем токен
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token); // вставляем секретный ключ и декодируем токен
            return !claimsJws.getBody().getExpiration().before(new Date()); // вытаскиваем дату истечения и проверяем что токен ещё не истек
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtAuthenticationException(exception.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    // получаем аунтефикацию из токена c помощью UserDetailsServiceImpl
    public Authentication getAuthentication (String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token)); // получили user details
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()); // создали авторизацию
    }

    // Получаем юзернейм
    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Служебный метод для получения хедера авторизации, в котором лежит токен
    public String resolveToken(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(header);
    }

}
