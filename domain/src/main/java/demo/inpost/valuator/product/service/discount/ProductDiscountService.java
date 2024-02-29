package demo.inpost.valuator.product.service.discount;

import demo.inpost.valuator.product.dao.ProductDiscountDao;
import demo.inpost.valuator.product.service.ProductDiscountStrategy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ProductDiscountService {
    private final ProductDiscountDao productDiscountDao;

    public ProductDiscountService(ProductDiscountDao productDiscountDao) {
        this.productDiscountDao = productDiscountDao;
    }

    public void addProductDiscount(UUID productId, Instant activeTo, ProductDiscountStrategy strategy) {
        productDiscountDao.addProductDiscount(productId, activeTo, strategy);
    }
}
