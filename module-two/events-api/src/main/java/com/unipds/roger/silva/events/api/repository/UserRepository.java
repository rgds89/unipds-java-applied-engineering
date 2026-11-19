package com.unipds.roger.silva.events.api.repository;

import com.unipds.roger.silva.events.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);

    User findByEmail(String email);
}
