package demo.inpost.valuator.product.controller;

import demo.inpost.valuator.product.dto.ProductPriceDto;
import demo.inpost.valuator.product.service.ProductPriceService;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductPriceService productPriceService;

    ProductController(ProductPriceService productPriceService) {
        this.productPriceService = productPriceService;
    }

    @GetMapping("/{id}/price")
    public ResponseEntity<ProductPriceDto> getProductPrice(
            @org.hibernate.validator.constraints.UUID(message = "Invalid id parameter") @PathVariable String id,
            @DecimalMin(value = "0.0", inclusive = false, message = "Invalid quantity parameter") @RequestParam String quantity) {
        BigDecimal q = new BigDecimal(quantity);
        UUID productId = UUID.fromString(id);

        var dto = this.productPriceService.getProductPrice(productId, q);
        return ResponseEntity.ok(dto);
    }
}
