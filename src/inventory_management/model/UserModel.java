package inventory_management.model;

import javafx.beans.property.*;

import java.io.File;

/**
 * Created by Kh3m on 7/22/2018.
 */
public class UserModel {
    private IntegerProperty userId;
    private StringProperty role;
    private StringProperty userName;
    private StringProperty password;
    private ObjectProperty<File> imageFile;

    public UserModel(int userId, String role, String userName, String password) {
        this.userId = new SimpleIntegerProperty(userId);
        this.role = new SimpleStringProperty(role);
        this.userName = new SimpleStringProperty(userName);
        this.password = new SimpleStringProperty(password);
    }

    public UserModel(int userId, String role, String userName, String password, File imageFile) {
        this.userId = new SimpleIntegerProperty(userId);
        this.role = new SimpleStringProperty(role);
        this.userName = new SimpleStringProperty(userName);
        this.password = new SimpleStringProperty(password);
        this.imageFile = new SimpleObjectProperty<>(imageFile);
    }

    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public String getRole() {
        return role.get();
    }

    public StringProperty roleIdProperty() {
        return role;
    }

    public void setRoleId(String role) {
        this.role.set(role);
    }

    public String getUserName() {
        return userName.get();
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public File getImageFile() {
        return imageFile.get();
    }

    public ObjectProperty<File> imageFileProperty() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile.set(imageFile);
    }
}
