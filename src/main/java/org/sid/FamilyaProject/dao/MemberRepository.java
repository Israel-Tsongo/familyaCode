package org.sid.FamilyaProject.dao;

import java.util.Date;
import java.util.List;

import org.sid.FamilyaProject.entities.Member;


import org.springframework.data.domain.Page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface MemberRepository extends JpaRepository<Member ,Long> {

	@Query(value= "SELECT * FROM member WHERE matricule = ?1", nativeQuery=true )
	public Member getUserByMatricule(String matric);
	
	@Query(value= "SELECT capital_initial FROM member WHERE matricule = ?1", nativeQuery=true )
	public double getCapitalByMatricule(String matric);
	
	@Query(value= "SELECT matricule FROM member WHERE id_member = ?1", nativeQuery=true )
	public String getMatriculeById(Long idMember);
	
	 public Page <Member> findByNomContains(String mc, org.springframework.data.domain.Pageable pageable);
	
	@Query(value= "SELECT id_member, nom, matricule,mandataire, capital_initial, categorie_membre, fonction, type_contrat, date_adhesion FROM member ", nativeQuery=true )
	public Page <List<List<Object>>> getAllFromMembersTable(org.springframework.data.domain.Pageable pageable);
	
	@Query(value= "SELECT nom, matricule, capital_initial FROM member WHERE member.matricule=?1 ", nativeQuery=true )
	public List<List<Object>> getCapitalMatriculeNom(String matricule);
	
	
	@Query(value="SELECT    CASE   WHEN member.matricule=payement.entered_matric THEN (SUM(contrib_mensuel)+ member.capital_initial)    WHEN  NOT EXISTS (SELECT * FROM payement WHERE member.matricule=payement.entered_matric) THEN member.capital_initial ELSE (contrib_mensuel+ member.capital_initial)    END AS Total_cotisation  FROM `member`  INNER JOIN payement ON member.id_member= payement.foreign_key_member_paying OR NOT EXISTS (SELECT * FROM payement WHERE member.id_member=payement.foreign_key_member_paying) GROUP BY member.matricule", nativeQuery=true)
	  List<Double> totalEnCaisse();
	
	
	@Query(value=" SELECT nom,matricule,capital_initial,date_adhesion,   CASE    WHEN  NOT EXISTS (SELECT * FROM payement WHERE member.id_member=payement.foreign_key_member_paying) THEN 0    ELSE SUM(contrib_mensuel)    END AS Somme_des_contributions ,  CASE  WHEN member.matricule=payement.entered_matric THEN (SUM(contrib_mensuel)+ member.capital_initial)     WHEN  NOT EXISTS (SELECT * FROM payement WHERE member.matricule=payement.entered_matric) THEN member.capital_initial   ELSE (contrib_mensuel+ member.capital_initial)  END AS Total_cotisation,    CASE 	 WHEN NOT EXISTS (SELECT * FROM debiteur WHERE member.matricule=debiteur.entered_matric) THEN ' '  ELSE date_emprunt     END  AS Date_emprunt,        CASE    WHEN NOT EXISTS (SELECT * FROM debiteur WHERE member.matricule=debiteur.entered_matric) THEN ' '       ELSE taux        END  AS Taux,      CASE          WHEN NOT EXISTS (SELECT * FROM debiteur WHERE member.matricule=debiteur.entered_matric) THEN ' '     ELSE duree_echeance      END  AS Duree_echeance,   CASE          WHEN NOT EXISTS (SELECT * FROM debiteur WHERE member.matricule=debiteur.entered_matric) THEN ' '    ELSE somme_emprunt       END  AS Somme_empruntee  FROM `member` INNER JOIN debiteur ON  debiteur.foreign_key_member=member.id_member  OR NOT EXISTS (SELECT * FROM debiteur WHERE member.matricule=debiteur.entered_matric) INNER JOIN payement ON member.id_member= payement.foreign_key_member_paying OR NOT EXISTS (SELECT * FROM payement WHERE member.id_member=payement.foreign_key_member_paying) GROUP BY member.matricule",nativeQuery=true)
	List<List<Object>> getDebtAndSubscriptionsWithOwner();
	
	
	@Query(value= "SELECT matricule,`capital_initial` FROM `member`", nativeQuery=true )
	List<List<Object>> getCapitalInitialParMembre();
	
	
	@Query(value= "SELECT SUM(`capital_initial`) AS Somme_total FROM `member`", nativeQuery=true )
	Double getTotalCapitauxInitiaux();
	
	@Modifying
	@Transactional
	@Query(value="UPDATE member m SET m.capital_initial =:capital, m.categorie_membre=:categorie, m.mandataire=:mandataire, m.date_adhesion=:date, m.fonction=:fonction, m.matricule=:matricule, m.type_contrat=:contrat WHERE m.id_member=:id_member",nativeQuery=true)
	void updateMember(@Param("id_member") Long id_member,  @Param("matricule") String matricule, @Param("mandataire") String mandataire, @Param("capital")  double capital, @Param("fonction") String fonction, @Param("categorie")  String categorie,  @Param("contrat") String contrat,  @Param("date") Date date );
     
	
	@Modifying
	@Transactional
	@Query(value="UPDATE member m SET m.capital_initial=:newSolde WHERE m.matricule=:matricule",nativeQuery=true)
	public void updateCapitalInitialByMatricule(@Param("matricule") String matricule, @Param("newSolde") double solde);
	
	@Modifying
	@Transactional
	@Query(value="UPDATE member m SET m.nom=:newName WHERE m.matricule=:matricule",nativeQuery=true)
	public void updateName(@Param("matricule") String matricule, @Param("newName") String name);
	
	
	@Query(value="SELECT id_member FROM member WHERE member.user_foreign_key_in_member=:foreign_Id",nativeQuery=true)
	public Integer getMemberIdByProfilId(@Param("foreign_Id") Long foreign_Id);

	public List<Member> findByNomContains(String string);
	
	@Query(value="SELECT * FROM member WHERE  id_member=1 AND matricule=322 AND categorie_membre=Fondateur AND fonction=President AND type_contrat=CDI", nativeQuery=true )
	public Member getUserFondateur();
	
	
}
