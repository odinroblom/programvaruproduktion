package backend.domain;

import backend.utils.DateHandler;
import com.google.common.base.Objects;

import javax.persistence.*;
import java.util.Date;

/**
 * Creates a database table for storing sales by barcode, amount, customer number and date.
 *  An instance of this object also represents an entry in this database table.
 */
@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column( name = "barcode", nullable = false )
    private String barcode;

    @Column( name = "amount", nullable = false )
    private int amount;

    @Column( name = "customerNo", nullable = true )
    private String customerNo;

    @Column( name = "date", nullable = false )
    @Temporal(TemporalType.DATE)
    private Date date;

    public Sale() {
        super();
    }

    public Sale(String barcode, int amount, String customerNo, Date date ) {
        this.barcode = barcode;
        this.amount = amount;
        this.customerNo = customerNo;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sale that = (Sale) o;

        return Objects.equal(this.barcode, that.barcode) &&
                Objects.equal(this.amount, that.amount) &&
                Objects.equal(this.customerNo, that.customerNo) &&
                Objects.equal( DateHandler.format( this.date ), DateHandler.format( that.date ) );
    }

    @Override
    public int hashCode() {
        int hash = Objects.hashCode(barcode, amount, customerNo);
        return hash;
    }

    public static Sale of(String barcode, int amount, String customerNo, Date date ) {
        return new Sale( barcode, amount, customerNo, date );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date ) {
        this.date = date;
    }
}
