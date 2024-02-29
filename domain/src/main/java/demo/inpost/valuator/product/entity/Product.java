package demo.inpost.valuator.product.entity;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
public class Product {
    private UUID id;
    private UUID productId;
    private String name;
    private BigDecimal price;
}
