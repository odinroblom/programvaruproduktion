package backend.repositories;

import backend.domain.Sale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

// Auto-generates CRUD queries for the database table "sales" and adds two customized queries as well.
public interface SaleRepository extends CrudRepository<Sale, Integer> {
    @Query(value = "SELECT * FROM sales WHERE date = ?1", nativeQuery = true)
    List<Sale> findByDate( String date );

    @Query(value = "SELECT * FROM sales WHERE barcode = ?1", nativeQuery = true)
    List<Sale> findByBarcode( String barcode );
}
