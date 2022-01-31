package org.sid.FamilyaProject.dao;

import org.sid.FamilyaProject.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long>{

	
	@Query(value="SELECT * FROM  auth_role  Where auth_role.role_name = :role",nativeQuery=true)
	public Role findByRoleName(@Param(value="role") String role);

}
