package demo.inpost.valuator.product.service;

import java.math.BigDecimal;
import java.util.List;

public interface ProductDiscountResolver {
    BigDecimal resolveDiscount(List<BigDecimal> discounts);

    default BigDecimal resolveProductPriceDiscount(BigDecimal originalPrice, List<BigDecimal> discounts) {
        BigDecimal discount = resolveDiscount(discounts);
        boolean isDiscountGteZero = originalPrice.subtract(discount).compareTo(BigDecimal.ZERO) >= 0;
        return isDiscountGteZero ? discount : originalPrice;
    }
}
