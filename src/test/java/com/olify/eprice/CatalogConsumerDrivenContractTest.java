package com.olify.eprice;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.olify.client.CatalogClient;
import com.olify.epriceproduct.model.OlifyProduct;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CatalogConsumerDrivenContractTest {

	@Autowired
	CatalogClient catalogClient;

	@Test
	public void testFindAll() {
		Collection<OlifyProduct> result = catalogClient.findAll();
		assertEquals(1, result.stream()
				.filter(p -> (p.getProductName().equals("iPod") && p.getPrice() == 42.0 && p.getProductId() == 1)).count());
	}

	@Test
	public void testGetOne() {
		Collection<OlifyProduct> allProducts = catalogClient.findAll();
		Long id = allProducts.iterator().next().getProductId();
		OlifyProduct result = catalogClient.getOne(id);
		assertThat(result.getProductId()).isEqualTo(id.longValue());
		//assertEquals(id.longValue(), result.getProductId());
	}

}
