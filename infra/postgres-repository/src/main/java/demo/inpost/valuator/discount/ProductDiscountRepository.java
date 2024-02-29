package demo.inpost.valuator.discount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscountDbo, UUID> {
    List<ProductDiscountDbo> findByProductIdAndActiveToAfter(UUID productId, Instant timestamp);
}
