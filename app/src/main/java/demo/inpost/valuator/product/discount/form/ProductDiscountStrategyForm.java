package demo.inpost.valuator.product.discount.form;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = QuantityBasedProductDiscountStrategyForm.class, name = "quantity-based"),
        @JsonSubTypes.Type(value = FixedPercentageProductDiscountStrategyForm.class, name = "fixed")
})
public sealed interface ProductDiscountStrategyForm permits FixedPercentageProductDiscountStrategyForm, QuantityBasedProductDiscountStrategyForm {
}
