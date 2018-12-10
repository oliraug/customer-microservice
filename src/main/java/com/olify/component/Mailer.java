/*
 * The Mailer component is used to send an e-mail to the customer
 */
package com.olify.component;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Mailer {
	@Autowired
	private JavaMailSender javaMailService;
	public void sendMail(String customerEmail){
		SimpleMailMessage mailMessage =new SimpleMailMessage();
		mailMessage.setTo(customerEmail);
		mailMessage.setSubject("Registration");
		mailMessage.setText("Successfully Registered");
		javaMailService.send(mailMessage);
	}
}