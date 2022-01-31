package org.sid.FamilyaProject.dao;

import java.util.List;


import org.sid.FamilyaProject.entities.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OperationRepository extends JpaRepository<Operation,Long> {
	
	
	@Query(value= "SELECT id_operation, operation, date FROM operation", nativeQuery=true )
	public Page <List<List<Object>>> getAllOperation(org.springframework.data.domain.Pageable pageable); 
	
	 public Page <Operation> findByOperationContains(String mc, org.springframework.data.domain.Pageable pageable);

	public List<Operation> findByOperationContains(String string);

	

}
