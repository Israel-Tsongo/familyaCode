package org.sid.FamilyaProject.metier;

import java.sql.Connection;
import java.sql.DriverManager;

public class Singleton_connection {

	
private static Connection connect;
	
	
	static {
		
		try {
			
		Class.forName("com.mysql.cj.jdbc.Driver");		
		connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","");
		
		}catch(Exception e){
			
			System.out.println(e.getMessage());
			
		}
	}

	public static Connection getConnect() {
		return connect;
	}
	
	
	
	
	
}
