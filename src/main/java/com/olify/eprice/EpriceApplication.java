/*
 * Eureka server is used for service discovery
 */
package com.olify.eprice;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.olify.component.OlifyCustomerRegistrar;
import com.olify.model.OlifyCustomer;
import com.olify.repository.OlifyCustomerRepository;

@SpringBootApplication
@EnableEurekaServer
public class EpriceApplication {
	public static final String CUSTOMERS_SERVICE_URL = "http://customer-microservice-producer";

	public static void main(String[] args) {
		SpringApplication.run(EpriceApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(OlifyCustomerRepository repo) {
		return (evt) -> {
			repo.save(new OlifyCustomer(1L, "Moses Masiga", "077340024", "emacsone@aol.com", "Mutungo", "active", new Date(11/29/2018), 0));
		};
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	@Bean
	public OlifyCustomerRepository customerRepository(){
		return new OlifyCustomerRegistrar(CUSTOMERS_SERVICE_URL, null, null);
	}
}
