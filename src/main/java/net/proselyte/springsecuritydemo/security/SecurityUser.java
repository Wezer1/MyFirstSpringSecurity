package net.proselyte.springsecuritydemo.security;

import lombok.Data;
import net.proselyte.springsecuritydemo.model.Status;
import net.proselyte.springsecuritydemo.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
//с помощью данного класса мы предоставляем данные springsecurity об нашем пользователе, необходимые для аунтификации
public class SecurityUser implements UserDetails {

    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;
    private final boolean isActive;

    public SecurityUser(String username, String password, List<SimpleGrantedAuthority> authorities, boolean isActive) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.isActive = isActive;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }//возвращаем пароль пользователя

    @Override
    public String getUsername() {
        return username;
    }//возвращаем имя пользователя

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }//упрощеная реализация, т.к. сейчас мы это не используем

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }//упрощеная реализация, т.к. сейчас мы это не используем

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }//упрощеная реализация, т.к. сейчас мы это не используем

    @Override
    public boolean isEnabled() {
        return isActive;
    }//упрощеная реализация, т.к. сейчас мы это не используем

    //с помощью данного метода мы передаем данные пользователя springSecurity из базы данных
    public static UserDetails fromUser(User user){
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getRole().getAuthorities()
        );
    }
}
