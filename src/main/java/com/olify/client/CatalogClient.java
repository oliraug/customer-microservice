/**
 * 
 */
package com.olify.client;

/**
 * @author olify
 *
 */
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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.olify.epriceproduct.model.OlifyProduct;

@SpringBootApplication
//@EnableCircuitBreaker
@Component
public class CatalogClient {

	/**
	 * @EnableCircuitBreaker -> The main class of microservice that consumes other services, forexample
	 * if a user service would like to get order details, where a user has placed an order
	 */
	private final Logger log = LoggerFactory.getLogger(CatalogClient.class);

	public static class ProductPagedResources extends PagedResources<OlifyProduct> {

	}

	private RestTemplate restTemplate;
	private String catalogServiceHost;
	private long catalogServicePort;
	private boolean useRibbon;
	private LoadBalancerClient loadBalancer;
	private Collection<OlifyProduct> productsCache = null;

	@Autowired
	public CatalogClient(
			@Value("${catalog.service.host:catalog}") String catalogServiceHost,
			@Value("${catalog.service.port:8080}") long catalogServicePort,
			@Value("${ribbon.eureka.enabled:false}") boolean useRibbon) {
		super();
		this.restTemplate = getRestTemplate();
		this.catalogServiceHost = catalogServiceHost;
		this.catalogServicePort = catalogServicePort;
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

	@HystrixCommand(fallbackMethod = "priceCache", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public double price(long productId) {
		return getOne(productId).getPrice();
	}

	public double priceCache(long productId) {
		return getOneCache(productId).getPrice();
	}

	@HystrixCommand(fallbackMethod = "getProductsCache", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public Collection<OlifyProduct> findAll() {
		PagedResources<OlifyProduct> pagedResources = restTemplate.getForObject(
				catalogURL(), ProductPagedResources.class);
		this.productsCache = pagedResources.getContent();
		return pagedResources.getContent();
	}

	@SuppressWarnings("unused")
	private Collection<OlifyProduct> getProductsCache() {
		return productsCache;
	}

	private String catalogURL() {
		String url;
		if (useRibbon) {
			ServiceInstance instance = loadBalancer.choose("CATALOG");
			url = String.format("http://%s:%s/catalog/", instance.getHost(), instance.getPort());
		} else {
			url = String.format("http://%s:%s/catalog/", catalogServiceHost, catalogServicePort);
		}
		log.trace("Catalog: URL {} ", url);
		return url;
	}

	@HystrixCommand(fallbackMethod = "getOneCache", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2") })
	public OlifyProduct getOne(long productId) {
		return restTemplate.getForObject(catalogURL() + productId, OlifyProduct.class);
	}

	public OlifyProduct getOneCache(long productId) {
		return productsCache.stream().filter(i -> (i.getProductId() == productId))
				.findFirst().get();
	}
	
}