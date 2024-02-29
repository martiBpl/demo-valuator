package demo.inpost.valuator.product.discount.form;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class AddProductDiscountForm {

    @NotNull(message = "Missing body property: activeTo")
    private Instant activeTo;

    @Valid
    @NotNull(message = "Missing discount strategy")
    private ProductDiscountStrategyForm strategy;
}
