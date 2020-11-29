package com.gfg.product.assembler;

import com.gfg.product.hateos.ProductModel;
import com.gfg.product.model.Product;
import com.gfg.product.web.ProductController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class ProductRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Product, ProductModel> {

    public ProductRepresentationModelAssembler() {
        super(ProductController.class, ProductModel.class);
    }

    @Override
    public ProductModel toModel(Product entity) {
        Objects.requireNonNull(entity);
        ProductModel representation = productModelConverter(entity);
        try {
            representation.add(linkTo(methodOn(ProductController.class).findById(entity.getId())).withSelfRel());
            representation.add(linkTo(methodOn(ProductController.class).newProduct(entity)).withRel("CREATE"));
            representation.add(linkTo(methodOn(ProductController.class).updateProduct(entity, entity.getId())).withRel("UPDATE"));
            representation.add(linkTo(methodOn(ProductController.class).deleteProduct(entity.getId())).withRel("DELETE"));
        } catch (Exception e) {
            log.error("Error while convert to model {}", e.getMessage());
        }
        return representation;
    }

    private ProductModel productModelConverter(Product product) {
        return ProductModel.builder()
                .id(product.getId())
                .productId(product.getProductId())
                .title(product.getTitle())
                .description(product.getDescription())
                .color(product.getColor())
                .brand(product.getBrand())
                .price(product.getPrice()).build();
    }
}
