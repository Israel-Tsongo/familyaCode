package org.sid.FamilyaProject.dao;

import java.util.Date;
import java.util.List;

import org.sid.FamilyaProject.entities.Debiteur;
import org.sid.FamilyaProject.entities.InteretParMembre;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Payement;
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
	
	@Query(value= "SELECT * FROM `debiteur` INNER JOIN member ON member.id_member=debiteur.foreign_key_member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule=:matricule", nativeQuery=true )
	public Debiteur getDebiteurByMatricule(@Param("matricule") String matricule);
	
	@Query(value="SELECT * FROM payement  WHERE id_debiteur = ?1", nativeQuery=true )
	public Debiteur getDebiteurById(Long id);
	
	
	@Query(value= "SELECT date_emprunt FROM `debiteur` INNER JOIN member ON member.id_member=debiteur.foreign_key_member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule=:matricule", nativeQuery=true )
	public String getDebiteurDateByMatricule(@Param("matricule") String matricule);
	
	
	@Query(value= "SELECT * FROM `debiteur`  INNER JOIN member ON member.id_member=debiteur.foreign_key_member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule  LIKE %:mc%", nativeQuery=true )
	public Page <Debiteur> findEnteredMatricContains(String mc, org.springframework.data.domain.Pageable pageable  );
	
	
	@Query(value="SELECT  somme_emprunt ,taux, duree_echeance FROM debiteur INNER JOIN member ON member.id_member=debiteur.foreign_key_member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule = ?1", nativeQuery=true)
	List<List<Double>> getDetteByMatricule(String matric);
	
	@Query(value="SELECT   duree_echeance FROM debiteur INNER JOIN member ON member.id_member=debiteur.foreign_key_member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule=?1", nativeQuery=true)
	Double getDureEcheanceByMatricule(String matric);
	
	@Query(value= "SELECT  CASE    WHEN  NOT EXISTS (SELECT * FROM debiteur  WHERE  entered_matric = ?1) THEN 0    ELSE 1   END", nativeQuery=true )
	public String matricIsExist(String matric);
	
	@Query(value="SELECT `id_debiteur`,`code`,`matricule`,`somme_emprunt`,`taux`,`dette_plus_interet`,`duree_echeance`, `dette_courante`, `type_interet` ,`current_penalite`, `date_emprunt`,premier_remboursement FROM `debiteur` INNER JOIN member ON member.id_member=debiteur.foreign_key_member  INNER JOIN auth_user ON member.user_foreign_key_in_member = auth_user.auth_user_id", nativeQuery=true )
	public Page<List<List<Object>>> getDetteWithMembers(org.springframework.data.domain.Pageable pageable);
	
	@Query(value="SELECT  `type_interet` FROM debiteur  INNER JOIN member ON member.id_member=debiteur.foreign_key_member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule=?1", nativeQuery=true )
	public  String  typeInteretByMatricule(String maticule);	
	
	
	@Query(value="SELECT dette_courante FROM debiteur INNER JOIN member ON member.id_member=debiteur.foreign_key_member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule=?1",nativeQuery=true)
	public Double detteCouranteByMatricule(String matricule);
	
	@Query(value="SELECT current_penalite FROM debiteur INNER JOIN member ON member.id_member=debiteur.foreign_key_member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule=?1",nativeQuery=true)
	public Double getCurrentPenaliteByMatricule(String matricule);
	
	@Query(value="SELECT former_penalite FROM debiteur INNER JOIN member ON member.id_member=debiteur.foreign_key_member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule=?1",nativeQuery=true)
	public Double getFormerPenaliteByMatricule(String matricule);
	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE debiteur d SET d.somme_emprunt =:montant,  d.date_emprunt=:date, d.duree_echeance=:duree_echeance, d.type_interet=:type_interet, d.dette_plus_interet=:dettePlusInteret, d.dette_courante=:detteCourante, d.premier_remboursement=:premierRemboursement WHERE d.id_debiteur=:id_debiteur",nativeQuery=true)
	void updateDebiteur(@Param("id_debiteur") Long idDeb,    @Param("montant") double montant, @Param("duree_echeance")  double Echeance, @Param("type_interet") String typeInteret , @Param("date") Date date ,@Param("detteCourante") double detteCourante, @Param("dettePlusInteret") double dettePlusInteret, @Param("premierRemboursement") double premierRemboursement );

	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE debiteur d SET d.dette_courante =:detteCourante  WHERE d.id_debiteur=:id_debiteur",nativeQuery=true)	
	void updateDetteCourante( @Param("id_debiteur") Long idDeb, @Param("detteCourante") double montant_restant);
     
	@Modifying
	@Transactional(propagation=Propagation.REQUIRES_NEW,isolation=Isolation.SERIALIZABLE)
	@Query(value="UPDATE debiteur d SET d.current_penalite =:penalite  WHERE d.id_debiteur=:idDebiteur",nativeQuery=true)	
	void updateCurrentPenalite( @Param("idDebiteur") Long idDebiteur, @Param("penalite") double penalite);

	@Modifying
	@Transactional
	@Query(value="UPDATE debiteur d SET d.former_penalite =:penalite  WHERE d.id_debiteur=:idDebiteur",nativeQuery=true)	
	void updateFormerPenalite( @Param("idDebiteur") Long idDebiteur, @Param("penalite") double penalite);

	@Query(value= "SELECT * FROM `debiteur`  INNER JOIN member ON member.id_member=debiteur.foreign_key_member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule  LIKE %:matricule%", nativeQuery=true )
	List<Debiteur> findEnteredMatricContains(@Param("matricule") String matricule);
	
	
}
