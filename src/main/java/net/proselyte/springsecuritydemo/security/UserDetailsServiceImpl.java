package net.proselyte.springsecuritydemo.security;

import net.proselyte.springsecuritydemo.model.User;
import net.proselyte.springsecuritydemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpl")
//с помощью UserDetailsService мы получаем методы для загрузки информации о пользователе, необходимые для аунтификации
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException("User doesn`t exists"));
        return SecurityUser.fromUser(user);//преобразует объект User в объект UserDetails
    }//с помощью метода findByEmail находим пользователя и возвра
}
