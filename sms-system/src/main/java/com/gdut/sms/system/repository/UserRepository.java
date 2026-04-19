package com.gdut.sms.system.repository;

import com.gdut.sms.common.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.dept LEFT JOIN u.role WHERE u.username = :username")
    Optional<User> findByUsernameWithDeptAndRoles(String username);

    void deleteByUsername(String username);
}
