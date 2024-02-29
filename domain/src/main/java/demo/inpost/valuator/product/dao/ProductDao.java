package demo.inpost.valuator.product.dao;


import demo.inpost.valuator.product.entity.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductDao {
    Optional<Product> getProduct(UUID productId);
}
