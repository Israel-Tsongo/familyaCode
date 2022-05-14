package org.sid.FamilyaProject.dao;

import java.util.List;

import org.sid.FamilyaProject.entities.Archive;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArchiveRepository extends JpaRepository<Archive, Long>  {
	
	
	
	 public Page <Archive> findByEnteredMatricContains(String mc, org.springframework.data.domain.Pageable pageable );
	
	
	 @Query(value="SELECT  SUM(`benefice_genere`) AS Total_des_interets FROM archive", nativeQuery=true)
	 Double totalBenefitInArchive();
	 
	 @Query(value="SELECT  SUM(`somme_penalty`) AS Total_des_interets FROM archive", nativeQuery=true)
	 Double totalPenalite();
	
	 @Query(value="SELECT `id_deb_archiv`,`nom`,`entered_matric`,`somme_emprunt`,`taux`,`dette_plus_interet`,`duree_echeance`, `type_interet` , `date_emprunt`,somme_penalty,benefice_genere FROM `archive`", nativeQuery=true )
     public Page<List<List<Object>>> getArchiveList(org.springframework.data.domain.Pageable pageable);


	public List<Archive> findByEnteredMatricContains(String string);
	
	@Query(value="SELECT entered_matric FROM archive",nativeQuery=true)
	public List<String> listAllMatricule();
	
	@Query(value="SELECT * FROM archive WHERE entered_matric=:matricule ",nativeQuery=true)
	public List<List<Archive>> getArchivesByMatricule(@Param("matricule") String matricule) ;

}
