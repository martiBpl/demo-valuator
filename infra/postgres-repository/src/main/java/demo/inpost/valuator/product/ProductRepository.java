package demo.inpost.valuator.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductDbo, UUID> {
     Optional<ProductDbo> findByProductId(UUID productId);
}
