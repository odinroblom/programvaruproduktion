package backend.domain;

import com.google.common.base.Objects;

//Class for mapping between a product and its price.
public class ProductPrice {
    private final Product product;
    private final Double price;

    private ProductPrice(Product product, Double price) {
        this.product = product;
        this.price = price;
    }

    public static ProductPrice of(Product product, Double price ) {
        return new ProductPrice( product, price );
    }

    @Override
    public String toString() {
        return product.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductPrice that = (ProductPrice) o;
        return Objects.equal(product, that.product) && Objects.equal(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(product, price);
    }

    public Product getProduct() {
        return product;
    }

    public Double getPrice() {
        return price;
    }
}
