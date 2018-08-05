package inventory_management.controllers;

import inventory_management.data.UserDao;
import inventory_management.utils.UserDataUtil;
import inventory_management.factory.MainMenuCellFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;


import java.io.IOException;


public class MainController {

    @FXML
    private BorderPane root;

    private ListView<MainMenuCellFactory> lvMainMenu;
    private Parent parent;
    private UserDao userDao;

    public void initialize () throws Exception{
        userDao = new UserDao();
        configureMenus();
        lvMainMenu.getSelectionModel().selectFirst();

        // Default Configs
        root.setCenter(FXMLLoader.load(getClass().getResource("../../resources/layouts/dashboard.fxml")));
    }

    private void configureMenus () {
        lvMainMenu = new ListView();
        ObservableList<MainMenuCellFactory> menus = FXCollections.observableArrayList();
        menus.addAll(
            new MainMenuCellFactory(new Image(getClass().getResourceAsStream("../../resources/icons/png/24x24/home.png")), "Dashboard"),
            new MainMenuCellFactory(new Image(getClass().getResourceAsStream("../../resources/icons/png/24x24/briefcase.png")), "Item & Category"),
//            new MainMenuCellFactory(new Image(getClass().getResourceAsStream("../../resources/icons/png/24x24/palette.png")), "Inventory"),
            new MainMenuCellFactory(new Image(getClass().getResourceAsStream("../../resources/icons/png/24x24/yen_currency_sign.png")), "Sales"),
            new MainMenuCellFactory(userDao.getUserImage(UserDataUtil.getUserModel().getUserName()),
                    UserDataUtil.getUserModel().getUserName(), UserDataUtil.getUserModel().getRole())
        );

        lvMainMenu.getItems().addAll(menus);
        lvMainMenu.setCellFactory(new Callback<ListView<MainMenuCellFactory>, ListCell<MainMenuCellFactory>>() {
            @Override
            public ListCell call(ListView param) {
                final ListCell<MainMenuCellFactory> listCell = new ListCell<MainMenuCellFactory>() {
                    @Override
                    public void updateItem (MainMenuCellFactory item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(arrangeMenuItem(item));
                        }
                    }
                };

                return listCell;
            }
        });

        lvMainMenu.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<MainMenuCellFactory>() {
                    @Override
                    public void changed(ObservableValue<? extends MainMenuCellFactory> observable, MainMenuCellFactory oldValue, MainMenuCellFactory newValue) {
                        if (newValue.isUserUI())
                            return;

                        try {
                            switch (newValue.getMenuName().trim().toLowerCase()) {
                                case "dashboard": {
                                    parent = FXMLLoader.load(getClass().getResource("../../resources/layouts/dashboard.fxml"));
                                    break;
                                }
                                case "item & category": {
                                    parent = FXMLLoader.load(getClass().getResource("../../resources/layouts/item_category.fxml"));
                                    break;
                                }
                                case "inventory": {
                                    parent = FXMLLoader.load(getClass().getResource("../../resources/layouts/inventory.fxml"));
                                    break;
                                }
                                case "sales": {
                                    parent = FXMLLoader.load(getClass().getResource("../../resources/layouts/sales.fxml"));
                                    break;
                                }
                            }
                        } catch (IOException ioe) {
                            System.out.println("Something went wrong @MainController @changed");
                        }

                        root.setCenter(parent);
                    }
                }
        );
        root.setLeft(lvMainMenu);
    }

    private HBox arrangeMenuItem (MainMenuCellFactory item) {

        ImageView leftIconImageView = new ImageView(item.getLeftIcon());
        Label menuNameLabel = new Label(item.getMenuName());

        menuNameLabel.setPadding(new Insets(5));

        HBox hBox = new HBox(leftIconImageView, menuNameLabel);
        hBox.setSpacing(16);
        hBox.setPadding(new Insets(5));

        if (item.isUserUI()) {
            ImageView userImageView = new ImageView(item.getUserImage());
            userImageView.setFitHeight(128);
            userImageView.setFitWidth(128);
            Rectangle imageClip = new Rectangle(
                    userImageView.getFitWidth(), userImageView.getFitHeight()
            );
            imageClip.setArcHeight(userImageView.getFitHeight());
            imageClip.setArcWidth(userImageView.getFitWidth());
            userImageView.setClip(imageClip);

            Text userNameText = new Text(item.getUserName());
            userNameText.setFont(Font.font("Arial", 30));
            Label userRoleLabel = new Label(item.getUserRole());
            Button btnLogOut = new Button("", new ImageView(new Image(getClass().getResourceAsStream("../../resources/icons/png/24x24/shut_down.png"))));
            btnLogOut.setOnAction(event -> {
                Stage stage = (Stage) btnLogOut.getScene().getWindow();
                try {
                    Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../../resources/layouts/login.fxml")),
                            795, 500);
                    stage.setScene(scene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            VBox vBox = new VBox(10, userImageView, userNameText, userRoleLabel, btnLogOut);
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(30, 0, 0,0));
            hBox.getChildren().add(vBox);
        }

        return hBox;
    }

}