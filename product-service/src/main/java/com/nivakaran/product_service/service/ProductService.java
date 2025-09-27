package com.nivakaran.product_service.service;

import com.nivakaran.product_service.dto.ProductRequest;
import com.nivakaran.product_service.dto.ProductResponse;
import com.nivakaran.product_service.model.Product;
import com.nivakaran.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor  // this will create all the required constructors for us
@Slf4j // Used for logging
public class ProductService {

    private final ProductRepository productRepository;


    public void createProduct(ProductRequest productRequest) {



        //Instance of the product created
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }


    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}

