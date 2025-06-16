package com.example.simple.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.simple.model.Product;
import com.example.simple.repository.ProductRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ProductService {
	private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void initProducts() {
        if (productRepository.count() == 0) { // Avoid duplicate inserts
            productRepository.save(new Product("Laptop Pro", "Powerful laptop for professionals.", 120000L, "usd",
            		"https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=400&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8bGFwdG9wfGVufDB8fDB8fHww"));
            productRepository.save(new Product("Wireless Mouse", "Ergonomic wireless mouse.", 2500L, "usd",
            		"https://images.unsplash.com/photo-1527814050087-3793815479db?w=400&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8d2lyZWxlc3MlMjBtb3VzZXxlbnwwfHwwfHx8MA%3D%3D"));
            productRepository.save(new Product("Mechanical Keyboard", "Tactile and responsive keyboard.", 7500L, "usd",
            		"https://images.unsplash.com/photo-1625130694338-4110ba634e59?w=400&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTF8fG1lY2hhbmljYWwlMjBrZXlib2FyZHxlbnwwfHwwfHx8MA%3D%3D"));
            productRepository.save(new Product("HeadPhone Black", "A headphones for quality sound.", 3000L, "usd",
            		"https://plus.unsplash.com/premium_photo-1679913792954-6a5a93ae4cff?w=400&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTd8fGxhcHRvcCUyMHN0YW5kfGVufDB8fDB8fHww"));
            productRepository.save(new Product("Webcam Full HD", "HD webcam for video calls.", 4500L, "usd",
            		"https://images.unsplash.com/photo-1611703523714-663c38cd9f55?w=400&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8d2ViJTIwY2FtfGVufDB8fDB8fHww"));
        }
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

}