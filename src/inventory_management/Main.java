package inventory_management;

import inventory_management.utils.ConnectionUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initRootLayout();
    }

    private void initRootLayout () {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../resources/layouts/login.fxml"));
            Scene scene = new Scene(root, 795, 500);
            primaryStage.setTitle("InventoryController Management");
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../resources/icons/bahubali.PNG")));
            primaryStage.show();
        } catch (IOException ioe) {
            System.out.println("Something went wrong @Main @initRootLayout(): ");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage () {
        return primaryStage;
    }
}
