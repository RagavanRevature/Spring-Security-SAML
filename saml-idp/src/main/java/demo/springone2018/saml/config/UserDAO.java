package demo.springone2018.saml.config;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Long> {

  Optional<User> findByUserName(String userName);
}