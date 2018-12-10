package com.olify.eprice;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Collection;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.olify.client.CustomerClient;
import com.olify.model.OlifyCustomer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CustomerConsumerDrivenContractTest {

	@Autowired
	CustomerClient customerClient;

	@Test
	public void testFindAll() throws Exception {
		Collection<OlifyCustomer> result = customerClient.findAll();
		assertEquals(1,
				result.stream()
						.filter(c -> (c.getCustomerName().equals("Moses Masiga") && c.getCustomerMobile().equals("0773405024")
								&& c.getCustomerEmail().equals("masiga2005@gmail.com")
								&& c.getCustomerAddress().equals("Mutungo Bbina") && c.getCustomerStatus().equals("active")
								&& c.getCustomerJoinDate().equals(12/5/2018)))
						.count());
	}

	@Test
	public void testGetOne() throws Exception {
		Collection<OlifyCustomer> allCustomer = customerClient.findAll();
		Long id = allCustomer.iterator().next().getCustomerId();
		OlifyCustomer result = customerClient.getOne(id);
		//assertEquals(id.longValue(), result.getCustomerId());
		assertThat(result.getCustomerId()).isEqualTo(id.longValue());
	}

	@Test
	public void testValidCustomerId() throws Exception {
		Collection<OlifyCustomer> allCustomer = customerClient.findAll();
		Long id = allCustomer.iterator().next().getCustomerId();
		assertThat(customerClient.isValidCustomerId(id)).isEqualTo(true);
		assertThat(customerClient.isValidCustomerId(-1)).isEqualTo(false);
		/*assertTrue(customerClient.isValidCustomerId(id));
		assertFalse(customerClient.isValidCustomerId(-1));*/
	}

}
