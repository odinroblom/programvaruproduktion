package backend.services;

import backend.domain.ProductPriceColumn;

import java.util.Map;

// Communicates with the database table "product_pricing" through ProductPriceRepository.
public interface ProductPriceService {

    Map<String, Double> getAllPrices();

    Double getPriceFor(String barcode );

    void saveAll(Iterable<ProductPriceColumn> productPrices );

    void save(ProductPriceColumn productPrice );

    void remove( ProductPriceColumn productPrice );

    void removeAll( Iterable<ProductPriceColumn> productPrices );
}
