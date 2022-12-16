package org.sid.FamilyaProject.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import org.sid.FamilyaProject.entities.Depense;

public interface DepenseRepository extends JpaRepository <Depense,Long> {
	
	
	@Query(value="SELECT  SUM(`montant_depense`) AS Somme_depense FROM `depense`",nativeQuery=true)
    Double getTotalOutgo();
	
	public Page <Depense> findByMotifContains(String mc, org.springframework.data.domain.Pageable pageable  );
	
	@Query(value="SELECT id, montant_depense, motif, date_du_depense FROM `depense`",nativeQuery=true)
    Page<List<List<Object>>> getAllDep(org.springframework.data.domain.Pageable pageable);
	
	@Query(value="SELECT id, montant_depense, motif, date_du_depense FROM `depense`",nativeQuery=true)
	List<Depense> getAllDep();
	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE depense dep SET dep.montant_depense =:montant, dep.motif=:motif, dep.date_du_depense=:date WHERE dep.id=:id_dep",nativeQuery=true)
	void updateDepense(@Param("id_dep") Long id_dep,@Param("montant")  double montant, @Param("motif")  String motif, @Param("date") Date date );

	List<Depense> findByMotifContains(String string);

	
	
}
