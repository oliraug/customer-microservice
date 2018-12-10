package com.olify.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.olify.model.OlifyCustomer;

@RepositoryRestResource(collectionResourceRel = "olify_customer", path = "olify_customer")
public interface OlifyCustomerRepository extends JpaRepository<OlifyCustomer, Long>{

	Optional<Integer> forCustomer(final String customerName);
	public void saveOrUpdate(OlifyCustomer customer);
	Optional<OlifyCustomer> findByCustomerName(@Param("customerName") String customerName);
}