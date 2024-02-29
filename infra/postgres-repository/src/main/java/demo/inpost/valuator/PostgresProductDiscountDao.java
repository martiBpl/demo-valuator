package demo.inpost.valuator;

import demo.inpost.valuator.discount.ProductDiscountDbo;
import demo.inpost.valuator.discount.ProductDiscountRepository;
import demo.inpost.valuator.product.dao.ProductDiscountDao;
import demo.inpost.valuator.product.service.ProductDiscountStrategy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostgresProductDiscountDao implements ProductDiscountDao {
    private final ProductDiscountRepository productDiscountRepository;

    public PostgresProductDiscountDao(ProductDiscountRepository productDiscountRepository) {
        this.productDiscountRepository = productDiscountRepository;
    }

    public List<ProductDiscountStrategy> getActiveProductDiscounts(UUID productId) {
        return productDiscountRepository.findByProductIdAndActiveToAfter(productId, Instant.now())
                .stream()
                .map(ProductDiscountDbo::getStrategy)
                .collect(Collectors.toList());
    }

    public UUID addProductDiscount(UUID productId, Instant activeTo, ProductDiscountStrategy strategy) {
        var dbo = ProductDiscountDbo.builder()
                .productId(productId)
                .strategy(strategy)
                .activeTo(activeTo)
                .build();
        return productDiscountRepository.save(dbo).getId();
    }

}
