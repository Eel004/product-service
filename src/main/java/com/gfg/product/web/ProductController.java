package com.gfg.product.web;

import com.gfg.product.exception.BadRequestException;
import com.gfg.product.exception.ProductNotFoundException;
import com.gfg.product.model.Product;
import com.gfg.product.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * GET findAll get all products
     * @param
     * @return product list in DB and status OK, return NO_CONTENT if product list empty
     * @throws Exception
     */
    @GetMapping("/findAll")
    public ResponseEntity<List<Product>> findAll() throws Exception {
        ResponseEntity<List<Product>> response;
        List<Product> productList = null;
        productList = productRepository.findAll();
        if (productList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }


    /**
     * GET findById/{id} the id of product
     * @param id
     * @return product with status OK if it found, else return NOT_FOUND
     * @throws Exception
     */
    @GetMapping(value = "/findById/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Could not found product: %s", id)));
        return new ResponseEntity<>(product, HttpStatus.OK);

    }

    /**
     * POST /new create new product
     * @param product
     * @return
     * @throws Exception
     */
    @PostMapping("/new")
    public ResponseEntity<Product> newProduct(@RequestBody Product product) throws Exception {
        Product savedProduct = productRepository.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.OK);
    }

    /**
     * POST /updateById/{id} find the product with id, then update it. throw bad request if product now found
     * @param product
     * @param id
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/updateById/{id}")
    public ResponseEntity<Product> updateProduct(@Valid @RequestBody Product product, @PathVariable Long id) throws Exception {
        ResponseEntity<Product> response;
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not exist"));

        product.setId(id);
        existingProduct.setProductId(product.getProductId());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setColor(product.getColor());
        existingProduct.setTitle(product.getTitle());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());

        Product savedProduct = productRepository.save(existingProduct);
        response = new ResponseEntity<>(savedProduct, HttpStatus.OK);

        return response;
    }

    /**
     * Delete /deleteById/{id} delete product with id
     * @param id
     * @return
     * @throws Exception
     */
    @DeleteMapping(value = "/deleteById/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) throws Exception {
        productRepository.deleteById(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}