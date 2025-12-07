package com.sunilkumarl.demo.service;

import com.sunilkumarl.demo.model.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class CustomerService {

    private final RestClient restClient;


    public CustomerService(RestClient.Builder builder,
                           @Value("${external.service.base-url:http://localhost:3000}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public List<Customer> findAll() {
        return restClient.get()
                .uri("/customers")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Customer>>() {});
    }

    public Customer findByCustomerId(String customer_id) {
        return restClient.get()
                .uri("/customers?customer_id={customer_id}", customer_id)
                .retrieve()
                .body(Customer.class);
    }

    public List<Customer> findByCustomerName(String customer_name) {
        return restClient.get()
                .uri("/customers?customer_name={customer_name}", customer_name)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Customer>>() {});
    }

}
