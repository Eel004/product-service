package com.gfg.product.dsl.builder;

import com.gfg.product.dsl.SearchCriteria;
import com.gfg.product.dsl.ProductPredicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProductPredicateBuilder {

    private List<SearchCriteria> params;

    public ProductPredicateBuilder() {
        this.params = new ArrayList<>();
    }

    public ProductPredicateBuilder with(String key, String operation, Object value) {

        SearchCriteria param = new SearchCriteria();
        param.setKey(key);
        param.setOperation(operation);
        param.setValue(value);
        params.add(param);

        return this;
    }

    public BooleanExpression build() {
        if (params == null) {
            return null;
        }

        List<BooleanExpression> predicates = params.stream()
                .map(p -> {
                    ProductPredicate txnPredicate = new ProductPredicate(p);
                    return txnPredicate.getPredicate();
                }).filter(Objects::nonNull).collect(Collectors.toList());

        BooleanExpression result = Expressions.asBoolean(true).isTrue();
        for (BooleanExpression predicate : predicates) {
            result = result.and(predicate);
        }
        return result;
    }

}
