package inventory_management.data;

import inventory_management.model.ItemModel;
import inventory_management.utils.ConnectionUtil;
import inventory_management.data.InventoryContract.RoleEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Kh3m on 7/22/2018.
 */
public class RoleDao {

    public ObservableList<String> getRoles () {
        final String SQL_QUERY_ALL_ROLE = "SELECT " +
                RoleEntry._ID + ", " +
                RoleEntry.COLUMN_ROLE +
                " FROM " + RoleEntry.TABLE_NAME;

        ObservableList<String> role = FXCollections.observableArrayList();

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_QUERY_ALL_ROLE);
                ResultSet rs = pstm.executeQuery()
        ) {

            while (rs.next()) {
                role.add(rs.getString(RoleEntry.COLUMN_ROLE));
            }

        } catch (SQLException exception) {
            System.out.println("Failed to load all item: " + exception);
        }

        return role;
    }



    public int getRoleIdByRole(String role) {
        String SQL_FIND_ROLE_ID = "SELECT " +
                RoleEntry._ID + " FROM " +
                RoleEntry.TABLE_NAME + " WHERE " +
                RoleEntry.COLUMN_ROLE + " = ?; ";

        int roleId;
        ResultSet rs = null;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_FIND_ROLE_ID)
        ) {

            pstm.setString(1, role);
            rs = pstm.executeQuery();
            rs.next();
            roleId = rs.getInt(RoleEntry._ID);

        } catch (SQLException exception) {
            roleId = 0;
            System.out.println(exception.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return roleId;
    }
}
