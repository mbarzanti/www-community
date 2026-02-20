package it.poste.coec.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import it.poste.common.cics.annotations.EnableCICSPool;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages =  {"it.poste.coec.api"})
@EnableCICSPool
@EnableAspectJAutoProxy
public class CoecApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoecApiApplication.class);
	}

}
