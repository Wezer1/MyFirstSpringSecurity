package net.proselyte.springsecuritydemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration//помечаем класс, который предоставляет конфигурацию бинов, которые должны быть добавлены
//в контейнер при запуске приложения
@EnableWebSecurity//помечаем класс источником настройки правил безопасности приложения
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests//позволяет задать правила, кто и как может обращаться к URL в прложении
                (authorize -> authorize
                        .requestMatchers("/**").permitAll()//позволяет получить доступ к любому URL начинаещегося с /
                        .anyRequest()//работает для любого запроса не прошедшего проверку
                        .authenticated()//говорит о необходимости аунтификации пользователя, прошедшего проверку
                );
        return http.build();
    }

    @Bean
    public UserDetailsManager configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        return new InMemoryUserDetailsManager(
                User.builder()//начинаем создавать пользователя с помощью метода builder()
                        .username("admin")//присваиваем логин
                        .password(passwordEncoder().encode("admin"))//присваиваем пароль через кодировку
                        .roles("ADMIN")//присваиваем роль
                        .build()//создаем user на основе выданных данных
        );
    }

    @Bean
    protected PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);//Возвращаем BCrypt кодировку 12 силы
    }
}
