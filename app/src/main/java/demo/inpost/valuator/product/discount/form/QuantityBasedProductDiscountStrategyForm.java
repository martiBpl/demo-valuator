package demo.inpost.valuator.product.discount.form;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName
public final class QuantityBasedProductDiscountStrategyForm implements ProductDiscountStrategyForm {

    // todo @mb add validation
    private Map<BigDecimal, BigDecimal> thresholds;
}
