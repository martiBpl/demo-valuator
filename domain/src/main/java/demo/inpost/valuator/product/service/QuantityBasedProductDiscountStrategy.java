package demo.inpost.valuator.product.service;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@JsonTypeName
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class QuantityBasedProductDiscountStrategy implements ProductDiscountStrategy {
    private Map<BigDecimal, BigDecimal> quantityToDiscountPercentageThresholds;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Override
    public boolean supports(BigDecimal quantity) {
        return quantityToDiscountPercentageThresholds.keySet()
                .stream()
                .anyMatch(quantityThreshold -> quantityThreshold.compareTo(quantity) <= 0);
    }

    @Override
    public BigDecimal getItemDiscount(BigDecimal originalPrice, BigDecimal quantity) {
        BigDecimal percentage = quantityToDiscountPercentageThresholds.entrySet()
                .stream()
                .filter(entry -> entry.getKey().compareTo(quantity) <= 0)
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse(BigDecimal.ZERO);
        return originalPrice.multiply(percentage).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }
}
