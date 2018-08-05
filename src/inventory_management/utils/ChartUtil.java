package inventory_management.utils;

import inventory_management.data.ItemDao;
import inventory_management.utils.ConnectionUtil;
import inventory_management.data.InventoryContract.SalesEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Kh3m on 7/30/2018
 */
public class ChartUtil {

    public static double getMonthlyProfit (int year, int month) {

        final String SQL = "select item_name, rate,\n" +
                "sum(`quantity`) quantity from\n" +
                "`sales` where\n" +
                "year(`created_on`) = ? and\n" +
                "month(`created_on`) = ?\n" +
                "group by item_name, rate;\n";

        ResultSet rs = null;
        double profit = 0.0;
        ItemDao itemDao = new ItemDao();
        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SQL)
            ){

            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            rs = pstmt.executeQuery();
            double totalPurchasePrice = 0.0;
            double totalSalePrice = 0.0;
            while (rs.next()) {
                int quantity = rs.getInt(SalesEntry.COLUMN_QUANTITY);
                double purchasePrice = itemDao.getItemRateByItemName(rs.getString(SalesEntry.COLUMN_ITEM_NAME)) * quantity;
                double salePrice = rs.getDouble(SalesEntry.COLUMN_RATE) * quantity;

                totalPurchasePrice += purchasePrice;
                totalSalePrice += salePrice;

            }

            profit = totalSalePrice - totalPurchasePrice;
        } catch (SQLException exc) {
            System.out.println(exc.getMessage());
        } finally {
                if (rs != null) try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

        return profit;
    }

}