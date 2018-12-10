package com.olify.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.olify.component.OlifyCustomerRegistrar;
import com.olify.model.OlifyCustomer;

@RestController
@EnableHystrixDashboard
public class OlifyCustomerController {
	
	//OlifyCustomerRegistrar component is added to handle the business logic
	@Autowired OlifyCustomerRegistrar customerRegistrar;
	
	
	/*logger for this class and subclasses*/
	protected final Log logger = LogFactory.getLog(getClass());
	
	/*Retrieve all customers*/
	@RequestMapping(value="/customers", method=RequestMethod.GET, produces="application/json")
	public ResponseEntity<List<OlifyCustomer>> listAllCustomers(HttpServletRequest request){
		List<OlifyCustomer> customers = customerRegistrar.findAll();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8"); 
		/*To generate a Device object from HttpServletRequest*/
		Device currentDevice = DeviceUtils.getCurrentDevice(request);
		if(currentDevice.isNormal()) {
			logger.info("Desktop user");
			}
		else if(currentDevice.isMobile()) {
			logger.info("Mobile user");
			}
		else if(currentDevice.isTablet()) {
			logger.info("Tablet user");
		}
			if(customers.isEmpty()){
				return new ResponseEntity<List<OlifyCustomer>>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<List<OlifyCustomer>>(customers, headers, HttpStatus.OK);
	}
	
	/*
	 * Get a single customer
	 */
	@RequestMapping(value="olifycustomer/{customerId}", method=RequestMethod.GET, produces="application/json")
	public ResponseEntity<OlifyCustomer> getCustomer(@PathVariable Long customerId, HttpServletRequest request){
		OlifyCustomer customer = customerRegistrar.getOne(customerId);
		/*To generate a Device object from HttpServletRequest*/
		Device currentDevice = DeviceUtils.getCurrentDevice(request);
		if(currentDevice.isNormal()) {
			logger.info("Desktop user");
			}
		else if(currentDevice.isMobile()) {
			logger.info("Mobile user");
			}
		else if(currentDevice.isTablet()) {
			logger.info("Tablet user");
		}
		return new ResponseEntity<OlifyCustomer>(customer, HttpStatus.OK);
	}
	
	/*
	 * Fetch customers with given id
	 */
	@RequestMapping(value="/olifycustomer/{customer_id}", method=RequestMethod.GET, produces="application/json")
	public ResponseEntity<OlifyCustomer> findByCustomerId(@PathVariable("customer_id") Long customerId, HttpServletRequest request){
		logger.info(String.format("customer-service findByCustomerId() invoked:{} for {}", customerRegistrar.getClass().getName(), customerId));
		customerId = customerId.longValue();
		Optional<OlifyCustomer> customer;
		/*To generate a Device object from HttpServletRequest*/
		Device currentDevice = DeviceUtils.getCurrentDevice(request);
		if(currentDevice.isNormal()) {
			logger.info("Desktop user");
			}
		else if(currentDevice.isMobile()) {
			logger.info("Mobile user");
			}
		else if(currentDevice.isTablet()) {
			logger.info("Tablet user");
		}
		
		try {
			customer = customerRegistrar.findById(customerId);
		} catch (Exception e) {
			logger.info(Level.SEVERE, e);
			return new ResponseEntity<OlifyCustomer>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return customer != null ? new ResponseEntity<OlifyCustomer>(HttpStatus.OK)
				: new ResponseEntity<OlifyCustomer>(HttpStatus.NO_CONTENT);
	}
	
	/*create a customer*/
	@RequestMapping(value="/olifycustomer", method=RequestMethod.POST, produces="application/json")
	public ResponseEntity<OlifyCustomer> createCustomer(@PathVariable String customername, @RequestBody OlifyCustomer customer, UriComponentsBuilder ucBuilder, HttpServletRequest request){
		System.out.println("Creating customer" + customer.getCustomerName());
		if(customerRegistrar.isCustomerNameExist(customername)){
			System.out.println("A customer with name" + customer.getCustomerName() +"already exists");
			return new ResponseEntity<OlifyCustomer>(HttpStatus.CONFLICT);
		} 
		/*To generate a Device object from HttpServletRequest*/
		Device currentDevice = DeviceUtils.getCurrentDevice(request);
		if(currentDevice.isNormal()) {
			logger.info("Desktop user");
			}
		else if(currentDevice.isMobile()) {
			logger.info("Mobile user");
			}
		else if(currentDevice.isTablet()) {
			logger.info("Tablet user");
		}
		customerRegistrar.createCustomer(customer);
		customerRegistrar.save(customer);
		final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/olifycustomer/{customerId}").build().expand(customer.getCustomerId()).toUri();
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Product created-", String.valueOf(customer.getCustomerName()));
		headers.add("Content-Type","application/json; charset=utf-8");
		headers.setLocation(location);
		final ResponseEntity<OlifyCustomer> entity = new ResponseEntity<OlifyCustomer>(headers,HttpStatus.CREATED);
		return entity;
	}
	
	/*create a update product*/
	@RequestMapping(value="/olifycustomer/{customerId}", method=RequestMethod.PUT)
	public ResponseEntity<OlifyCustomer> updateCustomer(@PathVariable ("customerId") Long customerId, @RequestBody OlifyCustomer customer, HttpServletRequest request){
		/*To generate a Device object from HttpServletRequest*/
		Device currentDevice = DeviceUtils.getCurrentDevice(request);
		if(currentDevice.isNormal()) {
			logger.info("Desktop user");
			}
		else if(currentDevice.isMobile()) {
			logger.info("Mobile user");
			}
		else if(currentDevice.isTablet()) {
			logger.info("Tablet user");}
		HttpHeaders headers = new HttpHeaders();
		OlifyCustomer isExist = customerRegistrar.getOne(customerId);
		if(isExist == null){
			return new ResponseEntity<OlifyCustomer>(HttpStatus.NOT_FOUND);
		}else if(customer == null){
			return new ResponseEntity<OlifyCustomer>(HttpStatus.BAD_REQUEST);
		}
		customerRegistrar.saveOrUpdate(customer);
		
		headers.add("Product updated", String.valueOf(customer.getCustomerId()));
		return new ResponseEntity<OlifyCustomer>(customer, headers, HttpStatus.OK);
		}
	
	/*
	 * Delete a product
	 */
	@RequestMapping(value="/olifycustomer/{customername}", method=RequestMethod.DELETE, produces="application/json")
	public ResponseEntity<OlifyCustomer> deleteCustomer(@PathVariable String customername, @PathVariable Long customerId,
			@RequestBody OlifyCustomer customer, HttpServletRequest request){
		/*To generate a Device object from HttpServletRequest*/
		Device currentDevice = DeviceUtils.getCurrentDevice(request);
		if(currentDevice.isNormal()) {
			logger.info("Desktop user");
			}
		else if(currentDevice.isMobile()) {
			logger.info("Mobile user");
			}
		else if(currentDevice.isTablet()) {
			logger.info("Tablet user");
			}
		if(!customerRegistrar.isCustomerNameExist(customername)) {
			return new ResponseEntity<OlifyCustomer>(HttpStatus.NOT_FOUND);
		}
		customerRegistrar.deleteById(customerId);
		customerRegistrar.save(customer);
		return new ResponseEntity<OlifyCustomer>(HttpStatus.OK); 
	}


}
