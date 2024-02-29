package demo.inpost.valuator.product.service;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@JsonTypeName
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class FixedPercentageProductDiscountStrategy implements ProductDiscountStrategy {
    private BigDecimal fixedDiscountPercentage;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private BigDecimal getValidFixedDiscountPercentage(BigDecimal fixedDiscountPercentage) {
        boolean isPercentageGtZero = fixedDiscountPercentage.compareTo(BigDecimal.ZERO) > 0;
        boolean isPercentageLteHundred = fixedDiscountPercentage.compareTo(ONE_HUNDRED) <= 0;
        boolean isValidPercentage = isPercentageGtZero && isPercentageLteHundred;
        if (!isValidPercentage) {
            log.warn("FixedPercentageProductDiscountStrategy configured out of range: " + fixedDiscountPercentage);
            return BigDecimal.ZERO;
        }
        return fixedDiscountPercentage;
    }

    @Override
    public boolean supports(BigDecimal quantity) {
        return getValidFixedDiscountPercentage(fixedDiscountPercentage).compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public BigDecimal getItemDiscount(BigDecimal originalPrice, BigDecimal quantity) {
        return originalPrice
                .multiply(getValidFixedDiscountPercentage(fixedDiscountPercentage))
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }
}
