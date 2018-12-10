package com.olify.eprice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atLeastOnce;
//import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.core.JsonParseException;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.olify.component.OlifyCustomerRegistrar;
import com.olify.controller.OlifyCustomerController;
import com.olify.model.OlifyCustomer;

public class OlifyCustomerControllerTest {
	private static final Long CUSTOMERID = 1L;
	private static final String CUSTOMERNAME = "Masiga Moses";
	private static final String CUSTOMERMOBILE = "0704008863";
	
	@InjectMocks
	OlifyCustomerController mockCustomerController;
	@Autowired @Mock
	OlifyCustomerRegistrar mockCustomerRegistrar;
	private MockMvc mockMvc;
	
	@Mock private OlifyCustomer mockCustomer;
	@Autowired 
	private WebApplicationContext wac;
	private RestTemplate restTemplate;
	private int serverPort;

	@Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
	
	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		mockCustomerRegistrar = mock(OlifyCustomerRegistrar.class, Mockito.RETURNS_DEEP_STUBS);
		restTemplate = new RestTemplate();
	}

	@After
	public void tearDown() throws Exception {
		mockCustomerController = null;
		mockCustomer = null;
		mockCustomerRegistrar = null;
		mockMvc = null;
		wac = null;
	}

	public String mapToJson(Object obj) throws JsonProcessingException {
	      ObjectMapper objectMapper = new ObjectMapper();
	      return objectMapper.writeValueAsString(obj);
	   }
	   protected <T> T mapFromJson(String json, Class<T> clazz)
	      throws JsonParseException, JsonMappingException, IOException {
	      
	      ObjectMapper objectMapper = new ObjectMapper();
	      return objectMapper.readValue(json, clazz);
	   }
	
	@Test
	public void testGetCustomerById() throws Exception{
		OlifyCustomer customer = new OlifyCustomer();
		customer.setCustomerId(1L);
		Mockito.when(mockCustomerRegistrar.getOne(1L)).thenReturn(customer);
		
		OlifyCustomer oc = mockCustomerController.getCustomer(1L);
		Mockito.verify(mockCustomerRegistrar).getOne(1L);
		assertEquals(1l, oc.getCustomerId().longValue());
	}
	
	@Test
	public void testIsCustomerFormDisplayed() throws Exception {
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(customerURL() + "/form.html", String.class);
        assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
        assertTrue(resultEntity.getBody().contains("<form"));
	}

	private String customerURL() {
		return "http://localhost:" + serverPort + "/";
	}
	
	@Test
	public void testDeleteCustomerIsSuccess() throws Exception {
		String uri = "/olify_customer/2";
		Mockito.when(mockCustomerRegistrar.findByCustomerName("CUSTOMERNAME")).thenReturn(null);
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
	      int status = mvcResult.getResponse().getStatus();
	      assertEquals(200, status);
	      String content = mvcResult.getResponse().getContentAsString();
	      //assertThat("Customer deleted successsfully!", isEqualTo(content));
	      assertEquals(content, "Customer deleted successsfully!");
	      Mockito.verify(mockCustomerRegistrar, times(1)).findByCustomerName(CUSTOMERNAME);
	      Mockito.verifyNoMoreInteractions(mockCustomerRegistrar);
	}
	
	@Test
	public void testCreateCustomerReturnsSuccess() throws Exception {
		String uri = "/olify_customer";
		OlifyCustomer customer = new OlifyCustomer();
		customer.setCustomerId(CUSTOMERID);
		customer.setCustomerName(CUSTOMERNAME);
		customer.setCustomerMobile(CUSTOMERMOBILE);
		String inputJson = super.mapToJson(customer);
		Mockito.when(mockCustomerRegistrar.registerCustomer(mockCustomer)).thenReturn(customer);
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(uri)
		         .contentType(MediaType.APPLICATION_JSON_VALUE)
		         .content(inputJson)).andReturn();
		      
		      int status = mvcResult.getResponse().getStatus();
		      assertEquals(200, status);
		      String content = mvcResult.getResponse().getContentAsString();
		      assertEquals(content, "Customer created successfully!");
		      Mockito.verify(mockCustomerRegistrar, atLeastOnce()).registerCustomer(customer);
		      Mockito.verifyNoMoreInteractions(mockCustomerRegistrar);
	}

}
