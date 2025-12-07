package com.sunilkumarl.demo.controller;


import com.sunilkumarl.demo.model.Product;
import com.sunilkumarl.demo.service.CustomerOrdersProductFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customer-products")
public class CustomerProductController {

    private final CustomerOrdersProductFacade customerProductService;

    public CustomerProductController(CustomerOrdersProductFacade customerProductService) {
        this.customerProductService = customerProductService;
    }

    @GetMapping
    public List<Product> getCustomerProducts(@RequestParam String customerName) {
        return customerProductService.getProductsByCustomerName(customerName);
    }

}
