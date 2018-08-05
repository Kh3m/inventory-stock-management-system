package inventory_management.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import inventory_management.data.InventoryContract.StockHisEntry;
import inventory_management.utils.ConnectionUtil;

/**
 * Created by Kh3m on 7/17/2018
 */
public class StockHisDao {

    public int insertStockHis (int stockId, int quantity, double unitCost) {

        final String SQL_INSERT_STOCK_HIS = "INSERT INTO " +
                StockHisEntry.TABLE_NAME + " ( " +
                StockHisEntry.COLUMN_STOCK_ID + ", " +
                StockHisEntry.COLUMN_QUANTITY + ", " +
                StockHisEntry.COLUMN_UNIT_COST + ", " +
                StockHisEntry.COLUMN_TCU + ", " +
                StockHisEntry.COLUMN_LAST_UPDATED +
                " ) VALUES ( ?, ?, ?, ?, ? ); ";

        int rowsAffected = 0;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_STOCK_HIS)
            ) {

            LocalDateTime now = LocalDateTime.now();
            double tcu = quantity * unitCost;

            pstmt.setInt(1, stockId);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, unitCost);
            pstmt.setDouble(4, tcu);
            pstmt.setString(5, now.toString());

            rowsAffected = pstmt.executeUpdate();

        } catch (SQLException exception) {
            return rowsAffected;
        }

        return rowsAffected;
    }
}
