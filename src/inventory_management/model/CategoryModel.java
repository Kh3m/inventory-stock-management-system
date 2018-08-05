package inventory_management.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Kh3m on 7/4/2018.
 */
public class CategoryModel {

    IntegerProperty id;
    StringProperty category;
    StringProperty description;

    public CategoryModel (int id, String category, String description) {
        this.id = new SimpleIntegerProperty(id);
        this.category = new SimpleStringProperty(category);
        this.description = new SimpleStringProperty(description);
    }

    public void setId (int id) {
        this.id.set(id);
    }
    public int getId () {
        return this.id.get();
    }
    public IntegerProperty idProperty () {
        return id;
    }
    public void setCategory(String category) {
        this.category.set(category);
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public void setDescription(String category) {
        this.description.set(category);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }
}
