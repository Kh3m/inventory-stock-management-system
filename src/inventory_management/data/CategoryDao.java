package inventory_management.data;

import inventory_management.model.CategoryModel;
import inventory_management.data.InventoryContract.CategoryEntry;
import inventory_management.utils.ConnectionUtil;
import inventory_management.utils.UserDataUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Kh3m on 7/4/2018
 */
public class CategoryDao {
    EventDao eventDao;

    public CategoryDao () {
        eventDao = new EventDao();
    }

    public ObservableList<CategoryModel> getAllCategory () {
        final String SQL_QUERY_ALL_CATEGORY = "SELECT " +
                CategoryEntry._ID + ", " +
                CategoryEntry.COLUMN_CATEGORY_NAME + ", " +
                CategoryEntry.COLUMN_CATEGORY_DESCRIPTION +
                " FROM " + CategoryEntry.TABLE_NAME;

        ObservableList<CategoryModel> categoryModels = FXCollections.observableArrayList();

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_QUERY_ALL_CATEGORY);
                ResultSet rs = pstm.executeQuery()
        ) {

            while (rs.next()) {
                categoryModels.add(
                        new CategoryModel(rs.getInt(CategoryEntry._ID),
                            rs.getString(CategoryEntry.COLUMN_CATEGORY_NAME),
                            rs.getString(CategoryEntry.COLUMN_CATEGORY_DESCRIPTION)));
            }

        } catch (SQLException exception) {
            System.out.println("Failed to load all category: " + exception);
        }

        return categoryModels;
    }

    public ObservableList<String> getCategoryNames () {
        ObservableList<String> categoryNames = FXCollections.observableArrayList();
        ObservableList<CategoryModel> categoryModels = FXCollections.observableArrayList(getAllCategory());

        for (int i = 0; i < categoryModels.size(); i++) {
            categoryNames.add(categoryModels.get(i).getCategory());
        }

        return categoryNames;
    }

    public int getCategoryIdByCategoryName (String categoryName) {
        final String SQL_FIND_CATEGORY_ID = "SELECT " +
                CategoryEntry._ID + " FROM " +
                CategoryEntry.TABLE_NAME + " WHERE " +
                CategoryEntry.COLUMN_CATEGORY_NAME + " = ?";

        System.out.println(SQL_FIND_CATEGORY_ID);

        int id;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_FIND_CATEGORY_ID);

            ) {

            pstm.setString(1, categoryName);
            ResultSet rs = pstm.executeQuery();
            rs.next();
            id = rs.getInt(CategoryEntry._ID);

            return id;

        } catch (SQLException exception) {
            id = -1;
        }

        return id;
    }

    public String getCategoryById(int id) {
        String SQL_FIND_ITEM_ID = "SELECT " +
                CategoryEntry.COLUMN_CATEGORY_NAME + " FROM " +
                CategoryEntry.TABLE_NAME + " WHERE " +
                CategoryEntry._ID + " = ?; ";

        String categoryName;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_FIND_ITEM_ID)
        ) {

            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            rs.next();
            categoryName = rs.getString(CategoryEntry.COLUMN_CATEGORY_NAME);

        } catch (SQLException exception) {
            categoryName = null;
        }
        return categoryName;
    }

    public int insertCategory (String category, String description) {
        final String SQL_INSERT_CATEGORY = "INSERT INTO " +
                CategoryEntry.TABLE_NAME + " ( " +
                CategoryEntry.COLUMN_CATEGORY_NAME + ", " +
                CategoryEntry.COLUMN_CATEGORY_DESCRIPTION + " ) " +
                "VALUES (?, ?)";

        int rowsAffected;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_INSERT_CATEGORY)
        ) {

            pstm.setString(1, category);
            pstm.setString(2, description);

            rowsAffected = pstm.executeUpdate();

            if (rowsAffected == 1) {
                eventDao.insertEvent(UserDataUtil.getUserModel().getUserId(), "Insert",
                        "Insert " + category + " to existing categories");
            }

        } catch (SQLException exception) {
            System.out.println("Failed to insert category: " + exception);
            rowsAffected = 0;
        }

        return rowsAffected;
    }

    public int updateCategory (int _id, String category, String description) {

        final String SQL_UPDATE_CATEGORY = "UPDATE " +
                CategoryEntry.TABLE_NAME + " SET " +
                CategoryEntry.COLUMN_CATEGORY_NAME + " = ?" + ", " +
                CategoryEntry.COLUMN_CATEGORY_DESCRIPTION + " = ?" +
                " WHERE " + CategoryEntry._ID + " = ?";
        int rowsAffected;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_UPDATE_CATEGORY)
        ) {

            pstm.setString(1, category);
            pstm.setString(2, description);
            pstm.setInt(3, _id);

            rowsAffected = pstm.executeUpdate();

            if (rowsAffected == 1) {
                eventDao.insertEvent(UserDataUtil.getUserModel().getUserId(), "Insert",
                        "Update category ");
            }

        } catch (SQLException exception) {
            rowsAffected = 0;
        }

        return rowsAffected;
    }

    public int deleteCategory (int _id) {

        final String SQL_DELETE_CATEGORY = "DELETE FROM " +
                CategoryEntry.TABLE_NAME + " WHERE "  + CategoryEntry._ID + " = ?";

        int rowsAffected;

        try (
                Connection conn = ConnectionUtil.getInstance().getConnection();
                PreparedStatement pstm = conn.prepareStatement(SQL_DELETE_CATEGORY);
            ){

            eventDao.insertEvent(UserDataUtil.getUserModel().getUserId(),
                    "Delete",
                    "Delete " + getCategoryById(_id) + " from category");
            pstm.setInt(1, _id);
            rowsAffected = pstm.executeUpdate();

        } catch (SQLException exception) {
            rowsAffected = 0;
        }

        return rowsAffected;
    }
}
