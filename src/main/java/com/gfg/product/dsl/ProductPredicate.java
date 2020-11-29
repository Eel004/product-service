package com.gfg.product.dsl;

import com.gfg.product.model.Product;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import org.apache.commons.lang3.StringUtils;

public class ProductPredicate {

    private SearchCriteria criteria;

    public ProductPredicate(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public BooleanExpression getPredicate() {

        PathBuilder<Product> entityPath = new PathBuilder<>(Product.class, "product");

        if (StringUtils.isNumeric(criteria.getValue().toString())) {
            NumberPath<Double> path = entityPath.getNumber(criteria.getKey(), Double.class);
            Double value = Double.parseDouble(String.valueOf(criteria.getValue()));
            switch (criteria.getOperation()) {
                case ":":
                    return path.eq(value);
                case ">":
                    return path.goe(value);
                case "<":
                    return path.loe(value);
                default:
            }
        } else {
            StringPath stringPath = entityPath.getString(criteria.getKey());
            if (criteria.getOperation().equals(":")) {
                return stringPath.containsIgnoreCase(String.valueOf(criteria.getValue()));
            }
        }

        return null;
    }
}
