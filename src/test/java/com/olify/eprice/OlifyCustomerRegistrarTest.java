package com.olify.eprice;

import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
 
import static org.assertj.core.api.BDDAssertions.then;
//import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
//import org.mockito.junit.MockitoJUnitRunner;

import com.olify.component.OlifyCustomerRegistrar;
import com.olify.model.OlifyCustomer;
import com.olify.repository.OlifyCustomerRepository;

//@RunWith(MockitoJUnitRunner.class)
public class OlifyCustomerRegistrarTest{

	OlifyCustomerRegistrar mockCustomerRegistrar;
	@Mock
	OlifyCustomerRepository mockCustomerRepository;
	@Mock
	private OlifyCustomer mockCustomer;
	
	private static final String CUSTOMERNAME = "Masiga";
	private static final String CUSTOMERMOBILE = "+256704008863";
	private static final String CUSTOMEREMAIL = "emacsone@aol.com";
	private static final String CUSTOMERADDRESS = "Mutungo";
	private static final String CUSTOMERSTATUS = "active";
	private static final Date CUSTOMERJOINDATE = new Date(11/30/2018);
	private static final int PORT = 8585;
	
	@Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Before
	public void setUp() throws Exception {
		//mockCustomerRegistrar = new OlifyCustomerRegistrar(mockCustomerRepository);
	}
	

	@After
	public void tearDown() throws Exception {
		mockCustomerRepository = null;
	}

	@Test
	public void testCreateCustomers() throws Exception {
		// Given
	    final OlifyCustomer inputCustomer = new OlifyCustomer(null, CUSTOMERNAME, null, null, null, null, null, 0);
	    given(mockCustomerRepository.forCustomer(CUSTOMERNAME))
	      .willReturn(Optional.of(PORT));
	    given(mockCustomerRepository.save(mockCustomer))
	      .willAnswer(answer -> ((OlifyCustomer) answer.getArgument(0)).copyWithCustomerId(randomLong()));
	 
	    // When
	    final OlifyCustomer actualCustomer = mockCustomerRegistrar.registerCustomer(inputCustomer);
	 
	    // Then
	    final OlifyCustomer expectedCustomer = new OlifyCustomer(null, CUSTOMERNAME, CUSTOMERMOBILE, CUSTOMEREMAIL, CUSTOMERADDRESS, CUSTOMERSTATUS, CUSTOMERJOINDATE, PORT);
	    then(actualCustomer.getCustomerId())
	      .as("Check that Customer ID is set when stored.")
	      .isNotNull();
	    then(actualCustomer)
	      .as("Check that Customer name,mobile,email, address,status, join data are correct and customer port is filled in.")
	      .isEqualToIgnoringGivenFields(expectedCustomer, "customerId");
	}

	private Long randomLong() {
		return ThreadLocalRandom.current().nextLong(1000L);
	}

}
