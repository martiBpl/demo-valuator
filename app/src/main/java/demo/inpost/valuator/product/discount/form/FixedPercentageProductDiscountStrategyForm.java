package demo.inpost.valuator.product.discount.form;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName
public final class FixedPercentageProductDiscountStrategyForm implements ProductDiscountStrategyForm {

    @DecimalMin(value = "0", inclusive = false, message = "Percentage must greater than 0")
    @DecimalMax(value = "100", inclusive = false, message = "Percentage must lower than 100")
    private BigDecimal percentage;
}
