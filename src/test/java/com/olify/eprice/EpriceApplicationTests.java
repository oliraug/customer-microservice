package com.olify.eprice;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EpriceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class EpriceApplicationTests {
	@LocalServerPort
    private int port = 0;
    private RestTemplate restTemplate = new RestTemplate();

	@Test
	public void testLastnLoads() throws Exception {
		ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:" + port + "/lastn/",
                String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
	}
	
	@Test
    public void testEurekaLoads() throws Exception {
        ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:" + port,
                String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

}
