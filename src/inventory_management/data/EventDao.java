package inventory_management.data;

/**
 * Created by Kh3m on 7/23/2018
 */

import inventory_management.data.InventoryContract.EventEntry;
import inventory_management.data.InventoryContract.UserEntry;
import inventory_management.factory.EventCellFactory;
import inventory_management.utils.ConnectionUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class EventDao {

    public ObservableList<EventCellFactory> getEvents () {
        final String SQL_GET_EVENTS = " select `event_id`, `image`, `username`, `role`,\n" +
                " `description`, `timestamp` from\n" +
                " events as t1 inner join users as t2\n" +
                " on t1.user_id = t2.user_id\n" +
                " inner join `role` as t3\n" +
                " on t2.role_id = t3.role_id" +
                " order by `timestamp` desc;";

        ObservableList<EventCellFactory> observableList = FXCollections.observableArrayList();

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_GET_EVENTS)
            ){

            while (rs.next()) {
                observableList.add(
                        new EventCellFactory(
                                rs.getInt(EventEntry._ID),
                                new UserDao().getUserImage(rs.getString(UserEntry.COLUMN_USERNAME)),
                                rs.getString(UserEntry.COLUMN_USERNAME),
                                rs.getString(UserEntry.COLUMN_ROLE),
                                rs.getString(EventEntry.COLUMN_DESC),
                                rs.getTimestamp(EventEntry.COLUMN_DATE))
                );
            }

        } catch (SQLException exc) {
            System.out.println(exc.getMessage());
        }

        return observableList;

    }
    public int insertEvent (int userId, String action, String description) {
        final String SQL_INSERT_EVENT = "INSERT INTO " +
                EventEntry.TABLE_NAME + " ( " +
                EventEntry.COLUMN_USER_ID + ", " +
                EventEntry.COLUMN_ACTION + ", " +
                EventEntry.COLUMN_DESC + ", " +
                EventEntry.COLUMN_DATE + " ) VALUES (?, ?, ?, ?);";

        int rowsAffected = 0;
        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_EVENT)
            )
        {

            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            pstmt.setString(3, description);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            rowsAffected = pstmt.executeUpdate();

        } catch (SQLException exc) {
            System.out.println(exc.getMessage());
        }

        return rowsAffected;
    }

    public int deleteEvent (int _id) {

        final String SQL_DELETE_ITEM = "DELETE FROM " +
                EventEntry.TABLE_NAME + " WHERE "  + EventEntry._ID + " = ?";

        int rowsAffected;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_DELETE_ITEM)
        ){

            pstm.setInt(1, _id);
            rowsAffected = pstm.executeUpdate();

        } catch (SQLException exception) {
            rowsAffected = 0;
        }

        return rowsAffected;
    }
}
