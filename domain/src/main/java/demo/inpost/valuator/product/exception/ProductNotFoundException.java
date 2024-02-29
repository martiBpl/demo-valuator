package demo.inpost.valuator.product.exception;

import java.util.UUID;

import static java.lang.String.format;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID productId) {
        super(format("Product %s not found.", productId));
    }
}
