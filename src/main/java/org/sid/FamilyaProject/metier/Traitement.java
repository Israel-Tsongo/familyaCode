package org.sid.FamilyaProject.metier;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sid.FamilyaProject.dao.ArchiveRepository;
import org.sid.FamilyaProject.dao.DebiteurRepository;
import org.sid.FamilyaProject.dao.DepenseRepository;
import org.sid.FamilyaProject.dao.EventsRepository;
import org.sid.FamilyaProject.dao.InteretParMembreRepository;
import org.sid.FamilyaProject.dao.MemberRepository;
import org.sid.FamilyaProject.dao.PayementRepository;
import org.sid.FamilyaProject.dao.UserRepository;
import org.sid.FamilyaProject.entities.Archive;
import org.sid.FamilyaProject.entities.Debiteur;
import org.sid.FamilyaProject.entities.Depense;
import org.sid.FamilyaProject.entities.Events;
import org.sid.FamilyaProject.entities.InteretParMembre;
import org.sid.FamilyaProject.entities.Member;
import org.sid.FamilyaProject.entities.Operation;
import org.sid.FamilyaProject.entities.Payement;
import org.sid.FamilyaProject.users.Role;
import org.sid.FamilyaProject.users.User;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class Traitement {
	
	
	 public double rounder(double d) {
		 
		 
		 return Math.round(d*1000.0)/1000.0;
	 }
	
	public List<List<Object>> converter(Page <List<List<Object>>> List ){
		
		Object[] obj=null;		
		List<List<Object>> viewList = new ArrayList<List<Object>>() ;			
		
		 List<List<List<Object>>> listMemb = (List<List<List<Object>>>)List.getContent();
		   
		    Iterator itr = listMemb.iterator();
		   while(itr.hasNext()) {
			   List<Object>  newList = new ArrayList<Object>();
			   obj =(Object[])itr.next();		   
			   
			        for( Object ob  :obj) {
			        	        
				               if(ob instanceof BigInteger) {			            	   
				            	   newList.add(Long.parseLong(String.valueOf(ob)));			            	  
				               }
				               else if(ob instanceof Timestamp) {
				            	   
				            	   newList.add(String.valueOf(ob).substring(0, 10));	
				            	   
				               } else if(ob instanceof Double) {
				            	   
				            	   newList.add(rounder((double)ob));	
				            	   
				               }
				               
				               else {			            	   
				            	   newList.add(String.valueOf(ob));	
				            	    
				                   }				              
			        }
			        
			   viewList.add(newList);
			    
			   
		   }
		 
		
		
		
		return viewList;
	}
	
	
public List<List<Object>> searchConverter(Page <Member> searchMemberList ){
		
	List<List<Object>> viewList = new ArrayList<List<Object>>() ;	
	List<Object>  newList=null;		           
		
		
						for( Member ob  :searchMemberList) {
							
							 newList= new ArrayList<Object>();
							 
							 newList.add(ob.getId_member());
							 newList.add(ob.getNom());
							 newList.add(ob.getMatricule());
							 newList.add(ob.getMandataire());
							 newList.add(rounder((double)ob.getCapital_Initial()));
							 newList.add(ob.getCategorieMembre());
							 newList.add(ob.getFonction());
							 newList.add(ob.getTypeContrat());
							 newList.add((ob.getDate_adhesion().toString()).substring(0, 10));
							 
							 viewList.add(newList);				              
					}
			   
		           
		 
	
		
		
		return viewList;
	}
	





public List<List<Object>> searchConverterPaye(Page <Payement> searchPayeList ){
	
	List<List<Object>> viewList = new ArrayList<List<Object>>() ;	
	List<Object>  newList=null;		           
		
		
						for( Payement ob  :searchPayeList) {
							
							 newList= new ArrayList<Object>();
							 
							 newList.add(ob.getId_paye());
							 newList.add(ob.getMemberPaying().getNom());
							 newList.add(ob.getEnteredMatric());
							 newList.add(rounder((double)ob.getContribMensuel()));													 
							 newList.add((ob.getDate_payement().toString()).substring(0, 10));
							 
							 viewList.add(newList);				              
					}
			   
		           
		 
	
		
		
		return viewList;
	}

 public List<List<Object>> searchDebConverter(Page <Debiteur> searchDebList ){
	
	List<List<Object>> viewList = new ArrayList<List<Object>>() ;	
	List<Object>  newList=null;		           
		
		
						for( Debiteur ob  :searchDebList) {
							
							 newList= new ArrayList<Object>();
							 
							 newList.add(ob.getId_debiteur());
							 newList.add(ob.getMember().getNom());
							 newList.add(ob.getEnteredMatric());
							 newList.add(rounder((double)ob.getSommeEmprunt()));
							 newList.add(ob.getTaux());
							 newList.add(ob.getDettePlusInteret());							 
							 newList.add(ob.getDuree_echeance());
							 newList.add(ob.getDetteCourante());							 
							 newList.add(ob.getTypeInteret());
							 newList.add(ob.getCurrentPenalite());
							 newList.add((ob.getDate_emprunt().toString()).substring(0, 10));							 
							 newList.add(ob.getPremierRemboursement());
							 viewList.add(newList);				              
					}
			   
		           
		 
	
		
		
		return viewList;
	}
	

 public List<List<Object>> searchArchivConverter(Page <Archive> searchArchiveList ){
		
		List<List<Object>> viewList = new ArrayList<List<Object>>() ;	
		List<Object>  newList=null;		           
			
			
							for( Archive ob  :searchArchiveList) {
								
								 newList= new ArrayList<Object>();
								 
								 newList.add(ob.getId_debArchiv());
								 newList.add(ob.getNom());
								 newList.add(ob.getEnteredMatric());
								 newList.add(rounder((double)ob.getSommeEmprunt()));
								 newList.add(ob.getTaux());
								 newList.add(ob.getDettePlusInteret());							 
								 newList.add(ob.getDuree_echeance());								 							 
								 newList.add(ob.getTypeInteret());							 
								 newList.add((ob.getDate_emprunt().toString()).substring(0, 10));
								 newList.add(ob.getSommePenalty());
								 newList.add(ob.getBeneficeGenere());
								 
								 viewList.add(newList);				              
						}
				   
			           
			 
		
			
			
			return viewList;
		}
 
 
 
 
 
 
 
 



public List<List<Object>> searchDepConverter(Page <Depense> searchDepList ){
	
	List<List<Object>> viewList = new ArrayList<List<Object>>() ;	
	List<Object>  newList=null;		           
		
		
						for( Depense ob  :searchDepList) {
							
							 newList= new ArrayList<Object>();
							 
							 newList.add(ob.getId());
							 newList.add(rounder((double)ob.getMontantDepense()));
							 newList.add(ob.getMotif());							
							 newList.add((ob.getDateDuDepense().toString()).substring(0, 10));
							 
							 viewList.add(newList);				              
					}
			   
		
		
		return viewList;
	}



public List<List<Object>> searchRembourseConverter(Page <Events> searchList ){
	
	List<List<Object>> viewList = new ArrayList<List<Object>>() ;	
	List<Object>  newList=null;		           
		
		
						for( Events ob  :searchList) {
							
							 newList= new ArrayList<Object>();
							 
							 newList.add(ob.getId_event());
							 newList.add(ob.getMembre().getNom());
							 newList.add(ob.getEntered_matricule());	
							 newList.add(rounder((double)ob.getDette()));
							 newList.add(rounder((double)ob.getRemboursement_courant()));
							 newList.add(rounder((double)ob.getMontant_restant()));	
							 newList.add(ob.getEcheance_courant());
							 newList.add(rounder((double)ob.getInteret_partiel()));							 
							 newList.add((ob.getDate_event().toString()).substring(0, 10));
							 newList.add(ob.getProchainMontant());
							 
							 viewList.add(newList);				              
					}
			   
		
		
		return viewList;
	}


public List<List<Object>> searchInteretConverter(Page<InteretParMembre> searchInteretList) {
	List<List<Object>> viewList = new ArrayList<List<Object>>() ;	
	List<Object>  newList=null;		           
		
		
						for( InteretParMembre ob  :searchInteretList) {
							
							 newList= new ArrayList<Object>();
							 
							 newList.add(ob.getId_interet());
							 newList.add(ob.getMembreDansInteret().getNom());
							 newList.add(ob.getMatriculeEntered());	
							 newList.add(rounder((double)ob.getInteretDuMembre()));			 							 
						 
							 viewList.add(newList);		
							 
					}
			   
		
		
		return viewList;
}


public List<List<Object>> searchOperaConverter(Page<Operation> searchOperaList) {
	List<List<Object>> viewList = new ArrayList<List<Object>>() ;	
	List<Object>  newList=null;		           
		
		
						for( Operation ob  :searchOperaList) {
							
							 newList= new ArrayList<Object>();							 
							 newList.add(ob.getIdOperation());			 
							 newList.add(ob.getOperation());	
							 newList.add(ob.getDate());
						 
							 viewList.add(newList);		
							 
					}
			   
		
		
		return viewList;
}

public void archiveDataBase(PayementRepository payeRepo,MemberRepository memberRepo,List<String> errorList) {
	
	 String matricule="" ;
	 double solde;
	 ArchiveDataBase arch= new ArchiveDataBase(); 
	 List<List<Object>> soldeList = payeRepo.getSoldes();
	 
	 arch.createDb(errorList);
	 arch.moveTablesInDb(errorList);
	 
	 for(List<Object> obj:soldeList) {
		 
		matricule= (String) obj.get(0);
		solde=(double) obj.get(2);
		 
		memberRepo.updateCapitalInitialByMatricule(matricule,solde);
		 
	 }
	 
	 
	 arch.clearDb(errorList);
	
	
	
	
	
}

public Object searchUserConverter(Page<User> searchUserList) {
	
	List<List<Object>> viewList = new ArrayList<List<Object>>() ;	
	List<Object>  newList=null;		           
		
		
						for( User ob :searchUserList) {
							
							 newList= new ArrayList<Object>();
							 
							 newList.add(ob.getUser_id());
							 
							 for(Role r: ob.getRoles()) {
								 newList.add(r.getRole_id());
							 }
							 
							 newList.add(ob.getNom());
							 newList.add(ob.getMatricule());
							 newList.add(ob.getEmail());
							 newList.add(ob.getMobile());
							 newList.add(ob.getPassword());
							 for(Role r: ob.getRoles()) {
								 newList.add(r.getRole_name());
							 }
							 
							 
							 
							 viewList.add(newList);				              
					}
			   
		           
		 
	
		
		
		return viewList;
}

public List<Double> debiteurCalculMontant(double montant, double echeance,String typeInteret) {
	
	List<Double> debiteurEntry = new ArrayList<Double>();
	
	double interet=((montant*1.5)/100);				      
    double dettePlusInteret= (montant+interet);
    
    double detteCourantEtDettePlusInteret=0.00;    
    double premierMontantARembourser=0.00;
    
    
    if(typeInteret.equals("Degressif")) { 	  
  	 
  	  
  	  detteCourantEtDettePlusInteret=dettePlusInteret;
  	  premierMontantARembourser=(dettePlusInteret/echeance);
  	  
	  	debiteurEntry.add(detteCourantEtDettePlusInteret);
	    debiteurEntry.add(premierMontantARembourser);
  	  
    }else if(typeInteret.equals("Constant"))  {
  	  
  	       System.out.println(" I m in Constant");  	       
  	       detteCourantEtDettePlusInteret=montant+(((montant*1.5)/100)*echeance);
  	       premierMontantARembourser=(detteCourantEtDettePlusInteret/echeance);	
  	     debiteurEntry.add(detteCourantEtDettePlusInteret);
  	    debiteurEntry.add(premierMontantARembourser);
    }
    
    
	
	return debiteurEntry;
}

public List<List<Object>> converter(List<List<Object>> listMemb ){
		
		Object[] obj=null;		
		List<List<Object>> viewList = new ArrayList<List<Object>>() ;	
		
		 
		   
		    Iterator itr = listMemb.iterator();
		   while(itr.hasNext()) {
			   List<Object>  newList = new ArrayList<Object>();
			   obj =(Object[])itr.next();		   
			   
			        for( Object ob  :obj) {
			        	        
				               if(ob instanceof BigInteger) {			            	   
				            	   newList.add(Long.parseLong(String.valueOf(ob)));			            	  
				               }
				               else if(ob instanceof Timestamp) {
				            	   
				            	   newList.add(String.valueOf(ob).substring(0, 10));	
				            	   
				               } else if(ob instanceof Double) {
				            	   
				            	   newList.add(rounder((double)ob));	
				            	   
				               }
				               
				               else {			            	   
				            	   newList.add(String.valueOf(ob));	
				            	    
				                   }				              
			        }
			        
			   viewList.add(newList);
		   
		   }
	
		return viewList;
	}

 public  ResponseEntity<byte[]> generatePDF(List<?> searchContribList,String jasperFileName,HashMap<String,Object> map,String fileName) throws Exception, JRException  {
	
	 
		   
		   JRBeanCollectionDataSource beanCollection=new JRBeanCollectionDataSource(searchContribList);
		 
		   
		   ClassPathResource classPathResource =new ClassPathResource(jasperFileName);
		   InputStream inpout =classPathResource.getInputStream();
		 	 
		   JasperReport compileReport =JasperCompileManager.compileReport(inpout);
		   
		   JasperPrint report =JasperFillManager.fillReport(compileReport,map, beanCollection);
			//JasperExportManager.exportReportToPdfFile(report,"contributionList.pdf");
		   
		   byte[] data =JasperExportManager.exportReportToPdf(report);
		   
		   HttpHeaders headers = new HttpHeaders();
		   headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename="+fileName+".pdf");
		   
	       return  ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
	   
	   
}

 
 public  ResponseEntity<byte[]> generateProof(String jasperFileName, Map<String, Object> map, String fileName) throws Exception, JRException  {
		
	 
	   
	   List<String> lst = new  ArrayList<String>();
	   
	   lst.add("salut");
	   
	   JRBeanCollectionDataSource beanCollection=new JRBeanCollectionDataSource(lst);
	   
	   ClassPathResource classPathResource =new ClassPathResource(jasperFileName);
	   InputStream inpout =classPathResource.getInputStream();
	   
	   JasperReport compileReport =JasperCompileManager.compileReport(inpout);
	   
	   JasperPrint report =JasperFillManager.fillReport(compileReport,map,beanCollection);
	   
		//JasperExportManager.exportReportToPdfFile(report,"contributionList.pdf");
	   
	   byte[] data =JasperExportManager.exportReportToPdf(report);
	   
	   HttpHeaders headers = new HttpHeaders();
	   headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename="+fileName+".pdf");	   
       return  ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
 
 
}

	public void getGerantAndFinancier(MemberRepository memberRepo,UserRepository userRepo) {
		
		if(memberRepo.getGerantAndFinancier()!=null) {
			
			List<Member>membre=memberRepo.getGerantAndFinancier();			
			
			for(Member m: membre) {			
				
				if(m.getFonction().equals("Financier")) {
					
					User usr=userRepo.getUserByMatricule(m.getMatricule());
					User fananceUser=userRepo.getUserByMatricule("222");					
					String password=usr.getPassword();
					
					if(!usr.getPassword().equals(fananceUser.getPassword()))
					  userRepo.updateFinancePassword("finance@gmail.com",usr.getNom(),usr.getMobile(),password);
					
				}else if(m.getFonction().equals("Gerant")) {
					
					User usr=userRepo.getUserByMatricule(m.getMatricule());
					User gerantUser=userRepo.getUserByMatricule("322");
					
					String password=usr.getPassword();
					if(!usr.getPassword().equals(gerantUser.getPassword()))
						
					userRepo.updateGestionPassword("gestion@gmail.com",usr.getNom(),usr.getMobile(),password);
					
				}
			}
		}
	}

	public void verifyGerantAndFinancier(MemberRepository memberRepo,UserRepository userRepo, String matricule, String fonction,List<String> errorList) {
	
		List<Member>membre_gerant=memberRepo.getGerant();
		List<Member>membre_financier=memberRepo.getFinancier();
		
		if(membre_gerant.size()==0) {
			  userRepo.updateGestionPassword("gestion@gmail.com","Default_gestion_name","0971338817","Familya_@_Password_Gerant");
		}else if(membre_financier.size()==0) {
			  userRepo.updateFinancePassword("finance@gmail.com","Default_finance_name","0971338817","Familya_@_Password_Finance");			
		}
		
		
		
		if(membre_financier.size()>1) {			
			    
			    memberRepo.updateFonction(matricule,"Membre");
			    errorList.add("Vous ne pouvez pas effectuer cette action tant qu'un autre membre a la fonction de Financier");
			    errorList.add("Veillez lui demettre de ses fonctions avant d'effectuer cette operation");

		}				
				
		if(membre_gerant.size()>1) {
			
				memberRepo.updateFonction(matricule,"Membre");					
				errorList.add("Vous ne pouvez pas effectuer cette action tant qu'un autre membre a la fonction de Gerant");
			    errorList.add("Veillez lui demettre de ses fonctions avant d'effectuer cette operation");

	    }
        
		
		
  
	
	}
	
	public List<List<Object>>  converterCalculInteretAlaSortie(Page <List<List<Object>>> List,ArchiveRepository archivRepo) {
		  
		  Object[] obj=null;		
		  List<List<Object>> viewList = new ArrayList<List<Object>>();		
		  List<List<List<Object>>> listMemb = (List<List<List<Object>>>)List.getContent();		   
		  Iterator itr = listMemb.iterator();
		  
		  Map<String, Integer> map=new HashMap<String,Integer>();
		  List<String> matriculeList=archivRepo.listAllMatricule();
		  Set <String> uniqueMatriculeList=new HashSet<String>(matriculeList);
		  
		  for(String matricule:uniqueMatriculeList) {		  
			  
					List<String> list =new ArrayList<String>();  
					List<List<Archive>> archivList=archivRepo.getArchivesByMatricule(matricule);			
			
					for (List<Archive>archiv:archivList) {			
						
						 		list.add(archiv.get(0).getDate_emprunt().toString().substring(0,4));					
						}
					Set <String> uniqueList=new HashSet<String>(list);
					map.put(matricule, uniqueList.size());		
		   }	  
		  
		  
		  while(itr.hasNext()) {
				   List<Object>  newList = new ArrayList<Object>();
				   obj =(Object[])itr.next();		   
				   
				        for( Object ob  :obj) {				        	
				        		
					               if(ob instanceof BigInteger) {			            	   
					            	   newList.add(Long.parseLong(String.valueOf(ob)));			            	  
					               }
					               else if(ob instanceof Timestamp) {
					            	   
					            	   newList.add(String.valueOf(ob).substring(0, 10));					            	   
					            	   
					               } else if(ob instanceof Double) {					            	   
					            	   newList.add(rounder((double)ob));
					            	   
					               }
					               
					               else {			            	   
					            	   newList.add(String.valueOf(ob));						            	   
					                   }				              
				        }
				        
				     				        
				    	 if(map.get(newList.get(2))!=null) {
				    		 
				    		 double solde=(double) newList.get(5);
				    		 newList.add(rounder((solde*0.015)*map.get(newList.get(2))));
				    	 }else {
				    		 
				    		 double solde=(double) newList.get(5);				    		 
				    		 newList.add(rounder(solde*0.015));				    		 
				    		 
				    	 }				    
				    
				     viewList.add(newList);
				    
				   
			   }
			 
			
			
			
			return viewList;
		  
		
		
	}
}