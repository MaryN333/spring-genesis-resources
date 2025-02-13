package cz.wz.marysidy.spring_genesis_resources.repository;

import cz.wz.marysidy.spring_genesis_resources.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByPersonId(String personId);
}
