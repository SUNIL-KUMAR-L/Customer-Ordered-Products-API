package com.sunilkumarl.demo.model;

import java.util.List;

public record Order(
        List<OrderLine> order_lines,
        Double order_total,
        Integer customer_id,
        String order_datetime,
        Integer order_id)
{}





