package demo.inpost.valuator.product.service;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.math.BigDecimal;

@JsonTypeName
public class NoOpProductDiscountStrategy implements ProductDiscountStrategy {
    @Override
    public boolean supports(BigDecimal quantity) {
        return false;
    }

    @Override
    public BigDecimal getItemDiscount(BigDecimal originalPrice, BigDecimal quantity) {
        return BigDecimal.ZERO;
    }
}
