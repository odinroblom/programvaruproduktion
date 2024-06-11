package backend.domain;

import backend.utils.DecimalHandler;
import com.google.common.base.Objects;

/**
 * Class representing a ScannedItem object.
 * It wraps a product with its quantity and price.
 * Every ScannedItem instance is added to the Sale instance in the shopping cart view, that the Cashier and Customer sees.
 */
public class ScannedItem {

    private final Product boundProduct;
    private final Integer itemQuantity;

    // This is not an instance of ProductPrice, since not all products have a corresponding ProductPrice mapping.
    private final Double itemPrice;
    private final String formattedPrice;

    private ScannedItem( Product boundProduct, Integer quantity, Double price ) {
        this.boundProduct = boundProduct;
        this.itemQuantity = quantity;
        this.itemPrice = price;
        formattedPrice = ( price != null ) ? DecimalHandler.format( price ) : "0";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScannedItem that = (ScannedItem) o;
        return Objects.equal(boundProduct, that.boundProduct) && Objects.equal(itemQuantity, that.itemQuantity) && Objects.equal(itemPrice, that.itemPrice) && Objects.equal(formattedPrice, that.formattedPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(boundProduct, itemQuantity, itemPrice, formattedPrice);
    }

    public static ScannedItem of(Product boundProduct, Integer quantity, Double price ) {
        return new ScannedItem( boundProduct, quantity, price );
    }

    public String getBarcode() {
        return boundProduct.getBarCode();
    }

    public double getVatPercentage() {
        return boundProduct.getVat();
    }

    public String getItemName() {
        return boundProduct.getName();
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }
}
