package org.sid.FamilyaProject.metier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.exolab.castor.types.DateTime;
import org.sid.FamilyaProject.dao.ArchiveRepository;
import org.sid.FamilyaProject.dao.PrevarchiveRepository;
import org.sid.FamilyaProject.entities.Archive;
import org.sid.FamilyaProject.entities.Prevarchive;

public class ArchiveDataBase {	
	
	
	public ArchiveDataBase() {
		super();
		
	}
	
	public String getNameOfDataBase(){
		
		Date now=new Date();
		String date=new SimpleDateFormat("yyyy_MM_dd",Locale.ENGLISH).format(now);				
		String dbName="archiveDb"+date.toString();
		
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
	
public void managePreviewsArchive(PrevarchiveRepository prevarchiveRepo,ArchiveRepository archiveRepo) {
	
	Connection connect=null;
	boolean prevTableExist=false;
	PreparedStatement ps=null;
	Statement stmt=null;
	String previewArchive="prevarchive";	
	
	
	try {
		
			Class.forName("com.mysql.cj.jdbc.Driver");		
			connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/familyadb","root","");			
			ResultSet rs=connect.getMetaData().getTables(null, null, previewArchive, null);
			
			while (rs.next()) {
				
				String tName =rs.getString("TABLE_NAME");
				if(tName!=null && tName.equals(previewArchive)) {					
					prevTableExist=true;
					break;					
				}				
			}
			

	}catch(Exception e){	
			System.out.println(e.getMessage());	
	}

	try {
			
			
		    if(connect!=null && !prevTableExist) {
			
					String sql ="CREATE TABLE familyadb."+previewArchive+" AS SELECT * FROM familyadb.archive";
					ps = connect.prepareStatement(sql);
					ps.executeUpdate();				    
				    System.out.println("The table "+previewArchive+"  has been  Moved  successfully...");				
				    ps.close();
				    
			}else if(connect!=null && prevTableExist) {
				
				   if(prevarchiveRepo.tableIsEmpty()==0) {
					   
					    stmt= connect.createStatement();	    
						String sql ="INSERT INTO prevarchive SELECT * FROM archive";						
					    stmt.executeUpdate(sql);
					    System.out.println("=====>Table copied ...");		 
					    stmt.close();
					   
				   }else if(prevarchiveRepo.tableIsEmpty()>0) {
					   
					   List<Archive> archList =archiveRepo.getArchiveList();
					   
					   for (Archive arch:archList) {
						   
						   prevarchiveRepo.save(new Prevarchive( arch.getNom(),arch.getEnteredMatric(),arch.getSommeEmprunt(),arch.getDuree_echeance(),arch.getTaux(),arch.getDettePlusInteret(),arch.getTypeInteret(),arch.getDate_emprunt(),arch.getBeneficeGenere(),arch.getSommePenalty()));
					   }
					    
				   }else {
					   
					   System.out.println("Table cout row is negative");
				   }
				
									
			}
		    
	 }catch(Exception e){		
		    System.out.println(e.getMessage());
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
