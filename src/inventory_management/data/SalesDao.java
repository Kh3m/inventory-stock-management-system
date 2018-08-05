package inventory_management.data;

import inventory_management.data.InventoryContract.SalesEntry;
import inventory_management.data.InventoryContract.ItemEntry;
import inventory_management.model.SalesModel;
import inventory_management.model.Top5SalesModel;
import inventory_management.utils.ConnectionUtil;
import inventory_management.utils.UserDataUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by Kh3m on 7/6/2018
 */
public class SalesDao {
    EventDao eventDao;
    public SalesDao () {
        eventDao = new EventDao();
    }
    public ObservableList<SalesModel> getAllSales () {
        final String SQL_QUERY_ALL_ITEM = "SELECT " +
                SalesEntry._ID + ", " +
                SalesEntry.COLUMN_ITEM_NAME + ", " +
                SalesEntry.COLUMN_QUANTITY + ", " +
                SalesEntry.COLUMN_RATE + ", " +
                SalesEntry.COLUMN_UNIT + ", " +
                SalesEntry.COLUMN_CREATED_ON +
                " FROM " + SalesEntry.TABLE_NAME;

        ObservableList<SalesModel> salesModels = FXCollections.observableArrayList();

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_QUERY_ALL_ITEM);
                ResultSet rs = pstm.executeQuery()
        ) {

            while (rs.next()) {
                salesModels.add(
                        new SalesModel(rs.getInt(SalesEntry._ID),
                                rs.getString(SalesEntry.COLUMN_ITEM_NAME),
                                rs.getInt(SalesEntry.COLUMN_QUANTITY),
                                rs.getDouble(SalesEntry.COLUMN_RATE),
                                rs.getString(SalesEntry.COLUMN_UNIT),
                                rs.getString(SalesEntry.COLUMN_CREATED_ON)));

            }

        } catch (SQLException exception) {
            System.out.println("Failed to load all sales: " + exception);
        }

        return salesModels;
    }

    public long getSalesCount () {
        final String SQL_QUERY_ALL_ITEM = "SELECT " +
                "COUNT(*) as " + InventoryContract.COLUMN_ALIAS_COUNT +
                " FROM " + SalesEntry.TABLE_NAME;

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

    public ObservableList<SalesModel> getItemsBySearchCriteria (String itemName, LocalDate from, LocalDate to) {
        String SQL_FIND_ITEM_BY_SEARCH_CRITERIA = "";


        if (!itemName.equals("") && from == null && to == null) {
            SQL_FIND_ITEM_BY_SEARCH_CRITERIA = "SELECT " +
                    SalesEntry._ID + ", " +
                    SalesEntry.COLUMN_ITEM_NAME + ", " +
                    SalesEntry.COLUMN_QUANTITY + ", " +
                    SalesEntry.COLUMN_RATE + ", " +
                    SalesEntry.COLUMN_UNIT + ", " +
                    SalesEntry.COLUMN_CREATED_ON +
                    " FROM " + SalesEntry.TABLE_NAME +
                    " WHERE " + SalesEntry.COLUMN_ITEM_NAME + " = ?;";
        }

        if (!itemName.equals("") && from != null && to == null) {
            SQL_FIND_ITEM_BY_SEARCH_CRITERIA = "SELECT " +
                    SalesEntry._ID + ", " +
                    SalesEntry.COLUMN_ITEM_NAME + ", " +
                    SalesEntry.COLUMN_QUANTITY + ", " +
                    SalesEntry.COLUMN_RATE + ", " +
                    SalesEntry.COLUMN_UNIT + ", " +
                    SalesEntry.COLUMN_CREATED_ON +
                    " FROM " + SalesEntry.TABLE_NAME +
                    " WHERE " + SalesEntry.COLUMN_ITEM_NAME + " = ? AND  " +
                    " DATE ( " + SalesEntry.COLUMN_CREATED_ON + " )" +
                    " BETWEEN DATE( CAST( ? AS DATE) )" +
                    " AND DATE( CAST( ? AS DATE) );";
        }

        if (!itemName.equals("") && from != null && to != null) {
            SQL_FIND_ITEM_BY_SEARCH_CRITERIA = "SELECT " +
                    SalesEntry._ID + ", " +
                    SalesEntry.COLUMN_ITEM_NAME + ", " +
                    SalesEntry.COLUMN_QUANTITY + ", " +
                    SalesEntry.COLUMN_RATE + ", " +
                    SalesEntry.COLUMN_UNIT + ", " +
                    SalesEntry.COLUMN_CREATED_ON +
                    " FROM " + SalesEntry.TABLE_NAME +
                    " WHERE " + SalesEntry.COLUMN_ITEM_NAME + " = ? AND  " +
                    " DATE ( " + SalesEntry.COLUMN_CREATED_ON + " )" +
                    " BETWEEN DATE( CAST( ? AS DATE) )" +
                    " AND DATE( CAST( ? AS DATE) );";
        }

        ResultSet rs = null;
        ObservableList<SalesModel> salesModels = FXCollections.observableArrayList();
        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_FIND_ITEM_BY_SEARCH_CRITERIA)
            ) {

            if (!itemName.equals("") && from == null && to == null) {
                pstm.setString(1, itemName);
            }

            LocalDate dateFrom;
            LocalDate dateTo;

            if (!itemName.equals("") && from != null && to == null) {
                dateFrom = LocalDate.of(from.getYear(), from.getMonth(), from.getDayOfMonth());
                pstm.setString(1, itemName);
                pstm.setString(2, dateFrom.toString());
            }

            if (!itemName.equals("") && from != null && to != null) {
                dateFrom = LocalDate.of(from.getYear(), from.getMonth(), from.getDayOfMonth());
                dateTo = LocalDate.of(to.getYear(), to.getMonth(), to.getDayOfMonth());
                pstm.setString(1, itemName);
                pstm.setString(2, dateFrom.toString());
                pstm.setString(3, dateTo.toString());

            }

            rs = pstm.executeQuery();

            while (rs.next()) {
                salesModels.add(
                        new SalesModel(rs.getInt(SalesEntry._ID),
                                rs.getString(SalesEntry.COLUMN_ITEM_NAME),
                                rs.getInt(SalesEntry.COLUMN_QUANTITY),
                                rs.getDouble(SalesEntry.COLUMN_RATE),
                                rs.getString(SalesEntry.COLUMN_UNIT),
                                rs.getString(SalesEntry.COLUMN_CREATED_ON)));

            }

        } catch (SQLException exception) {
                salesModels =  null;
            System.out.println(exception);
        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException exception){
                System.out.println("Failed to close");
            }

        }

        return salesModels;
    }

    public String getItemByItemId(int id) {
        String SQL_FIND_ITEM_ID = "SELECT " +
                SalesEntry.COLUMN_ITEM_NAME + " FROM " +
                SalesEntry.TABLE_NAME + " WHERE " +
                SalesEntry._ID + " = ?;";

        String itemName;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_FIND_ITEM_ID)
        ) {

            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            rs.next();
            itemName = rs.getString(SalesEntry.COLUMN_ITEM_NAME);

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            itemName = null;
        }
        return itemName;
    }

    public int insertItem (String itemName, int quantity, String unit, double unitPrice) {

        final String SQL_INSERT_ITEM = "INSERT INTO " +
                SalesEntry.TABLE_NAME + " ( " +
                SalesEntry.COLUMN_ITEM_NAME + ", " +
                SalesEntry.COLUMN_QUANTITY + ", " +
                SalesEntry.COLUMN_SALE_PRICE + ", " +
                SalesEntry.COLUMN_RATE + ", " +
                SalesEntry.COLUMN_UNIT  + ", " +
                SalesEntry.COLUMN_CREATED_ON + " ) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        final String SQL_UPDATE_STOCK_QUANTITY = "UPDATE " + ItemEntry.TABLE_NAME
                + " SET " + ItemEntry.COLUMN_TOTAL_QUANTITY +
                " = ( SELECT " + ItemEntry.COLUMN_TOTAL_QUANTITY +
                " - ? ), " + ItemEntry.COLUMN_TCU + " = " +
                ItemEntry.COLUMN_TOTAL_QUANTITY + " * " +
                ItemEntry.COLUMN_RATE +
                " WHERE " + ItemEntry.COLUMN_ITEM_NAME + " = ?; ";

        PreparedStatement ps = null;

        int insertedID = 0;
        ResultSet rs = null;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_INSERT_ITEM, Statement.RETURN_GENERATED_KEYS)
        ) {

            double sale_price = quantity * unitPrice;

            pstm.setString(1, itemName);
            pstm.setInt(2, quantity);
            pstm.setDouble(3, sale_price);
            pstm.setDouble(4, unitPrice);
            pstm.setString(5, unit);
            pstm.setString(6, LocalDateTime.now().toString());

            int rowsAffected = pstm.executeUpdate();

            if (rowsAffected == 1) {
                eventDao.insertEvent(UserDataUtil.getUserModel().getUserId(),
                        "Insert",
                        "Sold " + itemName + " for " + unitPrice);
                rs = pstm.getGeneratedKeys();
                if (rs.next())
                    insertedID = rs.getInt(1);
            }

            ps = conn.prepareStatement(SQL_UPDATE_STOCK_QUANTITY);
            ps.setInt(1, quantity);
            ps.setString(2, itemName);
            ps.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Failed to insert item: " + exception);
            insertedID = 0;
        } finally {
            try{
                if (rs != null)
                    rs.close();

                if (ps != null)
                    ps.close();

            } catch (SQLException exceptiom) {
                System.out.println("Failed to close resource");
            }
        }

        return insertedID;
    }

    public SalesModel getInsertedSalesModelById (int id) {
        final String SQL_GET_INSERTED_SALES_MODEL =  "SELECT " +
                SalesEntry._ID + ", " +
                SalesEntry.COLUMN_ITEM_NAME + ", " +
                SalesEntry.COLUMN_QUANTITY + ", " +
                SalesEntry.COLUMN_RATE + ", " +
                SalesEntry.COLUMN_UNIT + ", " +
                SalesEntry.COLUMN_CREATED_ON +
                " FROM " + SalesEntry.TABLE_NAME +
                " WHERE " + SalesEntry._ID + " = ?";

        SalesModel salesModel = null;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_GET_INSERTED_SALES_MODEL)
            ) {

            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                salesModel = new SalesModel(rs.getInt(SalesEntry._ID),
                        rs.getString(SalesEntry.COLUMN_ITEM_NAME),
                        rs.getInt(SalesEntry.COLUMN_QUANTITY),
                        rs.getDouble(SalesEntry.COLUMN_RATE),
                        rs.getString(SalesEntry.COLUMN_UNIT),
                        rs.getString(SalesEntry.COLUMN_CREATED_ON));
            }


        } catch (SQLException exception) {
            salesModel = null;
        }

        return salesModel;

    }

    public int updateSales (int _id, String columnName, Object item) {

        final String SQL_UPDATE_ITEM = "UPDATE " +
                SalesEntry.TABLE_NAME + " SET " +
                columnName + " = ?" +
                " WHERE " + SalesEntry._ID + " = ?";

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
                        "Update " + item + " in sales table under column " + columnName);
            }

        } catch (SQLException exception) {
            rowsAffected = 0;
        }

        return rowsAffected;
    }

    public void updateRate (int id, int quantity, String itemName) {
        final String SQL_UPDATE_QUANTITY = "UPDATE " +
                SalesEntry.TABLE_NAME + " SET " +
                SalesEntry.COLUMN_RATE  + " = ?" +
                " WHERE " + SalesEntry._ID + " = ?";

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_UPDATE_QUANTITY)
        ) {

            ItemDao itemDao = new ItemDao();

            double itemRate = itemDao.getItemRateByItemName(itemName);
            double rate = quantity * itemRate;

            pstm.setDouble(1, rate);
            pstm.setInt(2, id);
            pstm.executeUpdate();

        } catch (SQLException exception) {
            System.out.println(exception);
        }
    }

    public int deleteItem (int _id) {

        final String SQL_DELETE_SALES = "DELETE FROM " +
                SalesEntry.TABLE_NAME + " WHERE "  + SalesEntry._ID + " = ?";

        int rowsAffected;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_DELETE_SALES)
        ){

            pstm.setInt(1, _id);
            eventDao.insertEvent(UserDataUtil.getUserModel().getUserId(),
                    "Delete",
                    "Delete " + getItemByItemId(_id) + " from sales");
            rowsAffected = pstm.executeUpdate();

        } catch (SQLException exception) {
            rowsAffected = 0;
        }

        return rowsAffected;
    }

    /**
     *
     * @param today
     * @return long (daily sales)
     */
    public double getDailySales (int today) {
        final String SQL_TODAYS_SALES = "SELECT SUM(`" +
                SalesEntry.COLUMN_RATE + "`) AS " +
                InventoryContract.COLUMN_ALIAS_SUM +
                " FROM `" + SalesEntry.TABLE_NAME +
                "` WHERE DAYOFMONTH(`" +
                SalesEntry.COLUMN_CREATED_ON + "`) = ? " +
                "GROUP BY DAYOFMONTH(`" +
                SalesEntry.COLUMN_CREATED_ON + "`);";

        ResultSet rs = null;
        double dailySales = 0;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement psmt = conn.prepareStatement(SQL_TODAYS_SALES)
            ) {

            psmt.setInt(1, today);
            rs = psmt.executeQuery();
            if (rs.next()) {
                dailySales = rs.getLong(1);
            }

        } catch (SQLException exception) {
            System.out.println(exception);
            dailySales = 0;
        } finally {
            try {
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException exception){
                System.out.println(exception);
            }

        }
        return dailySales;
    }

    public double getWeeklySales () {
        final String SQL_WEEKLY_SALES = "SELECT " +
                "SUM(`" + SalesEntry.COLUMN_RATE +"`) AS " +
                InventoryContract.COLUMN_ALIAS_SUM +
                " FROM " + SalesEntry.TABLE_NAME +
                " GROUP BY" +
                " YEAR(`" + SalesEntry.COLUMN_CREATED_ON + "`)," +
                " MONTH(`" + SalesEntry.COLUMN_CREATED_ON + "`)," +
                " WEEKOFYEAR(`" + SalesEntry.COLUMN_CREATED_ON + "`)" +
                " ORDER BY WEEKOFYEAR(`" + SalesEntry.COLUMN_CREATED_ON + "`) DESC;";

        double weeklySales = 0;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_WEEKLY_SALES)
        ) {

            if (rs.next()) {
                weeklySales = rs.getDouble(1);
            }

        } catch (SQLException exception) {
            System.out.println(exception);
            weeklySales = 0;
        }

        return weeklySales;
    }

    public double getMonthlySales () {
        final String SQL_MONTHLY_SALES = "SELECT " +
                "SUM(`" + SalesEntry.COLUMN_RATE +"`) AS " +
                InventoryContract.COLUMN_ALIAS_SUM +
                " FROM " + SalesEntry.TABLE_NAME +
                " GROUP BY" +
                " YEAR(`" + SalesEntry.COLUMN_CREATED_ON + "`)," +
                " MONTH(`" + SalesEntry.COLUMN_CREATED_ON + "`)" +
                " ORDER BY YEAR(`" + SalesEntry.COLUMN_CREATED_ON + "`) DESC," +
                " MONTH(`" + SalesEntry.COLUMN_CREATED_ON + "`) DESC;";

        double monthlySales = 0;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_MONTHLY_SALES)
        ) {

            if (rs.next()) {
                monthlySales = rs.getDouble(1);
            }

        } catch (SQLException exception) {
            System.out.println(exception);
            monthlySales = 0;
        }

        return monthlySales;
    }

    public double getYearlySales () {
        final String SQL_YEARLY_SALES = "SELECT " +
                "SUM(`" + SalesEntry.COLUMN_RATE +"`) AS " +
                InventoryContract.COLUMN_ALIAS_SUM +
                " FROM " + SalesEntry.TABLE_NAME +
                " GROUP BY" +
                " YEAR(`" + SalesEntry.COLUMN_CREATED_ON + "`)" +
                " ORDER BY YEAR(`" + SalesEntry.COLUMN_CREATED_ON + "`) DESC;";

        double yearlySales = 0;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_YEARLY_SALES)
        ) {

            if (rs.next()) {
                yearlySales = rs.getDouble(1);
            }

        } catch (SQLException exception) {
            System.out.println(exception);
            yearlySales = 0;
        }

        return yearlySales;
    }

    public double getMonthlyRevenue (int year, int month) {
        final String SQL_MONTHLY_REVENUE = "SELECT " +
                "SUM(`" + SalesEntry.COLUMN_SALE_PRICE +"`) AS " +
                InventoryContract.COLUMN_ALIAS_SUM +
                " FROM " + SalesEntry.TABLE_NAME +
                " WHERE YEAR(`" + SalesEntry.COLUMN_CREATED_ON + "`) = " + year +
                " AND MONTH(`" + SalesEntry.COLUMN_CREATED_ON + "`) = " + month +
                " GROUP BY" +
                " YEAR(`" + SalesEntry.COLUMN_CREATED_ON + "`)," +
                " MONTH(`" + SalesEntry.COLUMN_CREATED_ON + "`);";

        double monthlyRevenue = 0;
        ResultSet rs = null;
        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SQL_MONTHLY_REVENUE)
        ) {

//            pstmt.setInt(1, year);
//            pstmt.setInt(2, month);
            rs = pstmt.executeQuery(SQL_MONTHLY_REVENUE);
            if (rs.next()) {
                monthlyRevenue = rs.getDouble(1);
            }

        } catch (SQLException exception) {
            System.out.println(exception);
            monthlyRevenue = 0;
        } finally {
            try {
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException exception) {}
        }

        return monthlyRevenue;
    }

    public ObservableList<Top5SalesModel> top5SalesModels (int year, int month) {
        String SQL_TOP5_SALES = "";
        if (month > -1) {
            SQL_TOP5_SALES = "SELECT SUM(" +
                    SalesEntry.COLUMN_QUANTITY + ") AS " +
                    InventoryContract.COLUMN_ALIAS_TOTAL + ", " +
                    SalesEntry.COLUMN_ITEM_NAME + " FROM " +
                    SalesEntry.TABLE_NAME + " WHERE " +
                    "YEAR(" + SalesEntry.COLUMN_CREATED_ON + ") = ? AND " +
                    "MONTH(" + SalesEntry.COLUMN_CREATED_ON + ") = ? " +
                    "GROUP BY " + SalesEntry.COLUMN_ITEM_NAME +
                    " ORDER BY " + InventoryContract.COLUMN_ALIAS_TOTAL + " DESC LIMIT 5; ";
        } else {
            SQL_TOP5_SALES = "SELECT SUM(" +
                    SalesEntry.COLUMN_QUANTITY + ") AS " +
                    InventoryContract.COLUMN_ALIAS_TOTAL + ", " +
                    SalesEntry.COLUMN_ITEM_NAME + " FROM " +
                    SalesEntry.TABLE_NAME + " WHERE " +
                    "YEAR(" + SalesEntry.COLUMN_CREATED_ON + ") = ? " +
                    "GROUP BY " + SalesEntry.COLUMN_ITEM_NAME +
                    " ORDER BY " + InventoryContract.COLUMN_ALIAS_TOTAL + " DESC LIMIT 5; ";
        }

        ObservableList<Top5SalesModel> top5SalesModels = FXCollections.observableArrayList();
        ResultSet rs = null;
        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_TOP5_SALES)
            ){

            if (month > -1) {
                pstm.setInt(1, year);
                pstm.setInt(2, month);
            } else {
                pstm.setInt(1, year);
            }

            int sn = 0;
            rs = pstm.executeQuery();

            while (rs.next()) {
                top5SalesModels.addAll(
                    new Top5SalesModel( ++sn,
                            rs.getInt(InventoryContract.COLUMN_ALIAS_TOTAL),
                            rs.getString(SalesEntry.COLUMN_ITEM_NAME)
                    )
                );
            }
        } catch (SQLException exception) {
            System.out.println(exception);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException exception) {}
        }

        return top5SalesModels;
    }
}
