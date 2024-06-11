package backend.services;

import backend.domain.Sale;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// Communicates with the database table "sales" through SaleRepository.
public interface SaleService {

    List<Sale> getAll();

    List<Sale> getAll( String barcode );

    Optional<Sale> get( Integer id );

    void save( Sale sale );

    void saveAll( Iterable<Sale> sales );

    void remove( Sale sale );

    void removeAll( Iterable<Sale> sales );

    List<Sale> getByDate( Date date );

    List<Sale> getByDate( String date );
}
