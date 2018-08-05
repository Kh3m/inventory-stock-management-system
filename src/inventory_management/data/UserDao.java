package inventory_management.data;

import inventory_management.utils.UserDataUtil;
import javafx.scene.image.*;
import java.io.*;
import java.sql.*;

import inventory_management.data.InventoryContract.UserEntry;
import inventory_management.data.InventoryContract.RoleEntry;
import inventory_management.model.UserModel;
import inventory_management.utils.ConnectionUtil;

/**
 * Created by Kh3m on 7/22/2018
 */
public class UserDao {

    private EventDao eventDao;


    public UserDao () {
        eventDao = new EventDao();
    }

    public long getUserCount () {
        final String SQL_QUERY_ALL_ITEM = "SELECT " +
                "COUNT(*) as " + InventoryContract.COLUMN_ALIAS_COUNT +
                " FROM " + UserEntry.TABLE_NAME;

        long count = 0;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                Statement stm = conn.createStatement();
                ResultSet rs = stm.executeQuery(SQL_QUERY_ALL_ITEM)
        ) {

            if (rs.next())
                count = rs.getLong(InventoryContract.COLUMN_ALIAS_COUNT);

        } catch (SQLException exception) {
            System.out.println("Failed to load all sales count: " + exception);
            count = 0;
        }

        return count;
    }

    public int insertUser (int roleId, String userName, File imageFile, String pass) {
        final String SQL_INSERT_USER = "INSERT INTO " +
                UserEntry.TABLE_NAME + " ( " +
                UserEntry.COLUMN_ROLE_ID + ", " +
                UserEntry.COLUMN_USERNAME + ", " +
                UserEntry.COLUMN_PASSWORD + ", " +
                UserEntry.COLUMN_IMAGE + " ) VALUES (?, ?, ?, ?);";

        System.out.println(SQL_INSERT_USER);
        int rowsAffected = 0;
        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_USER)
            ){

            FileInputStream inputStream = new FileInputStream(imageFile);

            pstmt.setInt(1, roleId);
            pstmt.setString(2, userName);
            pstmt.setString(3, pass);
            pstmt.setBinaryStream(4, inputStream);

            rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 1) {
                eventDao.insertEvent(UserDataUtil.getUserModel().getUserId(), "Insert",
                        "Insert " + userName + " to existing users");
                System.out.println("Record inserted successful");
                return rowsAffected;
            }

        } catch (SQLException | IOException exc) {
            System.out.println(exc.getMessage());
            return rowsAffected;
        }
        return rowsAffected;
    }

    public UserModel getUserByUsername (String username) {
        final String SQL_FIND_USER = "SELECT " +
                UserEntry._ID + ", " +
                UserEntry.COLUMN_ROLE + ", " +
                UserEntry.COLUMN_USERNAME + ", " +
                UserEntry.COLUMN_PASSWORD + " " +
                "FROM " + UserEntry.TABLE_NAME + " as t1 " +
                "INNER JOIN " + RoleEntry.TABLE_NAME + " as t2 " +
                "ON t1." + UserEntry.COLUMN_ROLE_ID + " = " +
                " t2."+ RoleEntry._ID +
                " WHERE " + UserEntry.COLUMN_USERNAME + " = ?;";

        UserModel userModel = null;
        ResultSet rs = null;
        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SQL_FIND_USER)
            ) {

            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                userModel = new UserModel(
                        rs.getInt(UserEntry._ID),
                        rs.getString(UserEntry.COLUMN_ROLE),
                        rs.getString(UserEntry.COLUMN_USERNAME),
                        rs.getString(UserEntry.COLUMN_PASSWORD)
                );
            }

        } catch (SQLException exc) {
            exc.printStackTrace();
            return userModel;
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userModel;
    }

    public Image getUserImage (String username) {
        final String SQL_FIND_USER_IMAGE = "SELECT " +
                UserEntry.COLUMN_IMAGE + " " +
                "FROM " + UserEntry.TABLE_NAME +
                " WHERE " + UserEntry.COLUMN_USERNAME + " = ?;";
        Image image = null;
        ResultSet rs = null;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SQL_FIND_USER_IMAGE)
            )
        {

            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            File fileName = new File("uploads-" + username);
            FileOutputStream outputStream = new FileOutputStream(fileName);
            if (rs.next()) {
                InputStream inputStream = rs.getBinaryStream(UserEntry.COLUMN_IMAGE);
                byte []buffer = new byte[1024];

                while (inputStream.read(buffer) > 0) {
                    outputStream.write(buffer);
                }

                image = new Image("file:" + fileName.getAbsolutePath());
            }

        } catch (SQLException | IOException exc){
            System.out.println(exc.getMessage());
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return image;
    }
}
