package net.proselyte.springsecuritydemo.config;

import net.proselyte.springsecuritydemo.security.JwtConfigurer;
import net.proselyte.springsecuritydemo.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration//помечаем класс, который предоставляет конфигурацию бинов, которые должны быть добавлены
//в контейнер при запуске приложения
@EnableWebSecurity//помечаем класс источником настройки правил безопасности приложения
@EnableGlobalMethodSecurity(prePostEnabled = true)//разрешаем использовать методы безопасности.
// В нашем случае разрешаем включить @PreAuthorize
public class SecurityConfig {

    private final JwtConfigurer jwtConfigurer;
    private final UserDetailsServiceImpl userDetailsService;
    public SecurityConfig (JwtConfigurer jwtConfigurer, UserDetailsServiceImpl userDetailsService) {
        this.jwtConfigurer = jwtConfigurer;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // отключаем csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // отключаем сессии
                .and()
                .authorizeHttpRequests() // начинаем настройку запросов, которые требуют авторизации
                .requestMatchers("/").permitAll() // к корню проекта доступ имеют все и пользователи и не пользователи системы
                .requestMatchers("/api/v1/auth/login").permitAll()
                .anyRequest() // каждый запрос
                .authenticated() // должен быть аутентифицирован
                .and()
                .apply(jwtConfigurer); // применяем настройки которые прописаны в этом jwtConfigurer
        return http.build();
    }

    // Этот бин отвечает за кодировку пароля по алгоритму BCrypt
    // Декодировать защированную этим алгоритмом строку практически невозможно
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // Создаем компонент отвечающийц за конфигурацию пользователя AuthenticationManager
    @Bean
    public AuthenticationManager daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();//используется для проверки аутентификационных данных пользователя в базе данных
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());//передаем пароль
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);//указываем от куда получить инфу о пользователе
        return new ProviderManager(daoAuthenticationProvider);//выполняем аунтификацию пользователя
    }
}
