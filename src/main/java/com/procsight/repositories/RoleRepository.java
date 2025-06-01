package com.procsight.repositories;

import com.procsight.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByNameAndCompanyId(String name, String companyId);

    @Query("SELECT r FROM Role r WHERE r.company.id = :companyId")
    List<Role> findByCompanyId(@Param("companyId") String companyId);

    @Query("SELECT r FROM Role r WHERE r.isSystemRole = true")
    List<Role> findSystemRoles();
}