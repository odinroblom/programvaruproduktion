package backend.services;

import backend.domain.Sale;
import backend.repositories.SaleRepository;
import backend.utils.DateHandler;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// Implementation of SaleService.
@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;

    @Autowired
    public SaleServiceImpl( SaleRepository saleRepository ) {

        this.saleRepository = saleRepository;
    }

    @Override
    public List<Sale> getAll() {
        ImmutableList.Builder<Sale> builder = ImmutableList.builder();
        builder.addAll( saleRepository.findAll() );
        return builder.build();
    }

    @Override
    public List<Sale> getAll( String barcode ) {
        return ImmutableList.copyOf( saleRepository.findByBarcode( barcode ) );
    }

    @Override
    public Optional<Sale> get( Integer id ) {
        return saleRepository.findById( id );
    }

    @Override
    public void save(Sale sale ) {
        saleRepository.save( sale );
    }

    @Override
    public void saveAll(Iterable<Sale> sales ) {
        saleRepository.saveAll( sales );
    }

    @Override
    public void remove(Sale sale) {
        saleRepository.delete( sale );
    }

    @Override
    public void removeAll(Iterable<Sale> sales) {
        saleRepository.deleteAll( sales );
    }

    @Override
    public List<Sale> getByDate( Date date ) {
        return saleRepository.findByDate( DateHandler.format( date ) );
    }

    @Override
    public List<Sale> getByDate( String date ) {
        return saleRepository.findByDate( date );
    }
}
