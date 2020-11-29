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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ProductServiceApplication.class)
public class ProductControllerTest {

    private static final String PRODUCT_ID_1 = "GAS12345699";
    private static final String PRODUCT_ID_2 = "GAS1234569_22222";
    private static final String TITLE = "Jeans";
    private static final String DESCRIPTION = "Slim fit jeans";
    private static final String BRAND = "GAS";
    private static final BigDecimal PRICE_1 = new BigDecimal("90.0");
    private static final BigDecimal PRICE_2 = new BigDecimal("100.0");
    private static final String COLOR = "Blue";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ProductRepository productRepository;

    private MockMvc mockMvc;

    private Product testData;

    private Product testData2;

    private void createTestEntity() {
        testData = Product.builder()
                .productId(PRODUCT_ID_1)
                .title(TITLE)
                .description(DESCRIPTION)
                .brand(BRAND)
                .color(COLOR)
                .price(PRICE_1)
                .build();
        testData2 = Product.builder()
                .productId(PRODUCT_ID_2)
                .title(TITLE)
                .description(DESCRIPTION)
                .brand(BRAND)
                .color(COLOR)
                .price(PRICE_2)
                .build();
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        productRepository.deleteAll();
        createTestEntity();
    }

    @Test
    public void findAllProduct_shouldReturnAllProductAndResponseOk() throws Exception {
        Product save = productRepository.save(testData);
        mockMvc.perform(get("/products/findAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.products.[*].productId").value(hasItem(PRODUCT_ID_1)))
                .andExpect(jsonPath("$._embedded.products.[*].title").value(hasItem(TITLE)))
                .andExpect(jsonPath("$._embedded.products.[*].description").value(hasItem(DESCRIPTION)))
                .andExpect(jsonPath("$._embedded.products.[*].brand").value(hasItem(BRAND)))
                .andExpect(jsonPath("$._embedded.products.[*].color").value(hasItem(COLOR)))
                .andExpect(jsonPath("$._embedded.products.[*].price").value(hasItem(PRICE_1.doubleValue())));
    }

    @Test
    public void findAllProduct_withSearchPredicate_shouldReturnProductAndResponseOK() throws Exception {
        Product save = productRepository.save(testData);
        mockMvc.perform(get("/products/findAll?search=product:", PRODUCT_ID_1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.products.[*].productId").value(hasItem(PRODUCT_ID_1)))
                .andExpect(jsonPath("$._embedded.products.[*].title").value(hasItem(TITLE)))
                .andExpect(jsonPath("$._embedded.products.[*].description").value(hasItem(DESCRIPTION)))
                .andExpect(jsonPath("$._embedded.products.[*].brand").value(hasItem(BRAND)))
                .andExpect(jsonPath("$._embedded.products.[*].color").value(hasItem(COLOR)))
                .andExpect(jsonPath("$._embedded.products.[*].price").value(hasItem(PRICE_1.doubleValue())));
    }

    @Test
    public void findAllProduct_withPriceGreaterOrEqualThan_shouldReturnCorrectProductAndResponseOK() throws Exception {
        // save 2 products to verify data correctly
        Product save = productRepository.save(testData);
        Product save2 = productRepository.save(testData2);
        mockMvc.perform(get("/products/findAll?search=price>=", PRICE_2))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.products.[*].productId").value(hasItem(PRODUCT_ID_2)))
                .andExpect(jsonPath("$._embedded.products.[*].title").value(hasItem(TITLE)))
                .andExpect(jsonPath("$._embedded.products.[*].description").value(hasItem(DESCRIPTION)))
                .andExpect(jsonPath("$._embedded.products.[*].brand").value(hasItem(BRAND)))
                .andExpect(jsonPath("$._embedded.products.[*].color").value(hasItem(COLOR)))
                .andExpect(jsonPath("$._embedded.products.[*].price").value(hasItem(PRICE_2.doubleValue())));
    }

    @Test
    public void findAllProduct_withSortDescByPrice_shouldReturnCorrectProductAndResponseOK() throws Exception {
        // save 2 products to verify data correctly
        // price 1 = 90
        // price 2 = 100
        // => should display in order: [testData2 , testData]
        Product save = productRepository.save(testData);
        Product save2 = productRepository.save(testData2);
        mockMvc.perform(get("/products/findAll?sort=price,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.products.[0].productId").value(PRODUCT_ID_2));
    }

    @Test
    public void findById_shouldResponseOkAndProductFound() throws Exception {
        Product saved = productRepository.save(testData);
        mockMvc.perform(get("/products/findById/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID_1))
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.brand").value(BRAND))
                .andExpect(jsonPath("$.color").value(COLOR))
                .andExpect(jsonPath("$.price").value(PRICE_1.doubleValue()));
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
