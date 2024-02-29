package demo.inpost.valuator.product;

import demo.inpost.valuator.product.entity.Product;

public class ProductDboMapper {

    static Product mapToEntity(ProductDbo dbo) {
        return Product.builder()
                .id(dbo.getId())
                .productId(dbo.getProductId())
                .name(dbo.getName())
                .price(dbo.getPrice())
                .build();
    }
}
