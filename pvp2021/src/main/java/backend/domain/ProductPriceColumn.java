package backend.domain;

import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Creates a database table, "product_pricing", for the mapping between product and price.
 * An instance of this object also represents an entry in this database table.
 **/
@Entity
@Table(name = "product_pricing")
public class ProductPriceColumn {

    @Id
    @Column( name = "barcode", nullable = false )
    private String barcode;

    @Column( name = "price", nullable = false )
    private double price;

    public ProductPriceColumn() {
        super();
    }

    private ProductPriceColumn(String barcode, double price ) {
        this.barcode = barcode;
        this.price = price;
    }

    public static ProductPriceColumn of(String barcode, double price ) {
        return new ProductPriceColumn( barcode, price );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductPriceColumn that = (ProductPriceColumn) o;
        return Double.compare(that.price, price) == 0 && Objects.equal(barcode, that.barcode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(barcode, price);
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
