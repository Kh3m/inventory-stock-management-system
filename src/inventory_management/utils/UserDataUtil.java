package inventory_management.utils;

import inventory_management.model.UserModel;

/**
 * Created by Kh3m on 7/23/2018.
 */
public class UserDataUtil {
    private static UserModel userModel;

    public static void setUserModel (UserModel user) {
        userModel = user;
    }

    public static UserModel getUserModel () {
        return userModel;
    }
}
