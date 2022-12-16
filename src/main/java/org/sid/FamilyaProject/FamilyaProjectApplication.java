package org.sid.FamilyaProject;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class FamilyaProjectApplication implements CommandLineRunner {
	
	

	public static void main(String[] args) {
		
		SpringApplication.run(FamilyaProjectApplication.class, args);
		
	}	
	

	@Override
	public void run(String... args) throws Exception {
		
		
		//System.out.println("serverPort :====>"+serverPort);
		System.out.println("serverIPAdress :====>"+InetAddress.getLocalHost().getHostAddress());
		
	}
	
	
	
	
		

}
