package com.gfg.product.web;

import com.gfg.product.assembler.ProductRepresentationModelAssembler;
import com.gfg.product.dsl.builder.ProductPredicateBuilder;
import com.gfg.product.exception.BadRequestException;
import com.gfg.product.hateos.ProductModel;
import com.gfg.product.model.Product;
import com.gfg.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/products")
public class ProductController {

    private ProductRepository productRepository;

    private final ProductRepresentationModelAssembler assembler;

    private final PagedResourcesAssembler<Product> pagedResourcesAssembler;

    public ProductController(ProductRepository productRepository,
                             ProductRepresentationModelAssembler assembler,
                             PagedResourcesAssembler<Product> pagedResourcesAssembler) {
        this.productRepository = productRepository;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    /**
     * GET findAll get all products
     * @param
     * @return product list in DB and status OK, return NO_CONTENT if product list empty
     * @throws Exception
     */
    @GetMapping("/findAll")
    public ResponseEntity<PagedModel<ProductModel>> findAll(@RequestParam(value = "search", required = false) String search,
                                                            Pageable pageable) throws Exception {
        ProductPredicateBuilder predicateBuilder = new ProductPredicateBuilder();
        if (search != null) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                predicateBuilder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        Page<Product> productPageable = productRepository.findAll(predicateBuilder.build(), pageable);
        PagedModel<ProductModel> pagedModel = pagedResourcesAssembler.toModel(productPageable, assembler);
        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }


    /**
     * GET findById/{id} the id of product
     * @param id
     * @return product with status OK if it found, else return NOT_FOUND
     * @throws Exception
     */
    @GetMapping(value = "/findById/{id}")
    public ResponseEntity<ProductModel> findById(@PathVariable Long id) throws Exception {
        return productRepository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

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