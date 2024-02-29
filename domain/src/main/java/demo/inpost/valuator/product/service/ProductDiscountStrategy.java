package demo.inpost.valuator.product.service;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "strategy",
        defaultImpl = NoOpProductDiscountStrategy.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(NoOpProductDiscountStrategy.class),
        @JsonSubTypes.Type(QuantityBasedProductDiscountStrategy.class),
        @JsonSubTypes.Type(FixedPercentageProductDiscountStrategy.class)
})
public interface ProductDiscountStrategy extends Serializable {
    boolean supports(BigDecimal quantity);

    BigDecimal getItemDiscount(BigDecimal originalPrice, BigDecimal quantity);
}
