package inventory_management.controllers;

import inventory_management.data.InventoryContract.SalesEntry;
import inventory_management.data.ItemDao;
import inventory_management.data.SalesDao;
import inventory_management.model.SalesModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Date;

/**
 * Created by Kh3m on 7/2/2018.
 */
public class SalesController {
    @FXML
    private DatePicker salesFrom;
    @FXML
    private DatePicker salesTo;
    @FXML
    private ChoiceBox<String> chbSalesSearchItemName;

    @FXML
    private TableView<SalesModel> tbvSales;
    @FXML
    private TableColumn<SalesModel, String> tcSalesItemName;
    @FXML
    private TableColumn<SalesModel, Integer> tcSalesQuantity;
    @FXML
    private TableColumn<SalesModel, Double> tcSalesRate;
    @FXML
    private TableColumn<SalesModel, String> tcSalesUnit;
    @FXML
    private TableColumn<SalesModel, String> tcSalesDate;

    @FXML
    private ChoiceBox<String> chbSalesItemName;
    @FXML
    private Spinner<Integer> spSalesQuantity;
    @FXML
    private ChoiceBox<String> chbSalesUnit;
    @FXML
    private TextField txtUnitPrice;

    private ItemDao itemDao;
    private SalesDao salesDao;
    private ObservableList<SalesModel> salesData;


    public void initialize () {
        setDefaults();

        validateSalesWhenChange();
    }

    private void setDefaults () {
        itemDao = new ItemDao();
        salesDao = new SalesDao();
        populateChoiceBox();
        populateSalesTable();
    }

    private void populateChoiceBox () {
        new Thread(() -> {
            chbSalesSearchItemName.getItems().addAll(itemDao.getItemNames());
            chbSalesItemName.getItems().addAll(itemDao.getItemNames());
        }, "ChoiceBoxPopulateThread").start();
    }

    private void populateSalesTable () {
        salesData = FXCollections.observableArrayList(salesDao.getAllSales());
        allowSalesTableEdit();
        tcSalesItemName.setCellValueFactory(cellEdit -> cellEdit.getValue().itemNameProperty());
        tcSalesQuantity.setCellValueFactory(cellEdit -> cellEdit.getValue().quantityProperty().asObject());
        tcSalesRate.setCellValueFactory(cellEdit -> cellEdit.getValue().rateProperty().asObject());
        tcSalesUnit.setCellValueFactory(cellEdit -> cellEdit.getValue().unitProperty());
        tcSalesDate.setCellValueFactory(cellEdit -> cellEdit.getValue().dateProperty());

        new Thread(() -> {
            tbvSales.setItems(salesData);
        }).start();
    }

    private void allowSalesTableEdit () {
        tcSalesItemName.setCellFactory(ComboBoxTableCell.forTableColumn(itemDao.getItemNames()));
        tcSalesItemName.setOnEditCommit(event -> {
            int id = event.getRowValue().getId();
            salesDao.updateSales(id, SalesEntry.COLUMN_ITEM_NAME, event.getNewValue());
        });
        tcSalesQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        tcSalesQuantity.setOnEditCommit(event -> {
            int id = event.getRowValue().getId();
            salesDao.updateSales(id, SalesEntry.COLUMN_QUANTITY, event.getNewValue());
            salesDao.updateRate(id, event.getNewValue(), event.getRowValue().getItemName());
        });
        tcSalesUnit.setCellFactory(ChoiceBoxTableCell.forTableColumn(
                                    "Kilogram (kg)",
                                    "Plate/s",
                                    "Piece/s",
                                    "Can/s",
                                    "Bottle/s",
                                    "Litter/s",
                                    "Carton/s"
        ));
        tcSalesUnit.setOnEditCommit(event -> {
            int id = event.getRowValue().getId();
            salesDao.updateSales(id, SalesEntry.COLUMN_UNIT, event.getNewValue());
        });
    }

    @FXML
    private void salesSearchHandler () {

        String itemName = chbSalesSearchItemName.getValue();
        LocalDate from = salesFrom.getValue();
        LocalDate to = salesTo.getValue();

        if (itemName.equals(" - Select Item - ")) {
            alertSomething(Alert.AlertType.ERROR, "No Item Selected",
                    "Please select item to search for");
            return;
        }

        if (from == null && to != null) {
            alertSomething(Alert.AlertType.ERROR, "Missing Range ( From )",
                    "Please select a range to search for");
            return;
        }

        if (!(itemName.equals(" - Select Item - ") && from == null && to == null)) {
            tbvSales.setItems(salesDao.getItemsBySearchCriteria(itemName, from, to));
        }

        if (!(itemName.equals(" - Select Item - ") && from != null && to == null)) {
            tbvSales.setItems(salesDao.getItemsBySearchCriteria(itemName, from, to));
        }

        if (!(itemName.equals(" - Select Item - ") && from != null && to != null)) {
            tbvSales.setItems(salesDao.getItemsBySearchCriteria(itemName, from, to));
        }

    }

    @FXML
    private void addSalesHandler () {
        if (isSalesInputValid()) {
            String itemName = chbSalesItemName.getValue();
            int quantity = spSalesQuantity.getValue();
            String unit = chbSalesUnit.getValue();
            int unitPrice = Integer.valueOf(txtUnitPrice.getText().trim());
            
            int insertedID = salesDao.insertItem(itemName, quantity, unit, unitPrice);

            if (insertedID > 0) {
                displaySalesSummary(itemName, quantity, unit);
                salesData.add(salesDao.getInsertedSalesModelById(insertedID));
            }
        }
    }

    private void displaySalesSummary (String itemName, int quantity, String unit) {
        double unitPrice = Double.valueOf(txtUnitPrice.getText().trim());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        DialogPane dialogPane = new DialogPane();
        dialogPane.setHeaderText("Record Saved");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(16);
        gridPane.setVgap(16);
        gridPane.setPrefWidth(400);

        Text summary = new Text("Summary");
        GridPane.setConstraints(summary, 0,0, 2,1);

        Label labelItemName = new Label("Item Name: ");
        Text textItemName = new Text(itemName);
        GridPane.setConstraints(labelItemName, 1,1);
        GridPane.setConstraints(textItemName, 2,1);

        Label labelQuantity = new Label("Quantity: ");
        Text textQuantity  = new Text(String.valueOf(quantity));
        GridPane.setConstraints(labelQuantity, 1,2);
        GridPane.setConstraints(textQuantity, 2,2);

        Label labelUnit = new Label("Measurement Unit: ");
        Text textUnit  = new Text(unit);
        GridPane.setConstraints(labelUnit, 1,3);
        GridPane.setConstraints(textUnit, 2,3);

        Label labelUnitPrice = new Label("Unit Price: ");
        Text textUnitPrice  = new Text(String.valueOf(unitPrice));
        GridPane.setConstraints(labelUnitPrice, 1,4);
        GridPane.setConstraints(textUnitPrice, 2,4);

        Label labelTotalPrice = new Label("Total Price: ");
        Text textTotalPrice  = new Text(String.valueOf(unitPrice * quantity));
        GridPane.setConstraints(labelTotalPrice, 1,5);
        GridPane.setConstraints(textTotalPrice, 2,5);

        gridPane.getChildren().addAll(summary, labelItemName, textItemName, labelQuantity, textQuantity,
                labelUnit, textUnit, labelUnitPrice, textUnitPrice, labelTotalPrice, textTotalPrice);

        dialogPane.setContent(gridPane);

        alert.setDialogPane(dialogPane);
        alert.showAndWait();
    }

    @FXML
    private void deleteSalesHandler() {
        SalesModel salesModel = tbvSales.getSelectionModel().getSelectedItem();

        if (salesModel != null) {
            int id = salesModel.getId();
            salesDao.deleteItem(id);
            salesData.remove(salesModel);
        }

    }

    @FXML
    private void refreshSalesHandler() {
        populateSalesTable();
    }

    private void validateSalesWhenChange() {
        chbSalesItemName.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                validateIndividualInput("", chbSalesItemName);
            }
        });

        chbSalesUnit.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                validateIndividualInput("", chbSalesUnit);
            }
        });

        txtUnitPrice.setOnKeyReleased((event) ->   {
            validateIndividualInput("Double", (TextField) event.getSource());
        });

    }

    private boolean isSalesInputValid () {
        boolean isValid = true;

        if (chbSalesItemName.getValue().equals(" - Select Item - ")) {
            isValid = isValid && false;
            chbSalesItemName.setStyle(errorStyle());
        } else {
            isValid = isValid && true;
            chbSalesItemName.setStyle(successStyle());
        }

        if (spSalesQuantity.getValue() == 0) {
            isValid = isValid && false;
            spSalesQuantity.setStyle(errorStyle());
        } else {
            try {
                Integer.valueOf(spSalesQuantity.getValue());
                isValid = isValid &&  true;
                spSalesQuantity.setStyle(successStyle());
            } catch (NumberFormatException exception) {
                System.out.println(exception);
                spSalesQuantity.setStyle(errorStyle());
                isValid = isValid &&  false;
            }
        }

        if (txtUnitPrice.getText().trim().equals("")) {
            isValid = isValid && false;
            txtUnitPrice.setStyle(errorStyle());
        } else {
            try {
                Double.valueOf(txtUnitPrice.getText().trim());
                isValid = isValid && true;
                txtUnitPrice.setStyle(successStyle());
            } catch (NumberFormatException exception) {
                System.out.println(exception);
                txtUnitPrice.setStyle(errorStyle());
                isValid = isValid &&  false;
            }
        }

        if (chbSalesUnit.getValue().equals(" - Measurement Units - ")) {
            isValid = isValid && false;
            chbSalesUnit.setStyle(errorStyle());
        } else {
            isValid = isValid &&  true;
            chbSalesUnit.setStyle(successStyle());
        }

        return isValid;
    }


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

        if (node instanceof ChoiceBox) {
            ChoiceBox choiceBox = (ChoiceBox) node;
            if (choiceBox.getValue().equals(" - Select Item - ") || choiceBox.getValue().equals(" - Measurement Units - ")){
                choiceBox.setStyle(errorStyle());
                isValid = isValid && false;
            } else {
                choiceBox.setStyle(successStyle());
                isValid = isValid && true;
            }
        }

        if (node instanceof Spinner) {
            Spinner<Integer> spinner = null;
            try {
               spinner = (Spinner) node;
                if (spinner.getValue() == 0) {
                    isValid = isValid && false;
                    spinner.setStyle(errorStyle());
                } else {
                    isValid = isValid && true;
                    spinner.setStyle(successStyle());
                }
            } catch (NumberFormatException exception) {
                isValid = isValid && false;
                spinner.setStyle(errorStyle());
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

    private void alertSomething (Alert.AlertType alertType, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
