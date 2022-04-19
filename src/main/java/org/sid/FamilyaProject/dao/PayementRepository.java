package org.sid.FamilyaProject.dao;

import java.util.Date;
import java.util.List;

import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Payement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PayementRepository extends JpaRepository<Payement ,Long> {

	
	
	@Query(value="SELECT * FROM payement WHERE entered_matric = ?1", nativeQuery=true )
	public Page<Payement> getPayementByMatric(String matric,org.springframework.data.domain.Pageable pageable);
	
	@Query(value="SELECT * FROM payement WHERE entered_matric = ?1", nativeQuery=true )
	public List<Payement> getPayementByMatricule(String matric);
	
	
	@Query(value="SELECT SUM(contrib_mensuel) FROM payement WHERE payement.entered_matric=?1", nativeQuery=true )
	public Double getSommeContribByMaticule(String matricule);
	
	
	@Query(value= "SELECT `id_member`,`nom`,`matricule`,`capital_initial`, CASE    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.id_member=payement.foreign_key_member_paying) THEN 0   ELSE SUM(contrib_mensuel)        END AS Somme_payement_par_membre ,   CASE    WHEN member.matricule=payement.entered_matric THEN (SUM(contrib_mensuel)+ member.capital_initial)    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric) THEN member.capital_initial     ELSE (contrib_mensuel+ member.capital_initial)     END AS Total_payement_par_membre FROM `member` INNER JOIN `payement` ON member.id_member= payement.foreign_key_member_paying OR NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric)  GROUP BY member.matricule", nativeQuery=true )
	public Page<List<List<Object>>> getSubscriptionsAndCapitalWithOwnerMember( org.springframework.data.domain.Pageable pageable);
	  
	
	
	@Query(value= "SELECT `nom`,`matricule`,`capital_initial`, CASE    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.id_member=payement.foreign_key_member_paying) THEN 0   ELSE SUM(contrib_mensuel)        END AS Somme_payement_par_membre ,   CASE    WHEN member.matricule=payement.entered_matric THEN (SUM(contrib_mensuel)+ member.capital_initial)    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric) THEN member.capital_initial     ELSE (contrib_mensuel+ member.capital_initial)     END AS Total_payement_par_membre FROM `member` INNER JOIN `payement` ON member.id_member= payement.foreign_key_member_paying OR NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric)  WHERE member.matricule=?1", nativeQuery=true )
	public List<List<Object>> getDetails(String matricule );
	  
	
	
	
	@Query(value= "SELECT `id_member`,`nom`,`matricule`,`capital_initial`, CASE    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.id_member=payement.foreign_key_member_paying) THEN 0   ELSE SUM(contrib_mensuel)        END AS Somme_payement_par_membre ,   CASE    WHEN member.matricule=payement.entered_matric THEN (SUM(contrib_mensuel)+ member.capital_initial)    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric) THEN member.capital_initial     ELSE (contrib_mensuel+ member.capital_initial)     END AS Total_payement_par_membre FROM `member` INNER JOIN `payement` ON member.id_member= payement.foreign_key_member_paying OR NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric) WHERE matricule LIKE %:mc%  GROUP BY member.matricule", nativeQuery=true )
	public Page<List<List<Object>>> getByMaticuleSubscriptionsAndCapitalWithOwnerMember(@Param("mc") String mc, org.springframework.data.domain.Pageable pageable);
	  
	@Query(value= "SELECT `id_member`,`nom`,`matricule`,`capital_initial`, CASE    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.id_member=payement.foreign_key_member_paying) THEN 0   ELSE SUM(contrib_mensuel)        END AS Somme_payement_par_membre ,   CASE    WHEN member.matricule=payement.entered_matric THEN (SUM(contrib_mensuel)+ member.capital_initial)    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric) THEN member.capital_initial     ELSE (contrib_mensuel+ member.capital_initial)     END AS Total_payement_par_membre FROM `member` INNER JOIN `payement` ON member.id_member= payement.foreign_key_member_paying OR NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric) WHERE matricule LIKE %:mc%  GROUP BY member.matricule", nativeQuery=true )
	public List<Object> getByMaticuleSubscriptionsAndCapitalWithOwnerMembers( @Param("mc") String mc);
	  
	
	
	public Page <Payement> findByenteredMatricContains(String mc, org.springframework.data.domain.Pageable pageable  );
	
	
	@Query(value= "SELECT id_paye,nom,matricule,`contrib_mensuel`,`date_payement` FROM `payement` INNER JOIN member WHERE member.id_member=payement.foreign_key_member_paying GROUP BY id_paye", nativeQuery=true )
	public Page<List<List<Object>>> getSubscriptionsWithMembers(org.springframework.data.domain.Pageable pageable);
	
	@Query(value= "SELECT SUM(`contrib_mensuel`) FROM payement", nativeQuery=true )
	public Double getSommeSubscriptions();
	
	@Query(value= "SELECT COALESCE(SUM(member.capital_initial),0) + COALESCE(SUM(payement.contrib_mensuel),0)  AS Somme FROM member INNER JOIN payement", nativeQuery=true )
	public Double getSommeSubscriptionsAndCapitaux();
	
	
	@Query(value="SELECT `matricule`, CASE    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.id_member=payement.foreign_key_member_paying) THEN 0   ELSE SUM(contrib_mensuel)        END AS Somme_payement_par_membre ,   CASE    WHEN member.matricule=payement.entered_matric THEN (SUM(contrib_mensuel)+ member.capital_initial)    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric) THEN member.capital_initial     ELSE (contrib_mensuel+ member.capital_initial)     END AS Total_payement_par_membre FROM `member` INNER JOIN `payement` ON member.id_member= payement.foreign_key_member_paying OR NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric)  GROUP BY member.matricule",nativeQuery=true)	
	public List<List<Object>> getSoldes();	
	
	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE payement p SET p.contrib_mensuel =:contribution, p.date_payement=:date, p.entered_matric=:matricule WHERE p.id_paye=:idContrib",nativeQuery=true)
	void updateContribution(@Param("idContrib") Long idContrib,   @Param("matricule") String matricule, @Param("contribution")  double contribution, @Param("date") Date date );
    
	
	
	@Query(value= "SELECT * FROM payement WHERE entered_matric=:matricule AND date_payement LIKE %:mc%", nativeQuery=true )
	public List<Payement> findByenteredMatricContains(@Param("matricule") String mc, @Param("mc") String dateKeyWord);
	
	
	@Query(value= "SELECT `id_member`,`nom`,`matricule`,`capital_initial`, CASE    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.id_member=payement.foreign_key_member_paying) THEN 0   ELSE SUM(contrib_mensuel)        END AS Somme_payement_par_membre ,   CASE    WHEN member.matricule=payement.entered_matric THEN (SUM(contrib_mensuel)+ member.capital_initial)    WHEN  NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric) THEN member.capital_initial     ELSE (contrib_mensuel+ member.capital_initial)     END AS Total_payement_par_membre FROM `member` INNER JOIN `payement` ON member.id_member= payement.foreign_key_member_paying OR NOT EXISTS (SELECT * FROM `payement` WHERE member.matricule=payement.entered_matric) WHERE matricule LIKE %:mc%  GROUP BY member.matricule", nativeQuery=true )
	public List<List<Object>> getByMaticuleSubscriptionsAndCapitalWithOwnerMember(String mc);



	public Page<Payement> findBydatePayementContains(String matricule, org.springframework.data.domain.Pageable pageable );
	
	
	@Query(value= "SELECT * FROM payement WHERE entered_matric=:matricule AND  date_payement LIKE %:mc%", nativeQuery=true )
	public Page<Payement> findByDatePayementContains(@Param("matricule") String matricule , String mc, org.springframework.data.domain.Pageable pageable );
	
	@Query(value= "SELECT * FROM payement WHERE  date_payement LIKE %:mc%", nativeQuery=true )
	public Page<Payement> findByDatePayementOnlyContains(@Param("mc") String mc, org.springframework.data.domain.Pageable pageable );

	public List<Payement> findByenteredMatricContains(String mc);
	
	@Query(value= "SELECT * FROM payement WHERE  date_payement LIKE %:mc%", nativeQuery=true )
	public List<Payement> findByDatePayementOnlyContains(@Param("mc") String dateKeyWord);
	
	@Query(value= "SELECT * FROM payement WHERE entered_matric=:matricule AND  date_payement LIKE %:mc%", nativeQuery=true )
	public List<Payement> findByDatePayementContains(@Param("matricule") String mc, @Param("mc") String dateKeyWord);
	
	
}
