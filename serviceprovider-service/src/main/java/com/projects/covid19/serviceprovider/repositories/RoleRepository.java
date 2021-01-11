package com.projects.covid19.serviceprovider.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.covid19.serviceprovider.models.Role;
import com.projects.covid19.serviceprovider.utils.RoleType;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Optional<Role> findByType(RoleType type);

}
