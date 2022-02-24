package org.sid.FamilyaProject.dao;

import java.util.Date;
import java.util.List;

import org.sid.FamilyaProject.entities.InteretParMembre;
import org.sid.FamilyaProject.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface InteretParMembreRepository extends JpaRepository<InteretParMembre, Long> {
	
	@Query(value= "SELECT * FROM `interet_par_membre` WHERE matricule_entered =:matricule", nativeQuery=true )
	public InteretParMembre getUserByMatricule(@Param("matricule") String matricule);
	
	@Query(value="SELECT id_interet,member.nom, member.matricule, interet_du_membre FROM `interet_par_membre`  INNER JOIN member ON member.id_member=interet_par_membre.foreign_key_interet_par_membre",nativeQuery=true)
	Page <List<List<Object>>> interetParMembre (org.springframework.data.domain.Pageable pageable); 
	
	 public Page <InteretParMembre> findByMatriculeEnteredContains(String mc, org.springframework.data.domain.Pageable pageable);
	
	@Query(value="SELECT  SUM(`interet_du_membre`) FROM `interet_par_membre`", nativeQuery=true)
	  Double totalInteretNet();
	
	@Query(value="SELECT  `interet_du_membre` FROM `interet_par_membre` WHERE matricule_entered=?1", nativeQuery=true)
	public Double getInteretDuMembreParMatricule(String matricule);
	
	@Query(value="DELETE   FROM  `interet_par_membre`  WHERE entered_matric=:matricule", nativeQuery=true)
	 public void deleteByMatricule(@Param("matricule") String matricule);
	
	
	@Query(value="SELECT interet_du_membre FROM interet_par_membre WHERE matricule_entered=:matricule",nativeQuery=true)
	public Double interetDuMembreByMatricule(@Param("matricule") String matricule);
	
	@Modifying
	@Transactional
	@Query(value="UPDATE interet_par_membre i SET i.interet_du_membre =:interet_du_membre,i.date_interet=:date WHERE i.id_interet=:id_interet",nativeQuery=true)
	void updateInteret(@Param("id_interet") Long id_interet,   @Param("interet_du_membre")  double reste, @Param("date") Date date );
     
	@Modifying
	@Transactional
	@Query(value="UPDATE interet_par_membre i SET i.interet_du_membre =:interet_du_membre WHERE matricule_entered=:matricule", nativeQuery=true)
	void updateInteretMembre(@Param("matricule") String matricule,  @Param("interet_du_membre")  double valeur);

	
	public List<InteretParMembre> findByMatriculeEnteredContains(String string);
	
	

}
