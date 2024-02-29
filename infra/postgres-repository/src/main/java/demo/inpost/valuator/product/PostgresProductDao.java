package demo.inpost.valuator.product;

import demo.inpost.valuator.product.dao.ProductDao;
import demo.inpost.valuator.product.entity.Product;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PostgresProductDao implements ProductDao {

    private final ProductRepository productRepository;

    public PostgresProductDao(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProduct(UUID productId) {
        return this.productRepository.findByProductId(productId).map(ProductDboMapper::mapToEntity);
    }

}
