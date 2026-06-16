package com.ecomerce.sb_ecom.repositories;

import com.ecomerce.sb_ecom.enums.AppRole;
import com.ecomerce.sb_ecom.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole roleName);

}
