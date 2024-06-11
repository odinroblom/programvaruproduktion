package backend.services;

import backend.domain.ProductPriceColumn;
import backend.repositories.ProductPriceRepository;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

// Implementation of ProductPriceService.
@Service
public class ProductPriceImpl implements ProductPriceService {

    private final ProductPriceRepository productPriceRepository;

    @Autowired
    public ProductPriceImpl( ProductPriceRepository productPriceRepository ) {
        this.productPriceRepository = productPriceRepository;
    }

    @Override
    public Map<String, Double> getAllPrices() {
        ImmutableMap.Builder<String, Double> builder = ImmutableMap.builder();
        productPriceRepository.findAll().forEach( productPrice -> {
            builder.put( productPrice.getBarcode(), productPrice.getPrice() );
        });
        return builder.build();
    }

    @Override
    public Double getPriceFor( String barcode ) {
        Optional<ProductPriceColumn> productPrice = productPriceRepository.findById( barcode );
        return productPrice.isPresent() ? productPrice.get().getPrice() : null;
    }

    @Override
    public void saveAll( Iterable<ProductPriceColumn> productPrices ) {
        productPriceRepository.saveAll( productPrices );
    }

    @Override
    public void save( ProductPriceColumn productPrice ) {
        productPriceRepository.save( productPrice );
    }

    @Override
    public void remove(ProductPriceColumn productPrice) {
        productPriceRepository.delete( productPrice );
    }

    @Override
    public void removeAll(Iterable<ProductPriceColumn> productPrices) {
        productPriceRepository.deleteAll( productPrices );
    }
}
