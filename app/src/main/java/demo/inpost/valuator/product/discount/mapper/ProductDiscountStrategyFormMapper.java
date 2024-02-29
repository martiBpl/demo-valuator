package demo.inpost.valuator.product.discount.mapper;

import demo.inpost.valuator.product.discount.form.FixedPercentageProductDiscountStrategyForm;
import demo.inpost.valuator.product.discount.form.ProductDiscountStrategyForm;
import demo.inpost.valuator.product.discount.form.QuantityBasedProductDiscountStrategyForm;
import demo.inpost.valuator.product.service.FixedPercentageProductDiscountStrategy;
import demo.inpost.valuator.product.service.ProductDiscountStrategy;
import demo.inpost.valuator.product.service.QuantityBasedProductDiscountStrategy;

public class ProductDiscountStrategyFormMapper {
    public static ProductDiscountStrategy mapToDomain(ProductDiscountStrategyForm form) throws IllegalArgumentException {
        if (form instanceof FixedPercentageProductDiscountStrategyForm fixed) {
            return new FixedPercentageProductDiscountStrategy(fixed.getPercentage());
        }
        if (form instanceof QuantityBasedProductDiscountStrategyForm quantityBased) {
            return new QuantityBasedProductDiscountStrategy(quantityBased.getThresholds());
        }
        throw new IllegalArgumentException("Missing implementation for ProductDiscountStrategy");
    }
}
