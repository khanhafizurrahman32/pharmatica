package org.example.pharmaticb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class PharmaticBApplication {

	public static void main(String[] args) {
		SpringApplication.run(PharmaticBApplication.class, args);
	}

}
