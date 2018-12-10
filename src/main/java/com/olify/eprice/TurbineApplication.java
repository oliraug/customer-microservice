/*
 * Turbine -> can be used to consolidate the Hystrix metrics and has a Hystrix dashboard
 */
package com.olify.eprice;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.cloud.netflix.turbine.amqp.EnableTurbineAmqp;
import org.springframework.context.annotation.Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;

@EnableAutoConfiguration
@EnableTurbine
@EnableTurbineAmqp
@EnableEurekaClient
@EnableHystrixDashboard
public class TurbineApplication {
	private static final Logger LOG = LoggerFactory.getLogger(TurbineApplication.class);
	@Value("${app.rabbitmq.host:localhost}")
	String rabbitMQHost;
	@Bean
	public ConnectionFactory connectionFactory() {
		LOG.info("Creating RabbitMQHost ConnectionFactory for host: {}", rabbitMQHost);
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(rabbitMQHost);
		return cachingConnectionFactory;
	}

	public static void main(String[] args) {
		SpringApplication.run(TurbineApplication.class, args);
	}

}