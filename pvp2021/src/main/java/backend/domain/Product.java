package backend.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

//Class representing a Product that is retrieved from the ProductCatalog.
public class Product {
    private final int id;
    private final String barCode;
    private final double vat;
    private final String name;
    private final String keyword;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Product( @JsonProperty( "@id" ) int id,
                    @JsonProperty( "barCode" ) String barCode,
                    @JsonProperty( "vat" ) double vat,
                    @JsonProperty( "name" ) String name,
                    @JsonProperty( "keyword" ) String keyword ) {
        this.id = id;
        this.barCode = barCode;
        this.vat = vat;
        this.name = name;
        this.keyword = keyword;
    }

    public static Product of( int id, String barCode, double vat, String name, String keyword ) {
        return new Product( id, barCode, vat, name, keyword );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id
                && Double.compare(product.vat, vat) == 0
                && Objects.equal(barCode, product.barCode)
                && Objects.equal(name, product.name)
                && Objects.equal(keyword, product.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, barCode, vat, name, keyword);
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getId() {
        return id;
    }

    public String getBarCode() {
        return barCode;
    }

    public double getVat() {
        return vat;
    }

    public String getName() {
        return name;
    }

    public String getKeyword() {
        return keyword;
    }
}
