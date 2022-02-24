package org.sid.FamilyaProject.metier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArchiveDataBase {
	
	
	
	
	
	public ArchiveDataBase() {
		super();
		
	}
	
	private String getNameOfDataBase(){
		Date date=new Date();
		System.out.println("#####"+date.toString().substring(24, 28));
		String dbName="archiveDb"+date.toString().substring(24, 28);
		return dbName;
	}

	public void createDb(List<String> errorList) {
		
		try {
			
			Connection conn = Singleton_connection.getConnect();
			Statement stmt = conn.createStatement();
		    
			String sql ="CREATE DATABASE "+getNameOfDataBase();
			
		    stmt.executeUpdate(sql);
		    System.out.println("Database created successfully...");
		    stmt.close();
		    
		    
		} catch (SQLException e) {
			
			errorList.add("Une error est survenu lors de la creation de la base de donnee");
			e.printStackTrace();
		}
		
 		
	}
	
	
public void moveTablesInDb(List<String> errorList) {
	
	Connection connect=null;
	List<String> allTables=new ArrayList<String>();
	PreparedStatement ps=null;
	
	allTables.add("member");
	allTables.add("debiteur");
	allTables.add("payement");
	allTables.add("events");
	allTables.add("operation");
	allTables.add("interet_par_membre");
	allTables.add("archive");
	allTables.add("depense");
	
		
		try {
			
			
			  try {
				
						Class.forName("com.mysql.cj.jdbc.Driver");		
						connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+getNameOfDataBase(),"root","");
				
				}catch(Exception e){
					
						System.out.println(e.getMessage());
					
				}
			
				
			for(String tableName: allTables) {
		    
					String sql ="CREATE TABLE "+getNameOfDataBase()+"."+tableName+" AS SELECT * FROM familyadb."+tableName;
					ps = connect.prepareStatement(sql);
					ps.executeUpdate();				    
				    System.out.println("The table "+tableName+"  has been  Moved  successfully...");
			  }
		    ps.close();
		    
		    
		} catch (SQLException e) {
			
			errorList.add("Une error est survenu lors du deplacemement des donnees");
			e.printStackTrace();
		}
		
 		
	}


public void clearDb(List<String> errorList) {
	
	
	Connection connect=null;
	Statement stmt=null;
	List<String> allTables=new ArrayList<String>();
	
	
	//allTables.add("debiteur");
	allTables.add("payement");
	//allTables.add("events");
	allTables.add("operation");	
	allTables.add("archive");
	allTables.add("depense");
	
	try {
		
		
		
		 try {
				
				Class.forName("com.mysql.cj.jdbc.Driver");		
				connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+getNameOfDataBase(),"root","");
		
		}catch(Exception e){
			
				System.out.println(e.getMessage());
			
		}
		
		//Connection conn = Singleton_connection.getConnect();
		 for(String tableName: allTables) {
			 
			 stmt= connect.createStatement();	    
			String sql ="TRUNCATE TABLE familyadb."+tableName;			
		    stmt.executeUpdate(sql);
		    System.out.println("Table"+tableName +" cleaned successfully...");
		 }
	    stmt.close();
	    
	    
	} catch (SQLException e) {
		
		errorList.add("Une error est survenu lors de la suppression des tables");
		e.printStackTrace();
	}
	
		
}

}
