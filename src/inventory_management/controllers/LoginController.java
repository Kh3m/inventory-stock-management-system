package inventory_management.controllers;


import inventory_management.utils.UserDataUtil;
import inventory_management.data.UserDao;
import inventory_management.model.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Kh3m on 7/22/2018
 */
public class LoginController {
    @FXML
    AnchorPane loginRoot;
    @FXML
    TextField tfUserName;
    @FXML
    TextField tfPassword;

    private UserDao userDao;

    @FXML
    private void loginClickHandler() {
        userDao = new UserDao();
        UserModel user = userDao.getUserByUsername(tfUserName.getText());
        if (user != null) {
            if (tfPassword.getText().equalsIgnoreCase(user.getPassword())) {
                UserDataUtil.setUserModel(user);
                switchScene();
            } else {
                System.out.println("Wrong Password");
            }
        } else {
            System.out.println("No user found");
        }

    }

    private void switchScene () {
        FXMLLoader loader = new FXMLLoader();
        try {
            Parent parent = loader.load(getClass().getResource("../../resources/layouts/root.fxml"));
            Stage stage = (Stage) loginRoot.getScene().getWindow();
            Scene scene = new Scene(parent, 995, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
