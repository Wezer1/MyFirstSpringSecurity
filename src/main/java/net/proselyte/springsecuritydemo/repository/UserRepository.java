package net.proselyte.springsecuritydemo.repository;


import net.proselyte.springsecuritydemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//имплементация JpaRepository предоставляет нам возможность использования методы операций CRUD
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
