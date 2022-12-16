package org.sid.FamilyaProject.dao;

import java.util.List;
import java.util.Optional;

import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.users.Role;
import org.sid.FamilyaProject.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query(value= "SELECT auth_user.auth_user_id, auth_role.auth_role_id, nom,code, matricule,fonction, email, mobile, password,role_name FROM `auth_user` INNER JOIN `auth_user_role` ON auth_user.auth_user_id=auth_user_role.auth_user_id INNER JOIN `auth_role` ON auth_role.auth_role_id=auth_user_role.auth_role_id", nativeQuery=true )
	public Page <List<List<Object>>> getAllUsers(org.springframework.data.domain.Pageable pageable);
	
	@Modifying
	@Transactional
	@Query(value="DELETE FROM auth_user_role WHERE auth_user_role.auth_user_id=:Id_user",nativeQuery=true)
	public void removeUserRoleById(@Param("Id_user") Long id_user);
	
	@Modifying
	@Transactional
	@Query(value="DELETE FROM auth_user WHERE auth_user.auth_user_id=:Id_user",nativeQuery=true)
	public void removeUserById(@Param("Id_user") Long id_user);
	
	
	

	@Query(value="SELECT * FROM auth_user INNER JOIN auth_user_role ON auth_user.auth_user_id=auth_user_role.auth_user_id INNER JOIN auth_role ON auth_role.auth_role_id=auth_user_role.auth_role_id WHERE  categorie_membre='Fondateur' AND fonction='President' AND auth_role.role_name='SUPER_USER'", nativeQuery=true )
	public User getUserFondateur();
	
		
	@Query(value="SELECT * FROM auth_user INNER JOIN auth_user_role ON auth_user.auth_user_id=auth_user_role.auth_user_id INNER JOIN auth_role ON auth_role.auth_role_id=auth_user_role.auth_role_id  WHERE categorie_membre='Membre_effectif' AND fonction='Financier' AND auth_role.role_name='ADMIN_USER'", nativeQuery=true )
	public List<User> getFinancier();
	
	@Query(value="SELECT *  FROM auth_user INNER JOIN auth_user_role ON auth_user.auth_user_id=auth_user_role.auth_user_id INNER JOIN auth_role ON auth_role.auth_role_id=auth_user_role.auth_role_id WHERE categorie_membre='Membre_effectif' AND fonction='Gerant' AND auth_role.role_name='ADMIN_USER'", nativeQuery=true )
	public List<User> getGerant();
	
	
	@Query("SELECT u from User u where u.email= :email")
	public User getUserByEmail(@Param("email") String email);
	
	@Query("SELECT u from User u where u.user_id = :id")
	public User getUserById(@Param("id") Long UserId);

	@Query("SELECT  u from User u where u.mobile = :phoneNumber")
	public User getUserByPhoneNumber(@Param("phoneNumber") String phoneNumber);
	
	@Query(value="SELECT * FROM auth_user" , nativeQuery=true)
	public List<User>findAllAuthUser();
	

	@Query(value="SELECT auth_user_id from auth_user ORDER BY auth_user_id ASC" , nativeQuery=true)
	public List<Long>getLatestId();

	@Query(value="SELECT matricule from auth_user ORDER BY auth_user_id Desc" , nativeQuery=true)
	public List<String>getLatestMatricule();

	
	public Optional<User> findUserByEmail(@Param("email") String email);

    @Query(value="SELECT * FROM auth_user WHERE matricule=:matricula" , nativeQuery=true)
	public User getUserByMatricule(@Param("matricula") String matricule);



	public Page<User> findByNomContains(String mc, org.springframework.data.domain.Pageable pageable);

    
	@Modifying
	@Transactional
	@Query(value="UPDATE auth_user  INNER JOIN auth_user_role ON auth_user.auth_user_id=auth_user_role.auth_user_id INNER JOIN auth_role ON auth_role.auth_role_id=auth_user_role.auth_role_id  SET auth_user.nom=:nom,auth_user.code=:code, auth_user.email=:email, auth_user.mobile=:mobile, auth_user.password=:password, auth_user_role.auth_role_id=:role WHERE auth_user.auth_user_id=:auth_user_id AND auth_role.auth_role_id=:auth_role_id",nativeQuery=true)
	public void updateUser(@Param("auth_user_id") Long idUser,@Param("auth_role_id") Long idRole,@Param("nom") String nom,@Param("code") String code,@Param("email") String email,@Param("mobile") String mobile,  @Param("password") String password,@Param("role") Long role);
	
	@Modifying
	@Transactional
	@Query(value="UPDATE auth_user  INNER JOIN auth_user_role ON auth_user.auth_user_id=auth_user_role.auth_user_id INNER JOIN auth_role ON auth_role.auth_role_id=auth_user_role.auth_role_id  SET  auth_user_role.auth_role_id=:role WHERE auth_user.auth_user_id=:auth_user_id",nativeQuery=true)
	public void updateRoleUser(@Param("auth_user_id") Long idUser,@Param("role") Long role);

	

	
	@Modifying
	@Transactional
	@Query(value="UPDATE auth_user  SET auth_user.genre=:genre,auth_user.type_piece=:typePiece,auth_user.numero_piece=:numeroPiece, auth_user.salaire=:salaire, auth_user.retenu=:retenu, auth_user.adresse=:adresse, auth_user.matricule=:matricule, auth_user.fonction=:fonction, auth_user.categorie_membre=:categorie, auth_user.numero_fiche=:numeroFiche WHERE auth_user.auth_user_id=:auth_user_id",nativeQuery=true)
	public void updateOthersDetailsUser(@Param("auth_user_id") Long idUser, @Param("genre") String genre,@Param("typePiece") String typePiece,@Param("numeroPiece") String numeroPiece,  @Param("salaire") String salaire,@Param("retenu")	String retenu, @Param("adresse") String adresse,@Param("matricule") String matricule, @Param("fonction") String fonction, @Param("categorie") String categorie,@Param("numeroFiche") String numeroFiche);

	
	@Query(value="SELECT auth_user.salaire FROM auth_user WHERE auth_user.matricule = ?1", nativeQuery=true )
	public double getSalaryByMatricule(String matric);
	
	@Query(value="SELECT auth_user.nom FROM auth_user WHERE auth_user.matricule = ?1", nativeQuery=true )
	public String getUserNameByMatricule(String matric);

	public List<User> findByNomContains(String string);
	
	@Query(value="SELECT auth_user.numero_fiche FROM auth_user WHERE auth_user.numero_fiche =?1", nativeQuery=true )
	public List<String> getUserByFicheNumber(String ficheNumber);
	
	@Query(value="SELECT * FROM auth_user WHERE auth_user.numero_piece = ?1", nativeQuery=true )
	public List<User> getUserByPieceNumber(String pieceNumber);

	

}
