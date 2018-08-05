package inventory_management.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Kh3m on 7/4/2018.
 */
public class ConnectionUtil {

    private static ConnectionUtil instance;

    private ConnectionUtil () {}

    public static ConnectionUtil getInstance () {
        if (instance == null) {
            instance = new ConnectionUtil();
        }
        return instance;
    }

    public Connection getConnection () throws SQLException {
        Connection conn = null;

        try (FileReader fileReader = new FileReader("C:\\Users\\Kh3m\\IdeaProjects\\InventoryStockManagementSystem\\src\\resources\\properties\\db.properties");)
        {
            Properties properties = new Properties();
            properties.load(fileReader);
            String url = properties.getProperty("databaseURL");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");
            conn = DriverManager.getConnection(url,user,password);

        } catch (IOException exception) {
            System.out.println("Connection Failed Check Code For: " + exception.getMessage());
        }

        return conn;
    }
}
