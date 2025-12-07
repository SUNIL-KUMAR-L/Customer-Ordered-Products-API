package com.sunilkumarl.demo.service;

import com.sunilkumarl.demo.model.*;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerOrdersProductFacade {

    private final CustomerService customerService;
    private final OrderService orderService;
    private final ProductService productService;


    public CustomerOrdersProductFacade(CustomerService customerService, OrderService orderService, ProductService productService) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.productService = productService;
    }

    public List<Product> getProductsByCustomerName(String customerName) {

        // 1. Fetch customers
        List<Customer> customerList = customerService.findByCustomerName(customerName);
        if (customerList == null || customerList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        Customer customer = customerList.get(0);
        Integer customerId = customer.customer_id();

        // 2. Fetch orders for customer
        List<Order> orders = orderService.findOrdersByCustomerId(customerId);
        if (orders == null || orders.isEmpty()) {
            return Collections.emptyList();
        }
        // collect product ids from order lines
        Set<Integer> productIds = new HashSet<>();
        for (Order o : orders) {
            List<OrderLine> lines = o.order_lines();
            if (lines != null) {
                for (OrderLine ol : lines) {
                    if (ol != null && ol.product_id() != null) {
                        productIds.add(ol.product_id());
                    }
                }
            }
        }
        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 3. Fetch all products and filter
        List<Product> allProducts = productService.findAll();
        if (allProducts == null || allProducts.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, Product> productMap = allProducts.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.product_id() != null)
                .collect(Collectors.toMap(Product::product_id, p -> p));

        return productIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    public CustomerOrderedProducts getCustomerOrderedProducts(String customerName) {

        // 1. Fetch customers
        List<Customer> customerList = customerService.findByCustomerName(customerName);
        if (customerList == null || customerList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        Customer customer = customerList.get(0);
        Integer customerId = customer.customer_id();

        // 2. Fetch orders for customer
        List<Order> orders = orderService.findOrdersByCustomerId(customerId);
        if (orders == null || orders.isEmpty()) {
            //return Collections.emptyList();
            return new CustomerOrderedProducts(customer, Collections.emptyList(), Collections.emptyList());
        }
        // collect product ids from order lines
        Set<Integer> productIds = new HashSet<>();
        for (Order o : orders) {
            List<OrderLine> lines = o.order_lines();
            if (lines != null) {
                for (OrderLine ol : lines) {
                    if (ol != null && ol.product_id() != null) {
                        productIds.add(ol.product_id());
                    }
                }
            }
        }
        if (productIds.isEmpty()) {
            return new CustomerOrderedProducts(customer, orders, Collections.emptyList());
        }
        // 3. Fetch all products and filter
        List<Product> allProducts = productService.findAll();
        if (allProducts == null || allProducts.isEmpty()) {
            return new CustomerOrderedProducts(customer, orders, Collections.emptyList());
        }

        Map<Integer, Product> productMap = allProducts.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.product_id() != null)
                .collect(Collectors.toMap(Product::product_id, p -> p));

        List<Product> products = productIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        CustomerOrderedProducts customerOrderedProducts = new CustomerOrderedProducts(customer, orders, products);
        return customerOrderedProducts;
    }

}
