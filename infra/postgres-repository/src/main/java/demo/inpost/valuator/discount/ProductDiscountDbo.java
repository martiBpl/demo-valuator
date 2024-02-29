package demo.inpost.valuator.discount;

import demo.inpost.valuator.product.service.ProductDiscountStrategy;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "product_discount")
public class ProductDiscountDbo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID productId;
    @JdbcTypeCode(SqlTypes.JSON)
    private ProductDiscountStrategy strategy;
    private Instant activeTo;
}
