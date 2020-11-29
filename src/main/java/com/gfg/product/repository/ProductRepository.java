package com.gfg.product.repository;

import com.gfg.product.model.Product;
import com.gfg.product.model.QProduct;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

public interface ProductRepository extends JpaRepository<Product, Long>,
                                            QuerydslPredicateExecutor<Product>,
                                            QuerydslBinderCustomizer<QProduct> {
    @Override
    default void customize(QuerydslBindings querydslBindings, QProduct qProduct) {
        querydslBindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }
}
