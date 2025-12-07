package com.sunilkumarl.demo.service;

import com.sunilkumarl.demo.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ProductService {

    private final RestClient restClient;

    public ProductService(RestClient.Builder builder,
                          @Value("${external.service.base-url:http://localhost:3000}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public List<Product> findAll() {
        return restClient.get()
                .uri("/products")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Product>>() {});
    }

    public List<Product> findProductById(Integer product_id) {
        return restClient.get()
                .uri("/products?product_id={product_id}", product_id)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Product>>() {});
    }

}
