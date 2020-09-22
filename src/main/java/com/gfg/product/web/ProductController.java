package com.gfg.product.web;

import com.gfg.product.model.Product;
import com.gfg.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository=productRepository;
    }

    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    @PostMapping("findAll")
    public ResponseEntity findAll() throws Exception {
        ResponseEntity response = null;
        List<Product> productList = null;
        try {
            productList = productRepository.findAll();
            response = new ResponseEntity(productList, HttpStatus.OK);
        } catch(Exception e) {
            response = new ResponseEntity("No products found", HttpStatus.OK);
        }
       return response;
    }

    @PostMapping(value = "findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) throws Exception {

        ResponseEntity<Product> response = null;
        Product product = null;
        try {
            product = productRepository.findById(id).get();
            response = new ResponseEntity(product, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseEntity("Product Not Found with Id "+id, HttpStatus.OK);
        }

        return response;
    }

    @PostMapping("/new")
    ResponseEntity newProduct(@RequestBody Product product) {

        ResponseEntity response = null;
        Product savedProduct = null;
        try {
            savedProduct = productRepository.save(product);
            response = new ResponseEntity(savedProduct, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseEntity("Unable to save product " + product, HttpStatus.OK);
        }

        return response;
    }

    @PostMapping(value = "updateById/{id}")
    ResponseEntity updateProduct(@RequestBody Product product, @PathVariable Long id) throws Exception {

        ResponseEntity response = null;
        try {
            Product existingProduct = productRepository.findById(id).get();

            if(existingProduct == null) {
                throw new Exception("Product to update does not exist");
            } else {
                product.setId(id);
                if (product.getProductId() != null && !product.getProductId().trim().isEmpty()) {
                    existingProduct.setProductId(product.getProductId());
                }

                if (product.getBrand() != null && !product.getBrand().trim().isEmpty()) {
                    existingProduct.setBrand(product.getBrand());
                }

                if (product.getColor() != null && !product.getColor().trim().isEmpty()) {
                    existingProduct.setColor(product.getColor());
                }

                if (product.getTitle() != null && !product.getTitle().trim().isEmpty()) {
                    existingProduct.setTitle(product.getTitle());
                }

                if (product.getDescription() != null && !product.getDescription().trim().isEmpty()) {
                    existingProduct.setDescription(product.getDescription());
                }

                if (product.getPrice() != null && product.getPrice().doubleValue() > 0) {
                    existingProduct.setPrice(product.getPrice());
                }

                Product savedProduct = productRepository.save(existingProduct);
                response = new ResponseEntity<>(savedProduct, HttpStatus.OK);

            }
        } catch (Exception e) {
            response = new ResponseEntity("Unable to update product " + product, HttpStatus.OK);
        }

        return response;
    }

    @PostMapping(value = "deleteById/{id}")
    ResponseEntity<?> deleteProduct(@PathVariable Long id) throws Exception {

        ResponseEntity<?> response = null;
        try {
            productRepository.deleteById(id);
            response = new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseEntity<>(false, HttpStatus.OK);

        }
        return response;
    }
}