package demo.inpost.valuator.product.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductPriceDto(
        BigDecimal itemPriceAfterDiscount,
        BigDecimal originalItemPrice,
        BigDecimal itemDiscount,
        BigDecimal totalPriceAfterDiscount,
        BigDecimal originalTotalPrice,
        BigDecimal totalDiscount
) {
}
