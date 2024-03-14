package net.proselyte.springsecuritydemo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

// Это кастомный фильтр http запросов
@Component
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //создаем собственный фильтр
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);//извлекаем токен из HTTP запроса
        try {
            if (token != null && jwtTokenProvider.validateToken(token))//проверяем есть ли токен и его валидность
            {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);//получаем аунтефикацию
                if (authentication != null) //если аунтефикация не null
                {
                    SecurityContextHolder.getContext().setAuthentication(authentication);//то устанавливаем аунтефикацию
                }
            }
        } catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) servletResponse).sendError(e.getHttpStatus().value()); // если поймали ошибку то отправляем её пользователю
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
        filterChain.doFilter(servletRequest, servletResponse); // после нашей фильтрации передаем запрос дефолтному фильтру
    }
}
