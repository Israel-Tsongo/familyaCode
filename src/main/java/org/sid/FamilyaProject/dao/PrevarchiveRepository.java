package org.sid.FamilyaProject.dao;


import java.util.List;
import org.sid.FamilyaProject.entities.Prevarchive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PrevarchiveRepository extends JpaRepository<Prevarchive, Long>  {
	
		@Query(value="SELECT `id_deb_archiv`,`nom`,`entered_matric`,`somme_emprunt`,`taux`,`dette_plus_interet`,`duree_echeance`, `type_interet` , `date_emprunt`,somme_penalty,benefice_genere FROM `prevarchive`", nativeQuery=true )
		public Page<List<List<Object>>> previewsArchivList(PageRequest of);
	
		public Page <Prevarchive> findByEnteredMatricContains(String mc, org.springframework.data.domain.Pageable pageable );
     
		@Query(value="SELECT COUNT(*) FROM prevarchive", nativeQuery=true)
	    public Integer tableIsEmpty();
	 


}
