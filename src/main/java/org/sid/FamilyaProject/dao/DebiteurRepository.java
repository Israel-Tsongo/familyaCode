package org.sid.FamilyaProject.dao;

import java.util.Date;
import java.util.List;

import org.sid.FamilyaProject.entities.Debiteur;
import org.sid.FamilyaProject.entities.InteretParMembre;
import org.sid.FamilyaProject.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface DebiteurRepository extends JpaRepository<Debiteur,Long>{

	
	
	@Query(value="SELECT  SUM(somme_emprunt) FROM debiteur", nativeQuery=true)
	  Double totalEnDette();
	
	@Query(value= "SELECT * FROM `debiteur` WHERE entered_matric=:matricule", nativeQuery=true )
	public Debiteur getDebiteurByMatricule(@Param("matricule") String matricule);
	
	@Query(value= "SELECT date_emprunt FROM `debiteur` WHERE entered_matric=:matricule", nativeQuery=true )
	public String getDebiteurDateByMatricule(@Param("matricule") String matricule);
	
	
	
	 public Page <Debiteur> findByEnteredMatricContains(String mc, org.springframework.data.domain.Pageable pageable  );
	
	
	@Query(value="SELECT  somme_emprunt ,taux, duree_echeance FROM debiteur WHERE  entered_matric = ?1", nativeQuery=true)
	List<List<Double>> getDetteByMatricule(String matric);
	
	@Query(value="SELECT   duree_echeance FROM debiteur WHERE  entered_matric = ?1", nativeQuery=true)
	Double getDureEcheanceByMatricule(String matric);
	
	@Query(value= "SELECT  CASE    WHEN  NOT EXISTS (SELECT * FROM debiteur  WHERE  entered_matric = ?1) THEN 0    ELSE 1   END", nativeQuery=true )
	public String matricIsExist(String matric);
	
	@Query(value="SELECT `id_debiteur`,`nom`,`entered_matric`,`somme_emprunt`,`taux`,`dette_plus_interet`,`duree_echeance`, `dette_courante`, `type_interet` , `date_emprunt`,premier_remboursement FROM `debiteur` INNER JOIN member WHERE member.id_member=debiteur.foreign_key_member", nativeQuery=true )
	public Page<List<List<Object>>> getDetteWithMembers(org.springframework.data.domain.Pageable pageable);
	
	@Query(value="SELECT  `type_interet` FROM debiteur  WHERE entered_matric=?1", nativeQuery=true )
	public  String  typeInteretByMatricule(String maticule);	
	
	
	@Query(value="SELECT dette_courante FROM debiteur WHERE entered_matric=?1",nativeQuery=true)
	public Double detteCouranteByMatricule(String matricule);
	
	@Query(value="SELECT current_penalite FROM debiteur WHERE entered_matric=?1",nativeQuery=true)
	public Double getCurrentPenaliteByMatricule(String matricule);
	
	@Query(value="SELECT former_penalite FROM debiteur WHERE entered_matric=?1",nativeQuery=true)
	public Double getFormerPenaliteByMatricule(String matricule);
	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE debiteur d SET d.somme_emprunt =:montant, d.entered_matric=:matricule, d.date_emprunt=:date, d.duree_echeance=:duree_echeance, d.type_interet=:type_interet, d.dette_plus_interet=:dettePlusInteret, d.dette_courante=:detteCourante, d.premier_remboursement=:premierRemboursement WHERE d.id_debiteur=:id_debiteur",nativeQuery=true)
	void updateDebiteur(@Param("id_debiteur") Long idDeb,  @Param("matricule") String matricule,  @Param("montant") double montant, @Param("duree_echeance")  double Echeance, @Param("type_interet") String typeInteret , @Param("date") Date date ,@Param("detteCourante") double detteCourante, @Param("dettePlusInteret") double dettePlusInteret, @Param("premierRemboursement") double premierRemboursement );

	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE debiteur d SET d.dette_courante =:detteCourante  WHERE d.id_debiteur=:id_debiteur",nativeQuery=true)	
	void updateDetteCourante( @Param("id_debiteur") Long idDeb, @Param("detteCourante") double montant_restant);
     
	@Modifying
	@Transactional(propagation=Propagation.REQUIRES_NEW,isolation=Isolation.SERIALIZABLE)
	@Query(value="UPDATE debiteur d SET d.current_penalite =:penalite  WHERE d.entered_matric=:matricule",nativeQuery=true)	
	void updateCurrentPenalite( @Param("matricule") String matricule, @Param("penalite") double penalite);

	@Modifying
	@Transactional
	@Query(value="UPDATE debiteur d SET d.former_penalite =:penalite  WHERE d.entered_matric=:matricule",nativeQuery=true)	
	void updateFormerPenalite( @Param("matricule") String matricule, @Param("penalite") double penalite);

	
	List<Debiteur> findByEnteredMatricContains(String string);
	
	
}
