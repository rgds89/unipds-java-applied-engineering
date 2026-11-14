package com.rogersilva.api.hello.controller;

import com.rogersilva.api.hello.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {
    @GetMapping("/products")
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            products.add(createProduct((long) i));
        }
        return products;
    }

    private Product createProduct(Long id) {
        Product p = new Product();
        p.setId(id);
        p.setName("PROD.A." + id);
        p.setPrice(BigDecimal.valueOf(id).multiply(new BigDecimal(100)));
        return p;
    }
}
