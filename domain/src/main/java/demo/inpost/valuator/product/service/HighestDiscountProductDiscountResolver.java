package demo.inpost.valuator.product.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Component
public class HighestDiscountProductDiscountResolver implements ProductDiscountResolver {
    @Override
    public BigDecimal resolveDiscount(List<BigDecimal> discounts) {
        return discounts.stream().max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
    }
}
