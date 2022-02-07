package org.sid.FamilyaProject.dao;

import java.util.Date;
import java.util.List;

import org.sid.FamilyaProject.entities.Debiteur;
import org.sid.FamilyaProject.entities.Events;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.web.RemboursementController;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EventsRepository extends JpaRepository<Events, Long>{
	
	@Query(value= "SELECT * FROM `events` WHERE entered_matricule=:matricule", nativeQuery=true )
	public List<Events> getEventByMatricule(@Param("matricule") String matricule);
	
	   @Query(value="SELECT events.id_event, nom, matricule, dette,echeance_courant,remboursement_courant,montant_restant, interet_partiel,date_event FROM events INNER JOIN member ON member.id_member IN  (events.foreign_key_for_members)", nativeQuery=true)
	   Page <List<List<Object>>> historyRemboursementDette(org.springframework.data.domain.Pageable pageable);
	
	   @Query(value="SELECT id_event, member.nom, member.matricule, dette,remboursement_courant,montant_restant,echeance_courant,interet_partiel,date_event, prochain_montant FROM `events` INNER JOIN `member` ON member.id_member=events.foreign_key_for_members", nativeQuery=true)
	   Page <List<List<Object>>> RemboursementDetteTable(org.springframework.data.domain.Pageable pageable);
	
	   
	   @Query(value= "SELECT  CASE    WHEN  NOT EXISTS (SELECT * FROM events  WHERE  entered_matricule = ?1) THEN 'false'  ELSE 'true'  END", nativeQuery=true )
		public Boolean matricIsExist(String matric);
		
	   @Query(value="SELECT   echeance_courant FROM events WHERE  entered_matricule = ?1", nativeQuery=true)
		List<Double> getEcheanceCourantByMatricule(String matric);
         
	   @Query(value="SELECT   dette FROM events WHERE  entered_matricule = ?1", nativeQuery=true)	   
		List<Double> getDetteByMatricule(String matric);
	   
	   @Query(value= "SELECT date_event FROM events WHERE entered_matricule=:matricule", nativeQuery=true )
		public List<String> getDateEventByMatricule(@Param("matricule") String matricule);
	   
	   @Query(value="SELECT   montant_restant FROM events WHERE  entered_matricule = ?1", nativeQuery=true)
		List<Double> getMontantRestantByMatricule(String matric);	   
	   
	    @Modifying
		@Transactional
		@Query(value="UPDATE events e SET e.remboursement_courant =:montant, e.date_event=:date, e.entered_matricule=:matricule WHERE e.id_event=:idRemb",nativeQuery=true)
		void updateRembourse(@Param("idRemb") Long idRemb,   @Param("matricule") String matricule, @Param("montant")  double montant, @Param("date") Date date );
		
	   
	   @Query(value="SELECT entered_matricule, SUM(`interet_partiel`) AS Somme_des_interets FROM events  WHERE entered_matricule IN (SELECT entered_matricule FROM events WHERE montant_restant IN (0)) GROUP BY entered_matricule", nativeQuery=true)
		List<Double> generetedBenefitByEachMember();
	   
	   @Query(value="SELECT  SUM(`interet_partiel`) AS Total_des_interets FROM events  WHERE entered_matricule IN (SELECT entered_matricule FROM events WHERE montant_restant IN (0)) ", nativeQuery=true)
		Double getTotalGeneretedFakeBenefitByAllMember();
	   
	   
	   @Query(value="SELECT  SUM(`interet_partiel`) AS Total_des_interets FROM events  WHERE entered_matricule =?1", nativeQuery=true)
		Double totalBenefitByMatricule(String matricule);
	   
	   @Query(value="SELECT  SUM(`interet_partiel`) FROM events", nativeQuery=true)
		Double getTotalGeneretedBenefitByAllMember();
	   
	   @Query(value="SELECT  SUM(`remboursement_courant`)  FROM events", nativeQuery=true)
		Double getTotalRemboursement();
	   
	   public Page <Events> findByEnteredMatriculeContains(String mc, org.springframework.data.domain.Pageable pageable);

	   public List<Events> findByEnteredMatriculeContains(String string);
	   
}
