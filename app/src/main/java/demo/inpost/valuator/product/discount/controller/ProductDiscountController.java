package demo.inpost.valuator.product.discount.controller;

import demo.inpost.valuator.product.discount.form.AddProductDiscountForm;
import demo.inpost.valuator.product.service.discount.ProductDiscountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static demo.inpost.valuator.product.discount.mapper.ProductDiscountStrategyFormMapper.mapToDomain;

@RestController
@RequestMapping("/product")
public class ProductDiscountController {
    private final ProductDiscountService productDiscountService;

    public ProductDiscountController(ProductDiscountService productDiscountService) {
        this.productDiscountService = productDiscountService;
    }

    @PostMapping("/{id}/discount")
    public ResponseEntity<Void> addProductDiscount(
            @org.hibernate.validator.constraints.UUID(message = "Invalid id parameter") @PathVariable String id,
            @Valid @RequestBody AddProductDiscountForm form) {
        productDiscountService.addProductDiscount(UUID.fromString(id), form.getActiveTo(), mapToDomain(form.getStrategy()));
        return ResponseEntity.accepted().build();
    }
}
