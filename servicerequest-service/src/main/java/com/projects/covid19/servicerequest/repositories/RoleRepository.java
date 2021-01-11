package com.projects.covid19.servicerequest.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.covid19.servicerequest.models.Role;
import com.projects.covid19.servicerequest.utils.RoleType;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Optional<Role> findByType(RoleType type);

}
