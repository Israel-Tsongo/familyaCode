package org.sid.FamilyaProject.dao;

import java.util.Date;
import java.util.List;

import org.sid.FamilyaProject.entities.Debiteur;
import org.sid.FamilyaProject.entities.Member;


import org.springframework.data.domain.Page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface MemberRepository extends JpaRepository<Member ,Long> {

	@Query(value= "SELECT * FROM member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id WHERE matricule = ?1", nativeQuery=true )
	public Member getMemberByMatricule(String matric);
	@Query(value="SELECT * FROM member  WHERE id_member = ?1", nativeQuery=true )
	public Member getMemberById(Long id);
	
	@Query(value= "SELECT capital_initial FROM member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  WHERE matricule = ?1", nativeQuery=true )
	public Double getCapitalByMatricule(String matric);
	
	@Query(value= "SELECT matricule FROM member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id WHERE id_member = ?1", nativeQuery=true )
	public String getMatriculeById(Long idMember);
	
	@Query(value= "SELECT * FROM member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id WHERE auth_user.matricule  LIKE %:mc%", nativeQuery=true )
	public Page <Member> findByUserMatriculeContains(String mc, org.springframework.data.domain.Pageable pageable);
	
	@Query(value= "SELECT id_member, code, matricule,mandataire, capital_initial, categorie_membre, fonction, type_contrat, date_adhesion FROM `member` INNER JOIN `auth_user` ON member.user_foreign_key_in_member = auth_user.auth_user_id", nativeQuery=true )
	public Page <List<List<Object>>> getAllFromMembersTable(org.springframework.data.domain.Pageable pageable);
	
	 
		
	@Query(value= "SELECT code, matricule, capital_initial FROM member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id WHERE auth_user.matricule=?1 ", nativeQuery=true )
	public List<List<Object>> getCapitalMatriculeNom(String matricule);
	
	
	@Query(value="SELECT    CASE   WHEN auth_user.matricule=payement.entered_matric THEN (SUM(contrib_mensuel)+ member.capital_initial)    WHEN  NOT EXISTS (SELECT * FROM payement WHERE auth_user.matricule=payement.entered_matric) THEN member.capital_initial ELSE (contrib_mensuel+ member.capital_initial)    END AS Total_cotisation  FROM `member` INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id  INNER JOIN payement ON member.id_member= payement.foreign_key_member_paying OR NOT EXISTS (SELECT * FROM payement WHERE member.id_member=payement.foreign_key_member_paying) GROUP BY auth_user.matricule", nativeQuery=true)
	  List<Double> totalEnCaisse();
	
	
	@Query(value="SELECT code,auth_user.matricule,capital_initial,date_adhesion,   CASE    WHEN  NOT EXISTS (SELECT * FROM payement WHERE member.id_member=payement.foreign_key_member_paying) THEN 0    ELSE SUM(contrib_mensuel)    END AS Somme_des_contributions ,  CASE  WHEN auth_user.matricule=payement.entered_matric THEN (SUM(contrib_mensuel)+ member.capital_initial)     WHEN  NOT EXISTS (SELECT * FROM payement WHERE auth_user.matricule=payement.entered_matric) THEN member.capital_initial   ELSE (contrib_mensuel+ member.capital_initial)  END AS Total_cotisation,    CASE 	 WHEN NOT EXISTS (SELECT * FROM debiteur WHERE auth_user.matricule=debiteur.entered_matric) THEN ' '  ELSE date_emprunt     END  AS Date_emprunt,        CASE    WHEN NOT EXISTS (SELECT * FROM debiteur WHERE auth_user.matricule=debiteur.entered_matric) THEN ' '       ELSE taux        END  AS Taux,      CASE          WHEN NOT EXISTS (SELECT * FROM debiteur WHERE auth_user.matricule=debiteur.entered_matric) THEN ' '     ELSE duree_echeance      END  AS Duree_echeance,   CASE          WHEN NOT EXISTS (SELECT * FROM debiteur WHERE auth_user.matricule=debiteur.entered_matric) THEN ' '    ELSE somme_emprunt       END  AS Somme_empruntee  FROM `member` INNER JOIN debiteur ON  debiteur.foreign_key_member=member.id_member  OR NOT EXISTS (SELECT * FROM debiteur WHERE auth_user.matricule=debiteur.entered_matric) INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id INNER JOIN payement ON member.id_member= payement.foreign_key_member_paying OR NOT EXISTS (SELECT * FROM payement WHERE member.id_member=payement.foreign_key_member_paying) GROUP BY auth_user.matricule",nativeQuery=true)
	List<List<Object>> getDebtAndSubscriptionsWithOwner();
	
	
	@Query(value= "SELECT matricule,`capital_initial` FROM `member` INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id", nativeQuery=true )
	List<List<Object>> getCapitalInitialParMembre();
	
	
	
	@Query(value= "SELECT SUM(`capital_initial`) AS Somme_total FROM `member`", nativeQuery=true )
	Double getTotalCapitauxInitiaux();
	
	@Modifying
	@Transactional
	@Query(value="UPDATE member m SET m.capital_initial =:capital, m.mandataire=:mandataire, m.date_adhesion=:date, m.type_contrat=:contrat WHERE m.id_member=:id_member",nativeQuery=true)
	void updateMember(@Param("id_member") Long id_member,  @Param("mandataire") String mandataire, @Param("capital")  double capital,  @Param("contrat") String contrat,  @Param("date") Date date );
     
	
	@Modifying
	@Transactional
	@Query(value="UPDATE member m SET m.capital_initial=:newSolde WHERE m.id_member=:idMember",nativeQuery=true)
	public void updateCapitalInitialById(@Param("idMember") Long idMember, @Param("newSolde") double solde);
	

	
	@Query(value="SELECT id_member FROM member WHERE member.user_foreign_key_in_member=:foreign_Id",nativeQuery=true)
	public Integer getMemberIdByProfilId(@Param("foreign_Id") Long foreign_Id);
	
	@Query(value= "SELECT * FROM member INNER JOIN auth_user ON member.user_foreign_key_in_member =auth_user.auth_user_id WHERE auth_user.matricule  LIKE %:mc%", nativeQuery=true )
	public List<Member> findByUserNomContains(String mc);
	
	
	
	
	
}
