package com.gdut.sms.system.repository;

import com.gdut.sms.common.entity.Role;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
