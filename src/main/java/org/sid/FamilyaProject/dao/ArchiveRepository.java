package org.sid.FamilyaProject.dao;

import org.sid.FamilyaProject.entities.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArchiveRepository extends JpaRepository<Archive, Long>  {
	
	
	
	
	 @Query(value="SELECT  SUM(`benefice_genere`) AS Total_des_interets FROM archive", nativeQuery=true)
	 Double totalBenefitInArchive();
	
	
	

}
