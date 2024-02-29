package demo.inpost.valuator.product.service;

import demo.inpost.valuator.product.dao.ProductDao;
import demo.inpost.valuator.product.dao.ProductDiscountDao;
import demo.inpost.valuator.product.dto.ProductPriceDto;
import demo.inpost.valuator.product.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductPriceService {
    private final ProductDao productDao;
    private final ProductDiscountResolver productDiscountResolver;
    private final ProductDiscountDao productDiscountDao;

    public ProductPriceService(
            ProductDao productDao,
            ProductDiscountResolver productDiscountResolver,
            ProductDiscountDao productDiscountDao) {
        this.productDao = productDao;
        this.productDiscountResolver = productDiscountResolver;
        this.productDiscountDao = productDiscountDao;
    }

    public ProductPriceDto getProductPrice(UUID productId, BigDecimal quantity) throws ProductNotFoundException {
        BigDecimal originalPrice = this.productDao.getProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId))
                .getPrice();

        List<BigDecimal> discounts = getPossibleDiscounts(productId, originalPrice, quantity);
        BigDecimal resolvedDiscount = this.productDiscountResolver.resolveProductPriceDiscount(originalPrice, discounts);

        return buildDto(originalPrice, resolvedDiscount, quantity);
    }

    private List<BigDecimal> getPossibleDiscounts(UUID productId, BigDecimal originalPrice, BigDecimal quantity) {
        return productDiscountDao.getActiveProductDiscounts(productId)
                .stream()
                .filter(strategy -> strategy.supports(quantity))
                .map(strategy -> strategy.getItemDiscount(originalPrice, quantity))
                .collect(Collectors.toList());
    }

    private ProductPriceDto buildDto(BigDecimal originalPrice, BigDecimal resolvedDiscount, BigDecimal quantity) {
        BigDecimal itemPriceAfterDiscount = originalPrice.subtract(resolvedDiscount);
        BigDecimal originalTotalPrice = originalPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPriceAfterDiscount = itemPriceAfterDiscount.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDiff = originalTotalPrice.subtract(totalPriceAfterDiscount);

        return new ProductPriceDto(
                itemPriceAfterDiscount,
                originalPrice,
                resolvedDiscount,
                totalPriceAfterDiscount,
                originalTotalPrice,
                totalDiff
        );
    }

}
