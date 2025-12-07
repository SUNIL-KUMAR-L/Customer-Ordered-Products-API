package com.sunilkumarl.demo.model;

public record OrderLine(
        Integer product_qty,
        Double product_price,
        Integer product_id,
        Integer order_line_seq_id,
        Integer order_line_id,
        Integer order_id)
{}



