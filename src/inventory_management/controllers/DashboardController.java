package inventory_management.controllers;

import inventory_management.data.*;
import inventory_management.factory.EventCellFactory;
import inventory_management.model.Top5SalesModel;
import inventory_management.utils.ChartUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.shape.*;

import java.io.File;
import java.io.IOException;
import inventory_management.Main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Kh3m on 7/1/2018
 */

public class DashboardController {
    @FXML
    private VBox dashboardRoot;

    @FXML
    private VBox vbUserFields;
    @FXML
    ImageView imgvUsers;
    @FXML
    Button btnUpload;
    @FXML
    Button btnAddUser;
    @FXML
    HBox hbUploadPlus;
    @FXML
    Label lblImage;
    @FXML
    Label lblErrorMsg;
    @FXML
    TextField tfUserName;
    @FXML
    ChoiceBox chbUserRole;

    @FXML
    VBox vbYearlySalesReport;
    @FXML
    ScrollPane spDash;
    @FXML
    TilePane tpSummary;
    @FXML
    VBox vbChartParent;
    @FXML
    GridPane gpInvSummary;

    @FXML
    private Text txtSalesOrder;
    @FXML
    private Text txtStockRegistry;
    @FXML
    private Text txtUserCount;
    @FXML
    private Text txtDailySales;
    @FXML
    private Text txtWeeklySales;
    @FXML
    private Text txtMonthlySales;
    @FXML
    private Text txtYearlySales;
    @FXML
    private Text txtTop5SalesMonth;
    @FXML
    private Text txtTop5SalesYear;

    @FXML
    private ListView<EventCellFactory> lvRecentEvents;

    @FXML
    private TableView<Top5SalesModel> tbvTopSalesMonth;
    @FXML
    private TableView<Top5SalesModel> tbvTopSalesYear;

    @FXML
    private TableColumn<Top5SalesModel, Integer> tcTop5SalesMonthSN;
    @FXML
    private TableColumn<Top5SalesModel, String> tcTop5SalesMonthPN;
    @FXML
    private TableColumn<Top5SalesModel, Integer> tcTop5SalesMonthQT;
    @FXML
    private TableColumn<Top5SalesModel, Integer> tcTop5SalesYearSN;
    @FXML
    private TableColumn<Top5SalesModel, String> tcTop5SalesYearPN;
    @FXML
    private TableColumn<Top5SalesModel, Integer> tcTop5SalesYearQT;


    private File file;
    private SalesDao salesDao;
    private ItemDao itemDao;
    private LocalDate now;
    private RoleDao roleDao;
    private UserDao userDao;
    private EventDao eventDao;


    public void initialize () {
        setDefaults();
    }
    @FXML
    private void ancSalesOrderHandler() {
        switchCenter("sales.fxml", 2);
    }

    @FXML
    private void ancStockRegistryHandler() {
        switchCenter("item_category.fxml", 1);
    }

    private void switchCenter (String resName, int selectIndex ) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../../resources/layouts/" + resName));
            Parent parent = loader.load();
            BorderPane mainRoot = ((BorderPane) dashboardRoot.getParent());
            ListView listView = (ListView) mainRoot.getLeft();
            listView.getSelectionModel().select(selectIndex);
            mainRoot.setCenter(parent);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    private int lbUserIconHandlerClickCount = 1;

    public void lbUserIconHandler() {
        if (++lbUserIconHandlerClickCount % 2 == 0) {
            vbUserFields.setVisible(true);
        } else {
            vbUserFields.setVisible(false);
            imgvUsers.setImage(new Image(getClass().getResourceAsStream("../../resources/icons/png/48x48/users.png")));
            lblImage.setStyle("-fx-opacity:0.5;");
            lblErrorMsg.setText("");
            tfUserName.clear();
            chbUserRole.getSelectionModel().selectFirst();
        }

    }

    private void setDefaults () {
        // initialize dao
        salesDao = new SalesDao();
        itemDao = new ItemDao();
        roleDao = new RoleDao();
        userDao = new UserDao();
        eventDao = new EventDao();
        now = LocalDate.now();

        VBox vBox = (VBox) spDash.getParent();
        tpSummary.prefWidthProperty().bind(vBox.widthProperty());
        gpInvSummary.prefWidthProperty().bind(vBox.widthProperty());
        vbChartParent.getChildren().add(0, createReportBarChart());
        GridPane.setHgrow(vbYearlySalesReport, Priority.ALWAYS);

        // display count summary
        txtSalesOrder.setText(String.valueOf(salesDao.getSalesCount()));
        txtStockRegistry.setText(String.valueOf(itemDao.getItemCount()));
        txtUserCount.setText(String.valueOf(userDao.getUserCount()));

        btnUpload.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            Stage stage = (new Main()).getPrimaryStage();
            file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                lblErrorMsg.setText("");
            } else {
                lblErrorMsg.setText("Please Select Image");
            }

            displayImage(file);
        });

        tfUserName.setOnKeyReleased((event) -> {
            validateIndividualInput("", (TextField)event.getSource());
        });

        btnAddUser.setOnAction( event -> {
            String userRole = chbUserRole.getSelectionModel().getSelectedItem().toString();
            String[] rnd = new String[]{"wd35&td32", "2sdaassrr", "#rsdd34345", "fd6rre$2", "gdfsd3@s"};
            String pass = rnd[(int)(Math.random() * 5.0D)];
            if (tfUserName.getText().equals("")) {
                tfUserName.setStyle(errorStyle());
            } else {
                tfUserName.setStyle(successStyle());
                if (!userRole.equalsIgnoreCase(" - User Role - ")) {
                    if (file != null) {
                        int id = roleDao.getRoleIdByRole(userRole);
                        System.out.println("Role id: " + id);
                        int rowsAffected = userDao.insertUser(id, tfUserName.getText(), file, pass);
                        if (rowsAffected == 1) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Generated Password: " + pass, new ButtonType[]{ButtonType.OK});
                            alert.setTitle("Password");
                            alert.setHeaderText("Your Login Password");
                            alert.show();
                            tfUserName.clear();
                            chbUserRole.getSelectionModel().selectFirst();
                        } else {
                            String msg = "Reason: Is either you pass in a username that already exists or the image you upload is to large \n\n\nHint: Ensure the username u entered does not already exists and your image size should not be greater than 4MB of data";
                            Alert alertx = new Alert(Alert.AlertType.INFORMATION, msg, new ButtonType[0]);
                            alertx.setTitle("Error");
                            alertx.setHeaderText("Something Went Wrong");
                            alertx.show();
                        }
                    } else {
                        lblErrorMsg.setText("Please Select Image");
                    }
                }

            }
        });

        // populate role choicebox
        populateRole();
        // Daily Sales
        displayDailySales();

        // Weekly Sales
        displayWeeklySales();

        // Monthly Sales
        displayMonthlySales();

        // Monthly Sales
        displayYearlySales();

        // Top 5 Monthly Sales
        displayTop5SalesMonth();

        // Top 5 Yearly Sales
        displayTop5SalesYear();

        lvRecentEvents.setCellFactory(new Callback<ListView<EventCellFactory>, ListCell<EventCellFactory>>() {
            @Override
            public ListCell<EventCellFactory> call(ListView<EventCellFactory> param) {
                ListCell listCell = new ListCell<EventCellFactory>() {

                    @Override
                    public void updateItem(EventCellFactory item, boolean empty) {
                        {
                            super.updateItem(item, empty);
                        }

                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setGraphic(createEventListPane(item));
                        }
                    }
                };

                return listCell;
            }
        });

        lvRecentEvents.setItems(eventDao.getEvents());
    }

    private void populateRole () {
        chbUserRole.getItems().addAll(roleDao.getRoles());
    }

    private BarChart<String, Number> createReportBarChart () {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        int year = now.getYear();

        BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
        barChart.setTitle("YEARLY SALES REPORT");

        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Revenue");
        revenueSeries.getData().addAll(FXCollections.observableArrayList(
            new XYChart.Data<>("Jan", salesDao.getMonthlyRevenue(year, 1)),
            new XYChart.Data<>("Feb", salesDao.getMonthlyRevenue(year, 2)),
            new XYChart.Data<>("Match", salesDao.getMonthlyRevenue(year, 3)),
            new XYChart.Data<>("Apr", salesDao.getMonthlyRevenue(year, 4)),
            new XYChart.Data<>("May", salesDao.getMonthlyRevenue(year, 5)),
            new XYChart.Data<>("June", salesDao.getMonthlyRevenue(year, 6)),
            new XYChart.Data<>("July", salesDao.getMonthlyRevenue(year, 7)),
            new XYChart.Data<>("Aug", salesDao.getMonthlyRevenue(year, 8)),
            new XYChart.Data<>("Sept", salesDao.getMonthlyRevenue(year, 9)),
            new XYChart.Data<>("Oct", salesDao.getMonthlyRevenue(year, 10)),
            new XYChart.Data<>("Nov", salesDao.getMonthlyRevenue(year, 11)),
            new XYChart.Data<>("Dec", salesDao.getMonthlyRevenue(year, 12))
        ));

        XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();
        profitSeries.setName("Profit");
        profitSeries.getData().addAll(FXCollections.observableArrayList(
                new XYChart.Data<>("Jan", ChartUtil.getMonthlyProfit(year, 1)),
                new XYChart.Data<>("Feb", ChartUtil.getMonthlyProfit(year, 2)),
                new XYChart.Data<>("Match", ChartUtil.getMonthlyProfit(year, 3)),
                new XYChart.Data<>("Apr", ChartUtil.getMonthlyProfit(year, 4)),
                new XYChart.Data<>("May", ChartUtil.getMonthlyProfit(year, 5)),
                new XYChart.Data<>("June", ChartUtil.getMonthlyProfit(year, 6)),
                new XYChart.Data<>("July", ChartUtil.getMonthlyProfit(year, 7)),
                new XYChart.Data<>("Aug", ChartUtil.getMonthlyProfit(year, 8)),
                new XYChart.Data<>("Sept", ChartUtil.getMonthlyProfit(year, 9)),
                new XYChart.Data<>("Oct", ChartUtil.getMonthlyProfit(year, 10)),
                new XYChart.Data<>("Nov", ChartUtil.getMonthlyProfit(year, 11)),
                new XYChart.Data<>("Dec", ChartUtil.getMonthlyProfit(year, 12))

        ));

        XYChart.Series<String, Number> purchaseSeries = new XYChart.Series<>();
        purchaseSeries.setName("Purchase");
        purchaseSeries.getData().addAll(FXCollections.observableArrayList(
                new XYChart.Data<>("Jan", itemDao.getMonthlyPurchase(year, 1)),
                new XYChart.Data<>("Feb", itemDao.getMonthlyPurchase(year, 2)),
                new XYChart.Data<>("Match", itemDao.getMonthlyPurchase(year, 3)),
                new XYChart.Data<>("Apr", itemDao.getMonthlyPurchase(year, 4)),
                new XYChart.Data<>("May", itemDao.getMonthlyPurchase(year, 5)),
                new XYChart.Data<>("June", itemDao.getMonthlyPurchase(year, 6)),
                new XYChart.Data<>("July", itemDao.getMonthlyPurchase(year, 7)),
                new XYChart.Data<>("Aug", itemDao.getMonthlyPurchase(year, 8)),
                new XYChart.Data<>("Sept", itemDao.getMonthlyPurchase(year, 9)),
                new XYChart.Data<>("Oct", itemDao.getMonthlyPurchase(year, 10)),
                new XYChart.Data<>("Nov", itemDao.getMonthlyPurchase(year, 11)),
                new XYChart.Data<>("Dec", itemDao.getMonthlyPurchase(year, 12))

        ));
        barChart.getData().addAll(revenueSeries, profitSeries, purchaseSeries);
        return barChart;
    }

    private GridPane createEventListPane (EventCellFactory eventCellFactory) {

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));

        ImageView userImage = new ImageView(eventCellFactory.getUserImage());
        userImage.setFitHeight(28);
        userImage.setFitWidth(28);
        Rectangle userImageClip = new Rectangle(userImage.getFitWidth(), userImage.getFitHeight());
        userImageClip.setArcWidth(userImage.getFitWidth());
        userImageClip.setArcHeight(userImage.getFitHeight());
        userImage.setClip(userImageClip);
        userImage.setPreserveRatio(true);
        GridPane.setMargin(userImage, new Insets(0,0,0,10));

        String userNameRole = eventCellFactory.getUserName() + " (" + eventCellFactory.getUserRole() + ")";
        Text txtUserNameRole = new Text(userNameRole);
        Label lblDesc = new Label(eventCellFactory.getDescription());
        VBox userAndDesc = new VBox(5,txtUserNameRole,lblDesc);
        userAndDesc.setAlignment(Pos.CENTER_LEFT);
        userAndDesc.setPadding(new Insets(0, 0, 0, 10));

        LocalDateTime dateTime = eventCellFactory.getDateTime().toLocalDateTime();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("MMMM d yyyy");
        DateTimeFormatter time = DateTimeFormatter.ofPattern("hh-mm-ss (a)");
        Label lblDate = new Label(dateTime.format(date));
        Label lblTime = new Label(dateTime.format(time));
        VBox dateAndTime = new VBox(5,lblDate,lblTime);
        dateAndTime.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHgrow(dateAndTime, Priority.ALWAYS);
        dateAndTime.setPadding(new Insets(0, 30, 0, 10));

        Label deleteButton = new Label();
        deleteButton.setGraphic(new ImageView(new Image(
                getClass().getResourceAsStream("../../resources/icons/png/24x24/minus.png")
        )));
        GridPane.setConstraints(deleteButton, 3,0, 1,2);

        deleteButton.setOnMouseClicked( (event) -> {
            lvRecentEvents.getItems().remove(lvRecentEvents.getSelectionModel().getSelectedIndex());
            eventDao.deleteEvent(lvRecentEvents.getSelectionModel().getSelectedItem().getEventId());
        });

        GridPane.setConstraints(userImage,0,0);
        GridPane.setConstraints(userAndDesc, 1, 0);
        GridPane.setConstraints(dateAndTime, 2, 0);
        gridPane.getChildren().addAll(userImage, userAndDesc, dateAndTime, deleteButton);

        return gridPane;
    }

    private void displayTop5SalesMonth () {
        String top5SalesMonth = txtTop5SalesMonth.getText() + " " + now.getMonth();
        txtTop5SalesMonth.setText(top5SalesMonth.toUpperCase());

        ObservableList<Top5SalesModel> top5SalesModelsMonth = FXCollections.observableArrayList(
                salesDao.top5SalesModels(now.getYear(), now.getMonthValue())
        );

        tcTop5SalesMonthSN.setCellValueFactory( cellData -> cellData.getValue().serialNumberProperty().asObject() );
        tcTop5SalesMonthPN.setCellValueFactory( cellData -> cellData.getValue().productNameProperty() );
        tcTop5SalesMonthQT.setCellValueFactory( cellData -> cellData.getValue().quantityProperty().asObject() );

        tbvTopSalesMonth.setItems(top5SalesModelsMonth);
    }

    private void displayTop5SalesYear () {
        String top5SalesYear = txtTop5SalesYear.getText() + " " + now.getYear();
        txtTop5SalesYear.setText(top5SalesYear.toUpperCase());

        ObservableList<Top5SalesModel> top5SalesModelsYear = FXCollections.observableArrayList(
                salesDao.top5SalesModels(now.getYear(), -1)
        );

        tcTop5SalesYearSN.setCellValueFactory( cellData -> cellData.getValue().serialNumberProperty().asObject() );
        tcTop5SalesYearPN.setCellValueFactory( cellData -> cellData.getValue().productNameProperty() );
        tcTop5SalesYearQT.setCellValueFactory( cellData -> cellData.getValue().quantityProperty().asObject() );

        tbvTopSalesYear.setItems(top5SalesModelsYear);
    }

    private void displayDailySales () {
        int today = LocalDate.now().getDayOfMonth();
        double dailySales = salesDao.getDailySales(today);
        txtDailySales.setText(String.format("%.2f", dailySales));
    }

    private void displayWeeklySales () {
        double weeklySales = salesDao.getWeeklySales();
        txtWeeklySales.setText(String.format("%.2f", weeklySales));
    }

    private void displayMonthlySales () {
        double monthlySales = salesDao.getMonthlySales();
        txtMonthlySales.setText(String.format("%.2f", monthlySales));
    }

    private void displayYearlySales () {
        double yearlySales = salesDao.getYearlySales();
        txtYearlySales.setText(String.format("%.2f", yearlySales));
    }

    private void displayImage (File file) {
        if (file != null) {
            imgvUsers.setFitWidth(48);
            imgvUsers.setFitHeight(48);
            Rectangle rectangle = new Rectangle(
                    imgvUsers.getFitWidth(), imgvUsers.getFitHeight()
            );
            rectangle.setArcWidth(50);
            rectangle.setArcHeight(50);
            imgvUsers.setClip(rectangle);
            imgvUsers.setImage(new Image("file:" + file.getAbsolutePath()));
            lblImage.setStyle("-fx-opacity:1");
        }
    }

    private boolean validateIndividualInput(String formatType, Node node) {
        boolean isValid = false;
        if (node instanceof TextField) {
            TextField textField = (TextField)node;
            if (textField.getText().trim().isEmpty()) {
                textField.setStyle(errorStyle());
                isValid = false;
            } else {
                textField.setStyle(successStyle());
                isValid = true;
            }
        }

        if (node instanceof ChoiceBox) {
            ChoiceBox choiceBox = (ChoiceBox)node;
            if (!choiceBox.getValue().equals(" - Select Item - ") && !choiceBox.getValue().equals(" - Measurement Units - ")) {
                choiceBox.setStyle(successStyle());
                isValid = isValid;
            } else {
                choiceBox.setStyle(errorStyle());
                if (isValid) {
                    ;
                }

                isValid = false;
            }
        }

        return isValid;
    }

    private static String errorStyle() {
        String style = "-fx-base: #F04466";
        return style;
    }

    private static String successStyle() {
        String style = "";
        return style;
    }
}
