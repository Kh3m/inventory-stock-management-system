package inventory_management.data;

import inventory_management.model.ItemModel;
import inventory_management.utils.ConnectionUtil;
import inventory_management.data.InventoryContract.ItemEntry;
import inventory_management.data.InventoryContract.StockHisEntry;
import inventory_management.utils.UserDataUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Created by Kh3m on 7/5/2018
 */
public class ItemDao {

    private EventDao eventDao;
    public ItemDao () {
        eventDao = new EventDao();
    }
    public ObservableList<ItemModel> getAllItems () {
        final String SQL_QUERY_ALL_ITEM = "SELECT " +
                ItemEntry._ID + ", " +
                ItemEntry.COLUMN_ITEM_NAME + ", " +
                ItemEntry.COLUMN_TOTAL_QUANTITY + ", " +
                ItemEntry.COLUMN_RATE + ", " +
                ItemEntry.COLUMN_TCU + ", " +
                ItemEntry.COLUMN_UNIT + ", " +
                ItemEntry.COLUMN_CREATED_ON +
                " FROM " + ItemEntry.TABLE_NAME;

        ObservableList<ItemModel> itemModels = FXCollections.observableArrayList();

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_QUERY_ALL_ITEM);
                ResultSet rs = pstm.executeQuery()
        ) {

            while (rs.next()) {
                itemModels.add(
                        new ItemModel(rs.getInt(ItemEntry._ID),
                                rs.getString(ItemEntry.COLUMN_ITEM_NAME),
                                rs.getInt(ItemEntry.COLUMN_TOTAL_QUANTITY),
                                rs.getDouble(ItemEntry.COLUMN_RATE),
                                rs.getDouble(ItemEntry.COLUMN_TCU),
                                rs.getString(ItemEntry.COLUMN_UNIT),
                                rs.getString(ItemEntry.COLUMN_CREATED_ON)));
            }

        } catch (SQLException exception) {
            System.out.println("Failed to load all item: " + exception);
        }

        return itemModels;
    }

    public long getItemCount () {
        final String SQL_QUERY_ALL_ITEM = "SELECT " +
                "COUNT(*) as " + InventoryContract.COLUMN_ALIAS_COUNT +
                " FROM " + InventoryContract.ItemEntry.TABLE_NAME;

        long count = 0;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                Statement stm = conn.createStatement();
                ResultSet rs = stm.executeQuery(SQL_QUERY_ALL_ITEM)
        ) {

            if (rs.next())
                count = rs.getLong(InventoryContract.COLUMN_ALIAS_COUNT);

        } catch (SQLException exception) {
            System.out.println("Failed to load all item count: " + exception);
            count = 0;
        }

        return count;
    }

    public ObservableList<String> getItemNames () {
        ObservableList<String> itemNames = FXCollections.observableArrayList();
        ObservableList<ItemModel> itemModels = FXCollections.observableArrayList(getAllItems());

        for (int i = 0; i < itemModels.size(); i++) {
            itemNames.add(itemModels.get(i).getItemName());
        }

        return itemNames;
    }

    public double getItemRateByItemName(String itemName) {
        String SQL_FIND_ITEM_RATE = "SELECT " +
                ItemEntry.COLUMN_RATE + " FROM " +
                ItemEntry.TABLE_NAME + " WHERE " +
                ItemEntry.COLUMN_ITEM_NAME + " = ?;";

        double itemRate;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_FIND_ITEM_RATE)
            ) {

            pstm.setString(1, itemName);
            ResultSet rs = pstm.executeQuery();
            rs.next();
            itemRate = rs.getDouble(ItemEntry.COLUMN_RATE);

        } catch (SQLException exception) {
            itemRate = 0;
        }
        return itemRate;
    }

    public int getItemIdByItemName(String itemName) {
        String SQL_FIND_ITEM_ID = "SELECT " +
                ItemEntry._ID + " FROM " +
                ItemEntry.TABLE_NAME + " WHERE " +
                ItemEntry.COLUMN_ITEM_NAME + " = ?; ";

        int itemId;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_FIND_ITEM_ID)
        ) {

            pstm.setString(1, itemName);
            ResultSet rs = pstm.executeQuery();
            rs.next();
            itemId = rs.getInt(ItemEntry._ID);

        } catch (SQLException exception) {
            itemId = 0;
        }
        return itemId;
    }

    public String getItemByItemId(int id) {
        String SQL_FIND_ITEM_ID = "SELECT " +
                ItemEntry.COLUMN_ITEM_NAME + " FROM " +
                ItemEntry.TABLE_NAME + " WHERE " +
                ItemEntry._ID + " = ?; ";

        String itemName;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_FIND_ITEM_ID)
        ) {

            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            rs.next();
            itemName = rs.getString(ItemEntry.COLUMN_ITEM_NAME);

        } catch (SQLException exception) {
            itemName = null;
        }
        return itemName;
    }

    public int insertItem (String itemName, int quantity, double rate, String unit, int cat_id) {

        final String SQL_INSERT_ITEM = "INSERT INTO " +
                ItemEntry.TABLE_NAME + " ( " +
                ItemEntry.COLUMN_CAT_ID + ", " +
                ItemEntry.COLUMN_ITEM_NAME + ", " +
                ItemEntry.COLUMN_TOTAL_QUANTITY + ", " +
                ItemEntry.COLUMN_RATE + ", " +
                ItemEntry.COLUMN_TCU + ", " +
                ItemEntry.COLUMN_UNIT  + ", " +
                ItemEntry.COLUMN_CREATED_ON + " ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        final String SQL_INSERT_STOCK_HIS = "INSERT INTO " +
                StockHisEntry.TABLE_NAME + " ( " +
                StockHisEntry.COLUMN_STOCK_ID + ", " +
                StockHisEntry.COLUMN_QUANTITY + ", " +
                StockHisEntry.COLUMN_UNIT_COST + ", " +
                StockHisEntry.COLUMN_TCU + ", " +
                StockHisEntry.COLUMN_LAST_UPDATED + " ) " +
                "VALUES (?, ?, ?, ?, ?)";

        int rowsAffected;
        ResultSet rsKeys = null;
        PreparedStatement pstmStockHis = null;
        LocalDateTime now = LocalDateTime.now();
        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_INSERT_ITEM, Statement.RETURN_GENERATED_KEYS)
        ) {
            // disable auto commit
            conn.setAutoCommit(false);

            double tcu = quantity * rate;
            pstm.setInt(1, cat_id);
            pstm.setString(2, itemName);
            pstm.setInt(3, quantity);
            pstm.setDouble(4, rate);
            pstm.setDouble(5, tcu);
            pstm.setString(6, unit);
            pstm.setString(7, now.toString());

            rowsAffected = pstm.executeUpdate();

            if (rowsAffected == 1) {
                rsKeys = pstm.getGeneratedKeys();

                if (rsKeys.next()) {
                    int id = rsKeys.getInt(1);
                    pstmStockHis = conn.prepareStatement(SQL_INSERT_STOCK_HIS);
                    pstmStockHis.setInt(1, id);
                    pstmStockHis.setInt(2, quantity);
                    pstmStockHis.setDouble(3, rate);
                    pstmStockHis.setDouble(4, tcu);
                    pstmStockHis.setString(5, now.toString());

                    int pstmStockHisRowsAffected = pstmStockHis.executeUpdate();

                    if (pstmStockHisRowsAffected < 1 )
                        conn.rollback();
                    else {
                        eventDao.insertEvent(UserDataUtil.getUserModel().getUserId(),
                                "Insert",
                                "Insert " + itemName + " into stock table");
                        conn.commit();
                    }

                }
            } else {
                conn.rollback();
            }

        } catch (SQLException exception) {
            System.out.println("Failed to insert item: " + exception);
            rowsAffected = 0;
        } finally {
            try {
                if (rsKeys != null) rsKeys.close();

                if (pstmStockHis != null) pstmStockHis.close();

            } catch (SQLException exception) {
                System.out.println("Failed to close");
            }
        }

        return rowsAffected;
    }

    public int updateItem (int _id, String columnName, Object item) {

        final String SQL_UPDATE_ITEM = "UPDATE " +
                ItemEntry.TABLE_NAME + " SET " +
                columnName + " = ?" +
                " WHERE " + ItemEntry._ID + " = ?";

        int rowsAffected;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_UPDATE_ITEM)
        ) {

            pstm.setObject(1, item);
            pstm.setInt(2, _id);

            rowsAffected = pstm.executeUpdate();

            if (rowsAffected == 1) {
                eventDao.insertEvent(UserDataUtil.getUserModel().getUserId(),
                        "Update",
                        "Update " + item + " in stock table under column " + columnName);
            }

        } catch (SQLException exception) {
            rowsAffected = 0;
        }

        return rowsAffected;
    }

    public void updateQuantity (int id, int quantity) {
        final String SQL_UPDATE_QUANTITY = "UPDATE " +
                ItemEntry.TABLE_NAME + " SET " +
                ItemEntry.COLUMN_TOTAL_QUANTITY  + " =  " +
                ItemEntry.COLUMN_TOTAL_QUANTITY  + " + ?, " +
                ItemEntry.COLUMN_TCU  + " = " +
                ItemEntry.COLUMN_TOTAL_QUANTITY + " * " +
                ItemEntry.COLUMN_RATE +
                " WHERE " + ItemEntry._ID + " = ?";

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_UPDATE_QUANTITY)
            ) {

            pstm.setInt(1, quantity);
            pstm.setInt(2, id);
            pstm.executeUpdate();

        } catch (SQLException exception) {
            System.out.println(exception);
        }
    }

    public int deleteItem (int _id) {

        final String SQL_DELETE_ITEM = "DELETE FROM " +
                ItemEntry.TABLE_NAME + " WHERE "  + ItemEntry._ID + " = ?";

        int rowsAffected;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_DELETE_ITEM)
        ){

            eventDao.insertEvent(UserDataUtil.getUserModel().getUserId(),
                    "Delete",
                    "Delete " + getItemByItemId(_id) + " from stock");
            pstm.setInt(1, _id);
            rowsAffected = pstm.executeUpdate();


        } catch (SQLException exception) {
            rowsAffected = 0;
        }

        return rowsAffected;
    }

    /**
     * @param year ( current year )
     * @param month of current year
     * @return double
     */
    public double getMonthlyPurchase (int year, int month) {
        final String SQL_MONTHLY_PURCHASE = "SELECT " +
                "YEAR(`" + StockHisEntry.COLUMN_LAST_UPDATED + "`) AS" +
                InventoryContract.COLUMN_ALIAS_YEAR + ", " +
                "MONTH(`" + StockHisEntry.COLUMN_LAST_UPDATED + "`) AS " +
                InventoryContract.COLUMN_ALIAS_MONTH + ", " +
                "SUM(`" + StockHisEntry.COLUMN_QUANTITY + "` * `" +
                StockHisEntry.COLUMN_UNIT_COST +"`) AS " +
                InventoryContract.COLUMN_ALIAS_SUM +
                " FROM " + StockHisEntry.TABLE_NAME +
                " WHERE YEAR(`" + StockHisEntry.COLUMN_LAST_UPDATED  + "`) = " + year + " AND " +
                "MONTH(`" + StockHisEntry.COLUMN_LAST_UPDATED + "`) = " + month +
                " GROUP BY " +
                "YEAR(`" + StockHisEntry.COLUMN_LAST_UPDATED  + "`), " +
                "MONTH(`" + StockHisEntry.COLUMN_LAST_UPDATED  + "`);";

        double monthlyPurchase;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_MONTHLY_PURCHASE)
        ) {

            if (rs.next()) {
                monthlyPurchase = rs.getDouble(InventoryContract.COLUMN_ALIAS_SUM);
            } else {
                monthlyPurchase = 0;
            }

        } catch (SQLException exception) {
            System.out.println(exception);
            monthlyPurchase = 0;
        }

        return monthlyPurchase;
    }

}
