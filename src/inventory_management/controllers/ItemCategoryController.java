package inventory_management.controllers;

import inventory_management.data.CategoryDao;
import inventory_management.data.InventoryContract.ItemEntry;
import inventory_management.data.ItemDao;
import inventory_management.data.StockHisDao;
import inventory_management.model.CategoryModel;
import inventory_management.model.ItemModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;


import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.sql.Timestamp;
import java.util.Optional;


/**
 * Created by Kh3m on 7/1/2018
 */
public class ItemCategoryController {

    @FXML
    private TableView<CategoryModel> tbvCategory;
    @FXML
    private TableColumn<CategoryModel, String> tcCategory;
    @FXML
    private TableColumn<CategoryModel, String> tcDescription;

    @FXML
    private TableView<ItemModel> tbvItems;
    @FXML
    private TableColumn<ItemModel, String> tcItemName;
    @FXML
    private TableColumn<ItemModel, Integer> tcItemQuantity;
    @FXML
    private TableColumn<ItemModel, Double> tcItemRate;
    @FXML
    private TableColumn<ItemModel, Double> tcTCU;
    @FXML
    private TableColumn<ItemModel, String> tcItemUnit;
    @FXML
    private TableColumn<ItemModel, String> tcItemDate;

    @FXML
    private TitledPane tdpCategory;

    // fields for category
    @FXML
    private TextField tfCategoryName;
    @FXML
    private TextField tfCategoryDescription;

    // fields for items
    @FXML
    private TextField tfItemName;
    @FXML
    private TextField tfItemQuantity;
    @FXML
    private TextField tfItemRate;
    @FXML
    private ChoiceBox<String> chbItemCategory;
    @FXML
    private ChoiceBox<String> chbItemUnit;

    @FXML
    private TitledPane tdpItem;

    private CategoryDao categoryDao;
    private ItemDao itemDao;
    private StockHisDao stockHisDao;
    private ObservableList<CategoryModel> categoryData;
    private ObservableList<ItemModel> itemData;

    public void initialize () {
        categoryDao = new CategoryDao();
        itemDao = new ItemDao();
        stockHisDao = new StockHisDao();
        tbvCategory.setEditable(true);
        tbvItems.setEditable(true);

        chbItemCategory.getItems().addAll(categoryDao.getCategoryNames());

        validateCategoryWhenKeyReleased();
        validateItemWhenKeyReleased();
        populateItemTable();
        allowItemEdit();
        populateCategoryTable();
        allowCategoryEdit();


        resetItemStyle();
        resetCategoryStyle();

    }

    private void resetCategoryStyle() {
        tdpCategory.expandedProperty().addListener(
            (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (!newValue) {
                    tfCategoryName.setStyle(successStyle());
                    tfCategoryDescription.setStyle(successStyle());
                }
        });
    }

    // Category Region
    private void populateCategoryTable () {
        categoryData = FXCollections.observableArrayList(categoryDao.getAllCategory());

        tcCategory.setCellValueFactory(
            cellData -> cellData.getValue().categoryProperty()
        );

        tcDescription.setCellValueFactory(
            cellData -> cellData.getValue().descriptionProperty()
        );

        tbvCategory.setItems( categoryData );
    }

    private void allowCategoryEdit () {
        tcCategory.setCellFactory(TextFieldTableCell.forTableColumn());
        tcCategory.setOnEditCommit(
            (TableColumn.CellEditEvent<CategoryModel, String> event) -> {
                int id = tbvCategory.getSelectionModel().getSelectedItem().getId();
                String category = event.getNewValue();
                String description = event.getTableView().getItems().get(event.getTablePosition().getRow()).getDescription();
                categoryDao.updateCategory(id, category, description);

        });

        tcDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDescription.setOnEditCommit(event -> {
            int id = tbvCategory.getSelectionModel().getSelectedItem().getId();
            String description = event.getNewValue();
            String category = event.getTableView().getItems().get(event.getTablePosition().getRow()).getCategory();
            categoryDao.updateCategory(id, category, description);
        });
    }

    @FXML
    private void addCategoryHandler() {

        if (isCategoryInputValid()) {
            String category = tfCategoryName.getText().toString();
            String description = tfCategoryDescription.getText().toString();
            int rowsAffected = categoryDao.insertCategory(category, description);

            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Record Added Successfully");
                alert.setContentText("Your Record Was Added Successfully With No Errors");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent()) {
                    populateCategoryTable();
                    tfCategoryName.clear();
                    tfCategoryDescription.clear();
                }

            }

        }

    }


    private void validateCategoryWhenKeyReleased () {
        tfCategoryName.setOnKeyReleased( event -> {
            validateIndividualInput("", (TextField) event.getSource());
        });

        tfCategoryDescription.setOnKeyReleased( event -> {
            validateIndividualInput("", (TextField) event.getSource());
        });
    }

    private boolean isCategoryInputValid () {
        boolean isValid = true;

        if (tfCategoryName.getText().trim().isEmpty()) {
            isValid = isValid && false;
            tfCategoryName.setStyle(errorStyle());
        } else {
            isValid = isValid && true;
            tfCategoryName.setStyle(successStyle());
        }

        if (tfCategoryDescription.getText().trim().isEmpty()) {
            isValid = isValid && false;
            tfCategoryDescription.setStyle(errorStyle());
        } else {
            isValid = isValid &&  true;
            tfCategoryDescription.setStyle(successStyle());
        }

        return isValid;
    }

    public void deleteCategoryHandler() {
        CategoryModel categoryModel = tbvCategory.getSelectionModel().getSelectedItem();
        if (categoryModel == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No Row Selected");
            alert.setContentText("Please select the row you wish to delete");
            alert.showAndWait();
            return;
        }
        categoryDao.deleteCategory(categoryModel.getId());
        categoryData.remove(categoryModel);
    }

    public void refreshCategoryHandler() {
        populateCategoryTable();
    }

    // Item Region
    private void populateItemTable () {
        itemData = FXCollections.observableArrayList(itemDao.getAllItems());

        tcItemName.setCellValueFactory(
                cellData -> cellData.getValue().itemNameProperty()
        );

        tcTCU.setCellValueFactory(
                cellData -> cellData.getValue().tcuProperty().asObject()
        );

        tcItemQuantity.setCellValueFactory(
                cellData -> cellData.getValue().quantityProperty().asObject()
        );

        tcItemRate.setCellValueFactory(
                cellData -> cellData.getValue().rateProperty().asObject()
        );

        tcItemUnit.setCellValueFactory(
                cellData -> cellData.getValue().unitProperty()
        );

        tcItemDate.setCellValueFactory(
                cellData -> cellData.getValue().dateProperty()
        );

        tbvItems.setItems( itemData );
    }


    public void addItemHandler() {

        if (isItemInputValid()) {
            String itemName = tfItemName.getText();
            double rate = Double.valueOf(tfItemRate.getText());
            int quantity = Integer.valueOf(tfItemQuantity.getText());
            String category = chbItemCategory.getValue();
            String unit = chbItemUnit.getValue();

            int cat_id = categoryDao.getCategoryIdByCategoryName(category);

            if (!(cat_id == -1)) {
                int rowsAffected = itemDao.insertItem(itemName, quantity, rate, unit, cat_id);

                if (rowsAffected == 1) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("Record Added Successfully");
                    alert.setContentText("Your Record Was Added Successfully With No Errors");
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent()) {
                        tfItemName.clear();
                        tfItemQuantity.clear();
                        tfItemRate.clear();
                        chbItemCategory.getSelectionModel().selectFirst();
                        chbItemUnit.getSelectionModel().selectFirst();
                    }
                } else {
                    // get stock id to insert into stock history table
                    int stockId = itemDao.getItemIdByItemName(tfItemName.getText());

                    DialogPane dialogPane = new DialogPane();
                    VBox vBox = new VBox();
                    Text txtAddToQuantity = new Text("Add " + quantity + " to quantity instead ? ");

                    dialogPane.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
                    dialogPane.setHeaderText("Item with name " + itemName + " already exists");

                    vBox.getChildren().addAll(txtAddToQuantity);
                    vBox.setSpacing(16);
                    dialogPane.setContent(vBox);
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setDialogPane(dialogPane);
                    dialog.setTitle("Increment Quantity");
                    dialog.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../../resources/icons/png/24x24/plus.png"))));
                    Optional<ButtonType> result = dialog.showAndWait();

                    if (result.isPresent()) {
                        if (dialog.getResult().equals(ButtonType.YES)) {
                            int rowsAffectced = stockHisDao.insertStockHis(stockId, quantity, rate);
                            itemDao.updateQuantity(stockId, quantity);
                            System.out.println("Rows Affected: " + rowsAffectced);
                            dialog.close();
                        }

                        if (dialog.getResult().equals(ButtonType.NO)) {
                            dialog.close();
                        }

                    }
                }

            }
        }
    }

    private void allowItemEdit () {

        tcItemName.setCellFactory(TextFieldTableCell.forTableColumn());
        tcItemName.setOnEditCommit(event -> {
            int id = event.getTableView().getItems().get(
                    event.getTablePosition().getRow()
            ).getId();

            if (event.getOldValue() != event.getNewValue())
                itemDao.updateItem(id, ItemEntry.COLUMN_ITEM_NAME, event.getNewValue());
        });


        tcItemRate.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        tcItemRate.setOnEditCommit(event -> {
            int id = event.getTableView().getItems().get(
                    event.getTablePosition().getRow()
            ).getId();

            int quantity = event.getTableView().getItems().get(
                    event.getTablePosition().getRow()
            ).getQuantity();

            // old value is what was already on the table
            double oldUnitCost = event.getOldValue();
            // new value is added value plus old value
            double newUnitCost = event.getNewValue();
            // added value is new value minus old value
            double addedUnitCost = newUnitCost - oldUnitCost;

            System.out.println("Old Unit Cost: " + event.getOldValue());
            System.out.println("New Unit Cost: " + event.getNewValue());
            System.out.println("Added Unit Cost: " + addedUnitCost);

            double tcu = quantity * newUnitCost;


            if (event.getOldValue() != event.getNewValue()) {
                itemDao.updateItem(id, ItemEntry.COLUMN_RATE, event.getNewValue());
                itemDao.updateItem(id, ItemEntry.COLUMN_TCU, tcu);
            }

        });

        tcItemQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        tcItemQuantity.setOnEditCommit(event -> {
            int id = event.getTableView().getItems().get(
                    event.getTablePosition().getRow()
            ).getId();

            int quantity = event.getNewValue();

            double unitCost = event.getTableView().getItems().get(
                    event.getTablePosition().getRow()
            ).getRate();

            double tcu = quantity * unitCost;
            // old value is what was already on the table
            int oldValue = event.getOldValue();
            // new value is added value plus old value
            int newValue = event.getNewValue();
            // added value is new value minus old value
            int addedValue = newValue - oldValue;

            if (oldValue != newValue) {
                System.out.println("Old Value: " + event.getOldValue());
                System.out.println("New Value: " + event.getNewValue());
                System.out.println("Added Value: " + addedValue);

                itemDao.updateItem(id, ItemEntry.COLUMN_TOTAL_QUANTITY, event.getNewValue());
                itemDao.updateItem(id, ItemEntry.COLUMN_TCU, tcu);
            }

            if (addedValue > 0) {
                stockHisDao.insertStockHis(id, addedValue, unitCost);
            }

        });

        tcItemUnit.setCellFactory(ChoiceBoxTableCell.forTableColumn(
                "Kilogram (kg)",
                "Plate/s",
                "Piece/s",
                "Can/s",
                "Bottle/s",
                "Litter/s",
                "Carton/s"
        ));
        tcItemUnit.setOnEditCommit(event -> {
            int id = event.getTableView().getItems().get(
                    event.getTablePosition().getRow()
            ).getId();

            if (event.getOldValue() != event.getNewValue())
                itemDao.updateItem(id, ItemEntry.COLUMN_UNIT, event.getNewValue());
        });
    }

    private boolean isItemInputValid () {
        boolean isValid = true;

        if (tfItemName.getText().trim().isEmpty()) {
            isValid = isValid && false;
            tfItemName.setStyle(errorStyle());
        } else {
            isValid = isValid && true;
            tfItemName.setStyle(successStyle());
        }

        if (tfItemQuantity.getText().trim().isEmpty()) {
            isValid = isValid && false;
            tfItemQuantity.setStyle(errorStyle());
        } else {
            try {
                Integer.valueOf(tfItemQuantity.getText());
                isValid = isValid &&  true;
                tfItemQuantity.setStyle(successStyle());
            } catch (NumberFormatException exception) {
                System.out.println(exception);
                tfItemQuantity.setStyle(errorStyle());
                isValid = isValid &&  false;
            }
        }


        if (tfItemRate.getText().trim().isEmpty()) {
            isValid = isValid && false;
            tfItemRate.setStyle(errorStyle());
        } else {
            try {
                Double.valueOf(tfItemRate.getText());
                isValid = isValid &&  true;
                tfItemRate.setStyle(successStyle());
            } catch (NumberFormatException exception) {
                System.out.println(exception);
                tfItemRate.setStyle(errorStyle());
                isValid = isValid &&  false;
            }
        }

        if (chbItemCategory.getValue().equals(" - Category - ")) {
            isValid = isValid && false;
            chbItemCategory.setStyle(errorStyle());
        } else {
            isValid = isValid &&  true;
            chbItemCategory.setStyle(successStyle());
        }

        if (chbItemUnit.getValue().equals(" - Measurement Units - ")) {
            isValid = isValid && false;
            chbItemUnit.setStyle(errorStyle());
        } else {
            isValid = isValid &&  true;
            chbItemUnit.setStyle(successStyle());
        }


        return isValid;
    }

    private void validateItemWhenKeyReleased() {
        tfItemName.setOnKeyReleased( event -> {
            validateIndividualInput("", (TextField) event.getSource());
        });

        tfItemQuantity.setOnKeyReleased( event -> {
            validateIndividualInput("Integer", (TextField) event.getSource());
        });

        tfItemRate.setOnKeyReleased( event -> {
            validateIndividualInput("Double", (TextField) event.getSource());
        });
    }


    private void resetItemStyle() {
        tdpItem.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (!newValue) {
                    tfItemName.setStyle(successStyle());
                    tfItemQuantity.setStyle(successStyle());
                    tfItemRate.setStyle(successStyle());
                    chbItemCategory.setStyle(successStyle());
                    chbItemUnit.setStyle(successStyle());
                }
            }
        });
    }

    @FXML
    private void refreshItemHandler() {
        populateItemTable();
    }

    @FXML
    private void deleteItemHandler() {
        ItemModel itemModel = tbvItems.getSelectionModel().getSelectedItem();
        if (itemModel == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No Row Selected");
            alert.setContentText("Please select the row you wish to delete");
            alert.showAndWait();
            return;
        }
        itemDao.deleteItem(itemModel.getId());
        itemData.remove(itemModel);
    }
    // globe
    private boolean validateIndividualInput (String formatType, Node node) {

        boolean isValid = false;

        if (node instanceof TextField) {
            TextField textField = (TextField) node;
            if (textField.getText().trim().isEmpty()){
                textField.setStyle(errorStyle());
                isValid = false;
            } else {
                textField.setStyle(successStyle());
                isValid = true;
            }

            if (!formatType.equals("")) {
                switch (formatType.trim().toLowerCase()) {
                    case "integer": {
                        try {
                            Integer.valueOf(textField.getText());
                            textField.setStyle(successStyle());
                            isValid = true;
                        } catch (NumberFormatException exception) {
                            textField.setStyle(errorStyle());
                            isValid = false;
                        }
                        break;
                    }

                    case "double": {
                        try {
                            Double.valueOf(textField.getText());
                            textField.setStyle(successStyle());
                            isValid = true;
                        } catch (NumberFormatException exception) {
                            textField.setStyle(errorStyle());
                            isValid = false;
                        }
                        break;
                    }
                }

            } else {
                isValid = true;
            }
        }

        return isValid;
    }

    private static String errorStyle () {
        String style = "-fx-base: #F04466";
        return style;
    }

    private static String successStyle () {
        String style = "";
        return style;
    }

}
