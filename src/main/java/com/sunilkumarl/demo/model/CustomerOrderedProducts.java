package com.sunilkumarl.demo.model;

import java.util.List;

public record CustomerOrderedProducts (Customer customer, List<Order> orders, List<Product> products) {}