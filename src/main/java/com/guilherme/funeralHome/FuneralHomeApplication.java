package com.guilherme.funeralHome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //cobrança mensal dos planos funerários (BillingScheduler)
public class FuneralHomeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FuneralHomeApplication.class, args);
	}

}
