/*
 * The Receiver class waits for a message on customer
 * This will receive a message sent by the Customer Profile service. 
 *	On the arrival of a message, it sends an e-mail
 */
package com.olify.component;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
	@Autowired
	Mailer mailer;
	@Bean
	Queue queue() {
		return new Queue("CustomerQ", false);
  }
	@RabbitListener(queues = "CustomerQ")
	public void processMessage(String customerEmail) {
		System.out.println(customerEmail);
		mailer.sendMail(customerEmail);
	}
}