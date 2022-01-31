package org.sid.FamilyaProject.users;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor   @ToString
@Table(name = "auth_role")
public class Role {
	
	
	
	
	@Id 	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auth_role_id")
	private Long roleId;
	private String roleName;
	private  String roleDescription;
	
	
	
	
	public Role( String role_name, String role_description) {
		
		
		this.roleName = role_name;
		this.roleDescription = role_description;
	}




	public Long getRole_id() {
		return roleId;
	}




	public void setRole_id(Long role_id) {
		this.roleId = role_id;
	}




	public String getRole_name() {
		return roleName;
	}




	public void setRole_name(String role_name) {
		this.roleName = role_name;
	}




	public String getRole_description() {
		return roleDescription;
	}




	public void setRole_description(String role_description) {
		this.roleDescription = role_description;
	}




	
	
	
	
	

}
