package backend.repositories;

import backend.domain.ProductPriceColumn;
import org.springframework.data.repository.CrudRepository;

// Auto-generates CRUD queries for the database table product_pricing.
public interface ProductPriceRepository extends CrudRepository<ProductPriceColumn, String> {
}
