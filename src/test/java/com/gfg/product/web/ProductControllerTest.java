package com.gfg.product.web;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gfg.product.ProductServiceApplication;
import com.gfg.product.model.Product;
import com.gfg.product.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ProductServiceApplication.class)
public class ProductControllerTest {

    private static final String PRODUCT_ID = "GAS12345699";
    private static final String TITLE = "Jeans";
    private static final String DESCRIPTION = "Slim fit jeans";
    private static final String BRAND = "GAS";
    private static final BigDecimal PRICE = new BigDecimal("100.0");
    private static final String COLOR = "Blue";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ProductRepository productRepository;

    private MockMvc mockMvc;

    private Product testData;

    private Product createTestEntity() {
        return Product.builder()
                .productId(PRODUCT_ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .brand(BRAND)
                .color(COLOR)
                .price(PRICE)
                .build();
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        productRepository.deleteAll();
        testData = createTestEntity();
    }

    @Test
    public void findAllProduct_shouldReturnAllProductAndResponseOk() throws Exception {
        Product save = productRepository.save(testData);
        mockMvc.perform(get("/products/findAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].productId").value(hasItem(PRODUCT_ID)))
                .andExpect(jsonPath("$.[*].title").value(hasItem(TITLE)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DESCRIPTION)))
                .andExpect(jsonPath("$.[*].brand").value(hasItem(BRAND)))
                .andExpect(jsonPath("$.[*].color").value(hasItem(COLOR)))
                .andExpect(jsonPath("$.[*].price").value(hasItem(PRICE.doubleValue())));
    }

    @Test
    public void findById_shouldResponseOkAndProductFound() throws Exception {
        Product saved = productRepository.save(testData);
        mockMvc.perform(get("/products/findById/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.brand").value(BRAND))
                .andExpect(jsonPath("$.color").value(COLOR))
                .andExpect(jsonPath("$.price").value(PRICE.doubleValue()));
    }

    @Test
    public void findById_shouldResponseNotFoundIfProductNotFound() throws Exception {
        Product saved = productRepository.save(testData);
        mockMvc.perform(get("/products/findById/{id}", saved.getId() + 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createProduct_shouldResponseOKAndDataIsSaved() throws Exception {
        int productSizeBeforeCreated = productRepository.findAll().size();
        mockMvc.perform(post("/products/new")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(convertObjectToJsonBytes(testData)))
                    .andExpect(status().isOk());
        List<Product> products = productRepository.findAll();
        Assertions.assertEquals(products.size(), productSizeBeforeCreated + 1);
    }

    @Test
    public void updateProductById_shouldResponseOkAndProductUpdated() throws Exception {
        String newTitle = "new title";
        Product saved = productRepository.save(testData);
        saved.setTitle(newTitle);
        mockMvc.perform(put("/products/updateById/{id}", saved.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(convertObjectToJsonBytes(saved)))
                        .andExpect(status().isOk());
        Product updatedProduct = productRepository.findById(saved.getId())
                                .orElse(null);
        Assertions.assertNotNull(updatedProduct);
        Assertions.assertEquals(newTitle, updatedProduct.getTitle());
    }

    @Test
    public void updateProductById_shouldResponseBadRequestIfProductNotExist() throws Exception {
        Product saved = productRepository.save(testData);
        mockMvc.perform(put("/products/updateById/{id}", saved.getId() + 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(convertObjectToJsonBytes(saved)))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteProductById_shouldResponseOkAndProductDeleted() throws Exception {
        Product saved = productRepository.save(testData);
        mockMvc.perform(delete("/products/deleteById/{id}", saved.getId()))
                .andExpect(status().isOk());
        Product deleted = productRepository.findById(saved.getId()).orElse(null);
        Assertions.assertNull(deleted);
    }

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert
     * @return the JSON byte array
     * @throws IOException
     */
    private static byte[] convertObjectToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        return mapper.writeValueAsBytes(object);
    }

}
