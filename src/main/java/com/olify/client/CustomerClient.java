/**
 * 
 */
package com.olify.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.olify.model.OlifyCustomer;

/**
 * @author olify
 *
 */
@Component
public class CustomerClient {

	/**
	 * 
	 */
	private final Logger log = LoggerFactory.getLogger(CustomerClient.class);

	public static class ProductPagedResources extends PagedResources<OlifyCustomer> {

	}

	private RestTemplate restTemplate;
	private String customerServiceHost;
	private long customerServicePort;
	private boolean useRibbon;
	private LoadBalancerClient loadBalancer;
	//private Collection<OlifyCustomer> itemsCache = null;

	@Autowired
	public CustomerClient(
			@Value("${customer.service.host:catalog}") String customerServiceHost,
			@Value("${customer.service.port:8080}") long customerServicePort,
			@Value("${ribbon.eureka.enabled:false}") boolean useRibbon) {
		super();
		this.restTemplate = getRestTemplate();
		this.customerServiceHost = customerServiceHost;
		this.customerServicePort = customerServicePort;
		this.useRibbon = useRibbon;
	}

	@Autowired(required = false)
	public void setLoadBalancer(LoadBalancerClient loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	protected RestTemplate getRestTemplate() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));
		converter.setObjectMapper(mapper);

		return new RestTemplate(
				Collections.<HttpMessageConverter<?>>singletonList(converter));
	}

	public boolean isValidCustomerId(long customerId) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<String> entity = restTemplate.getForEntity(
					customerURL() + customerId, String.class);
			return entity.getStatusCode().is2xxSuccessful();
		} catch (final HttpClientErrorException e) {
			if (e.getStatusCode().value() == 404)
				return false;
			else
				throw e;
		}
	}

	public Collection<OlifyCustomer> findAll() {
		PagedResources<OlifyCustomer> pagedResources = getRestTemplate()
				.getForObject(customerURL(), ProductPagedResources.class);
		return pagedResources.getContent();
	}
	private String customerURL() {
		String url;
		if (useRibbon) {
			ServiceInstance instance = loadBalancer.choose("CUSTOMER");
			url = String.format("http://%s:%s/customer/", instance.getHost(), instance.getPort());
		} else {
			url = String.format("http://%s:%s/customer/", customerServiceHost, customerServicePort);
		}
		log.trace("Customer: URL {} ", url);
		return url;

	}

	public OlifyCustomer getOne(long customerId) {
		return restTemplate.getForObject(customerURL() + customerId,
				OlifyCustomer.class);
	}

}
