package net.proselyte.springsecuritydemo.config;

//import net.proselyte.springsecuritydemo.model.Permission;
import net.proselyte.springsecuritydemo.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
@EnableGlobalMethodSecurity(prePostEnabled = true)//разрешаем использовать методы безопасности.
// В нашем случае разрешаем включить @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()//отключаем csrf
                .authorizeHttpRequests()
                //позволяет задать правила, кто и как может обращаться к URL в прложении

                .requestMatchers("/").permitAll()//позволяет получить пользователю
//                // доступ к любому URL начинаещегося с /
//                .requestMatchers(HttpMethod.GET, "/api/**").hasAuthority(Permission.DEVELOPERS_READ.getPermission())
//                //дает доступ любому пользователю с разрешением на чтение
//
//                .requestMatchers(HttpMethod.POST, "/api/**").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())
//                .requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())
                //дает доступ любому пользователю с разрешением на внесение изменений

                .anyRequest()//работает для любого запроса не прошедшего проверку
                .authenticated()//говорит о необходимости аунтификации пользователя
                .and()
                .httpBasic();//настройка базовой аунтификации приложения
        return http.build();
    }

    @Bean
    public UserDetailsManager configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        return new InMemoryUserDetailsManager(
                User.builder()//начинаем создавать пользователя с помощью метода builder()
                        .username("admin")//присваиваем логин
                        .password(passwordEncoder().encode("admin"))//присваиваем пароль через кодировку
                        .authorities(Role.ADMIN.getAuthorities())//присваиваем роль через authorities
                        .build(),//создаем user на основе выданных данных
                User.builder()
                        .username("user")
                        .password(passwordEncoder().encode("user"))
                        .authorities(Role.USER.getAuthorities())
                        .build()
        );
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);//Возвращаем BCrypt кодировку 12 силы
    }
}
