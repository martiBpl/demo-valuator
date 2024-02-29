package demo.inpost.valuator.product.dao;

import demo.inpost.valuator.product.service.ProductDiscountStrategy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ProductDiscountDao {
    List<ProductDiscountStrategy> getActiveProductDiscounts(UUID productId);
    UUID addProductDiscount(UUID productId, Instant activeTo, ProductDiscountStrategy strategy);
}
