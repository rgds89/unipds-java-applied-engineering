package com.rogersilva.api.hello.controller;

import com.rogersilva.api.hello.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private ArrayList<Product> products;

    public ProductController() {
        products = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            products.add(createProduct((long) i));
        }
    }

    @GetMapping
    public ResponseEntity<ArrayList<Product>> getProducts() {
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product p) {
        Product product = new Product();
        product.setId(p.getId());
        product.setName(p.getName());
        product.setPrice(p.getPrice());
        products.add(product);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId())
                .toUri();

        return ResponseEntity.created(uri).body(product);
    }

    @GetMapping("/getProduct")
    public ResponseEntity<Product> getProduct(@RequestParam int id) {
        return ResponseEntity.ok(products.stream().filter(p -> p.getId() == id).findFirst().orElse(null));
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<Product> updateProduct(@RequestParam int id, @RequestBody Product p) {
        int position = IntStream.range(0, products.size()).filter(i -> products.get(i).getId() == id).findFirst().orElse(-1);
        if (position >= 0) {
            products.set(position, p);
        }
        return ResponseEntity.ok(products.stream().filter(prod -> prod.getId() == id).findFirst().orElse(null));
    }

    @DeleteMapping("/deleteProduct")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProduct(@RequestParam int id) {
        products.remove(IntStream.range(0, products.size()).filter(i -> products.get(i).getId() == id).findFirst().orElse(-1));
    }

    @GetMapping("/sort")
    public ResponseEntity<List<Product>> getProductsOrderByPrice(@RequestParam(required = false) String order) {
        return ResponseEntity.ok(order == null ? products :
                order.equals("asc") ? products.stream().sorted(Comparator.comparing(Product::getPrice)).toList() :
                        order.equals("desc") ? products.stream().sorted(Comparator.comparing(Product::getPrice).reversed()).toList() : null);
    }

    private Product createProduct(Long id) {
        Product p = new Product();
        p.setId(id);
        p.setName("PROD.A." + id);
        p.setPrice(BigDecimal.valueOf(id).multiply(new BigDecimal(100)));
        return p;
    }
}
