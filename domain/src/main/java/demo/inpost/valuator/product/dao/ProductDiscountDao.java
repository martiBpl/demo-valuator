package demo.inpost.valuator.product.dao;

import demo.inpost.valuator.product.service.ProductDiscountStrategy;

import java.util.List;
import java.util.UUID;

public interface ProductDiscountDao {
    List<ProductDiscountStrategy> getActiveProductDiscounts(UUID productId);
}
