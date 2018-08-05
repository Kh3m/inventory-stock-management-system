package inventory_management.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Kh3m on 7/12/2018.
 */
public class Top5SalesModel {

    IntegerProperty serialNumber;
    IntegerProperty quantity;
    StringProperty productName;

    public Top5SalesModel(int serialNumber, int quantity, String productName) {
        this.serialNumber = new SimpleIntegerProperty(serialNumber);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.productName = new SimpleStringProperty(productName);
    }

    public int getSerialNumber() {
        return serialNumber.get();
    }

    public IntegerProperty serialNumberProperty() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber.set(serialNumber);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public String getProductName() {
        return productName.get();
    }

    public StringProperty productNameProperty() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }
}
