package inventory_management.factory;

import javafx.scene.image.*;
/**
 * Created by Kh3m on 7/1/2018.
 */
public class MainMenuCellFactory {
    private Image leftIcon;
    private Image rightIcon;
    private String menuName;

    // for user
    private Image userImage;
    private String userName;
    private String userRole;

    private boolean userUI = false;

    public MainMenuCellFactory(Image leftIcon, String menuName) {
        this.leftIcon = leftIcon;
        this.menuName = menuName;
    }

    public MainMenuCellFactory (Image leftIcon, Image rightIcon, String menuName) {
        this(leftIcon, menuName);
        this.rightIcon = rightIcon;
    }

    public MainMenuCellFactory (Image userImage, String userName, String userRole) {
        this.userImage = userImage;
        this.userName = userName;
        this.userRole = userRole;
        userUI = true;
    }

    public Image getLeftIcon() {
        return leftIcon;
    }

    public Image getRightIcon() {
        return rightIcon;
    }

    public String getMenuName() {
        return menuName;
    }

    public Image getUserImage() {
        return userImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public boolean isUserUI() {
        return userUI;
    }
}
