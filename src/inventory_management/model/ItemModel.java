package inventory_management.model;

import javafx.beans.property.*;

import java.sql.Timestamp;

/**
 * Created by Kh3m on 7/5/2018.
 */
public class ItemModel {

    IntegerProperty id;
    StringProperty itemName;
    IntegerProperty quantity;
    DoubleProperty rate;
    DoubleProperty tcu;
    StringProperty unit;
    StringProperty date;

    public ItemModel (int id, String itemName,
                    int quantity, double rate, double tcu, String unit, String date) {

        this.id = new SimpleIntegerProperty(id);
        this.itemName = new SimpleStringProperty(itemName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.rate = new SimpleDoubleProperty(rate);
        this.tcu = new SimpleDoubleProperty(tcu);
        this.unit = new SimpleStringProperty(unit);
        this.date = new SimpleStringProperty(date);

    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getItemName() {
        return itemName.get();
    }

    public StringProperty itemNameProperty() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName.set(itemName);
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

    public double getRate() {
        return rate.get();
    }

    public DoubleProperty rateProperty() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate.set(rate);
    }

    public double getTcTCU() {
        return tcu.get();
    }

    public DoubleProperty tcuProperty() {
        return tcu;
    }

    public void setTcTCU(double newSupply) {
        this.tcu.set(newSupply);
    }

    public String getUnit() {
        return unit.get();
    }

    public StringProperty unitProperty() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }
}
