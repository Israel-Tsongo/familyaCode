package org.sid.FamilyaProject.users;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.uniqueAnnotations.UniqueNumeroFicheInterface;
import org.sid.FamilyaProject.uniqueAnnotations.UniqueNumeroPieceInterface;

import javax.persistence.JoinColumn;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor   @ToString
@Table(name = "auth_user")
public class User implements Serializable {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auth_user_id")
	private Long user_id;
	
	@NotBlank(message="Le  nom est obligatoire" )
	private String nom;
	
	@Column(nullable=false , unique = true)
	@NotBlank(message="L 'adresse mail est obligatoire" )
	@Email(message="Entrer une adresse mail valide")
	private String email;
	
	@NotBlank(message="Le code est obligatoire" )
	private String code;
	
	@NotBlank(message="Le genre est obligatoire" )
	private String genre;
	
	//@UniqueNumeroFicheInterface
	@NotBlank(message="Le numero du fiche d'adhesion est obligatoire" )
	//@Column(nullable=false , unique = true)	
	private String numeroFiche;
	
	@NotBlank(message="Le type de la piece est obligatoire" )
	private String typePiece;
	
	@NotBlank(message="Le numero de la piece d'identite est obligatoire")
	//@Column(nullable=false,unique = true)	
	private String numeroPiece;
	
	@NotNull(message="Le montant de votre salaire est obligatoire")
	//@Column(nullable=false)
	private BigDecimal salaire;
	
	@NotNull(message="Le pourcentage de retenu est obligatoire")
	//@Column(nullable=false)
	private BigDecimal retenu;
	
	@Pattern(regexp="(^|[0-9]{10})",message="Votre numero doit contenir 10 chiffres (Ex 0978908909)")
	@NotBlank(message="Le  numero de telephone est obligatoire " )
	private String mobile;
	
	@NotBlank(message="L'adresse  est obligatoire" )
	private String adresse;
	
	@Column(nullable=false )
	@Length(min=8, message="Le mot de passe doit contenir au minimum 8 caracteres")
	@NotBlank(message="Vous devez entrer un mot de passe")
	private String password;	
	
	private boolean enabled;	
	
	@Column(nullable=false , unique = true)		
	private String matricule;
	
	private String fonction; 
	
	private String categorieMembre;	
	
	
	@ManyToMany(cascade = CascadeType.MERGE ,fetch = FetchType.EAGER)	
	@OnDelete(action = OnDeleteAction.CASCADE)	
	@JoinTable(name = "auth_user_role", joinColumns = @JoinColumn(name = "auth_user_id"), inverseJoinColumns = @JoinColumn(name = "auth_role_id"))
	private Set<Role> roles;
	
	@OneToOne(mappedBy = "memberUser", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Member member;
	
	
	
	public User(String nom,String code, BigDecimal retenu,BigDecimal salaire, String numeroFiche, String adresse,String numeroPiece, String typePiece, String postnom, String prenom, String email, String mobile, String password) {
		
		this.retenu=retenu;
		this.salaire=salaire;
		this.numeroFiche=numeroFiche;
		this.adresse=adresse;
		this.numeroPiece=numeroPiece;
		this.typePiece=typePiece;
		this.nom = nom;	
		this.code=code;
		this.email = email;
		this.mobile=mobile;
		this.password = password;
		
	}



	public Long getUser_id() {
		return user_id;
	}



	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}



	public String getNom() {
		return nom;
	}



	public void setNom(String nom) {
		this.nom = nom;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getMobile() {
		return mobile;
	}



	public void setMobile(String mobile) {
		this.mobile = mobile;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	



	public Set<Role> getRoles() {
		return roles;
	}



	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}



	public boolean isEnabled() {
		return enabled;
	}



	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	public Member getMember() {
		return member;
	}



	public void setMember(Member member) {
		this.member = member;
	}



	public String getMatricule() {
		return matricule;
	}



	public void setMatricule(String matricule) {
		this.matricule = matricule;
	}



	public String getGenre() {
		return genre;
	}



	public void setGenre(String genre) {
		this.genre = genre;
	}



	public String getNumeroFiche() {
		return numeroFiche;
	}



	public void setNumeroFiche(String numeroFiche) {
		this.numeroFiche = numeroFiche;
	}



	public String getTypePiece() {
		return typePiece;
	}



	public void setTypePiece(String typePiece) {
		this.typePiece = typePiece;
	}



	public String getNumeroPiece() {
		return numeroPiece;
	}



	public void setNumeroPiece(String numeroPiece) {
		this.numeroPiece = numeroPiece;
	}



	public    BigDecimal getSalaire() {
		return salaire;
	}



	public void setSalaire(BigDecimal salaire) {
		this.salaire = salaire;
	}



	public   BigDecimal getRetenu() {
		return retenu;
	}



	public void setRetenu(BigDecimal retenu) {
		this.retenu = retenu;
	}



	public String getAdresse() {
		return adresse;
	}



	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	public String getFonction() {
		return fonction;
	}



	public void setFonction(String fonction) {
		this.fonction = fonction;
	}



	public String getCategorieMembre() {
		return categorieMembre;
	}



	public void setCategorieMembre(String categorieMembre) {
		this.categorieMembre = categorieMembre;
	}
	
	
	
	
	
	

}
