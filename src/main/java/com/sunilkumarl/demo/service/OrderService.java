package com.sunilkumarl.demo.service;

import com.sunilkumarl.demo.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OrderService {

    private final RestClient restClient;


    public OrderService(RestClient.Builder builder,
                        @Value("${external.service.base-url:http://localhost:3000}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public List<Order> findAll() {
        return restClient.get()
                .uri("/orders")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Order>>() {});
    }

    public List<Order> findOrdersByCustomerId(Integer customer_id) {
        return restClient.get()
                .uri("/orders?customer_id={customer_id}", customer_id)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Order>>() {});
    }
}
