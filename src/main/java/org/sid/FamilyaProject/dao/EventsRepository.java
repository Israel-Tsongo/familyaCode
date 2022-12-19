package org.sid.FamilyaProject.dao;

import java.util.Date;
import java.util.List;

import org.sid.FamilyaProject.entities.Debiteur;
import org.sid.FamilyaProject.entities.Events;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Payement;
import org.sid.FamilyaProject.web.RemboursementController;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EventsRepository extends JpaRepository<Events, Long>{
	
		@Query(value= "SELECT * FROM `events`  INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule=:matricule", nativeQuery=true )
		public Page<Events> getEventsByMatricule(@Param("matricule") String matricule, org.springframework.data.domain.Pageable pageable);
		
		
		@Query(value= "SELECT * FROM `events` INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule=:matricule", nativeQuery=true )
		public List<Events> getEventByMatricule(@Param("matricule") String matricule);
		
		@Query(value="SELECT * FROM events  WHERE id_event = ?1", nativeQuery=true )
		public Events getRemboursementById(Long id);
	
	
	   @Query(value="SELECT events.id_event, code, matricule, dette,echeance_courant,remboursement_courant,montant_restant, interet_partiel,date_event FROM events INNER JOIN member ON member.id_member IN  (events.foreign_key_for_members) INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id", nativeQuery=true)
	   Page <List<List<Object>>> historyRemboursementDette(org.springframework.data.domain.Pageable pageable);
	
	   @Query(value="SELECT id_event, code, matricule, dette,remboursement_courant,montant_restant,echeance_courant,interet_partiel,date_event, prochain_montant FROM `events` INNER JOIN `member` ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id", nativeQuery=true)
	   Page <List<List<Object>>> RemboursementDetteTable(org.springframework.data.domain.Pageable pageable);
	
	   
	   @Query(value= "SELECT  CASE    WHEN  NOT EXISTS (SELECT * FROM events  INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule= ?1) THEN 'false'  ELSE 'true'  END", nativeQuery=true )
		public Boolean matricIsExist(String matric);
		
	   @Query(value="SELECT   echeance_courant FROM events INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule = ?1", nativeQuery=true)
		List<Double> getEcheanceCourantByMatricule(String matric);
         
	   @Query(value="SELECT   dette FROM events  INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule = ?1", nativeQuery=true)	   
		List<Double> getDetteByMatricule(String matric);
	   
	   @Query(value= "SELECT date_event FROM events INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule=:matricule", nativeQuery=true )
		public List<String> getDateEventByMatricule(@Param("matricule") String matricule);
	   
	   @Query(value="SELECT   montant_restant FROM events INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule = ?1", nativeQuery=true)
		List<Double> getMontantRestantByMatricule(String matric);	   
	   
	    @Modifying
		@Transactional
		@Query(value="UPDATE events e SET e.remboursement_courant =:montant, e.date_event=:date, WHERE e.id_event=:idRemb",nativeQuery=true)
		void updateRembourse(@Param("idRemb") Long idRemb,  @Param("montant")  double montant, @Param("date") Date date );
		
	   
	   @Query(value="SELECT matricule, SUM(`interet_partiel`) AS Somme_des_interets FROM events  INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule IN (SELECT matricule FROM events INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE montant_restant IN (0)) GROUP BY matricule", nativeQuery=true)
		List<Double> generetedBenefitByEachMember();
	   
	   @Query(value="SELECT  SUM(`interet_partiel`) AS Total_des_interets FROM events INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule  IN (SELECT matricule FROM events INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE montant_restant IN (0)) ", nativeQuery=true)
		Double getTotalGeneretedFakeBenefitByAllMember();
	   
	   
	   @Query(value="SELECT  SUM(`interet_partiel`) AS Total_des_interets FROM events  INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule =?1", nativeQuery=true)
		Double totalBenefitByMatricule(String matricule);
	   
	   @Query(value="SELECT  SUM(`interet_partiel`) FROM events", nativeQuery=true)
		Double getTotalGeneretedBenefitByAllMember();
	   
	   @Query(value="SELECT  SUM(`remboursement_courant`)  FROM events", nativeQuery=true)
		Double getTotalRemboursement();
	   
	   
	   @Query(value= "SELECT * FROM events INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member = auth_user.auth_user_id  WHERE matricule=:matricule WHERE  AND  date_event LIKE %:mc%", nativeQuery=true )
	   public Page<Events> findByDateRemboursementsContains(@Param("matricule")String matricule , String mc, org.springframework.data.domain.Pageable pageable );
	   
	   @Query(value= "SELECT * FROM events INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member = auth_user.auth_user_id  WHERE matricule=:matricule AND  date_event LIKE %:mc%", nativeQuery=true )
	   public List<Events> findByDateRemboursementsContainsList(@Param("matricule")String matricule , String mc );
		
	   @Query(value= "SELECT * FROM `events`  INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule  LIKE %:mc%", nativeQuery=true )
	   public Page <Events> findEnteredMatriculeContains(String mc, org.springframework.data.domain.Pageable pageable);
       
	   @Query(value= "SELECT * FROM `events`  INNER JOIN member ON member.id_member=events.foreign_key_for_members INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule  LIKE %:mc%", nativeQuery=true )
	   public List<Events> findEnteredMatriculeContains(String mc);
	   
}
