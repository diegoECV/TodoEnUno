package vallegrande.edu.pe.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
private static final String URL = "jdbc:mysql://localhost:3306/dbclientes";
private static final String USER = "root";
private static final String PASSWORD = "diego12345";

static {
        try  {
        Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        System.err.println("MySQL JDBC Driver not found. Make sure the JAR is in your classpath.");
            e.printStackTrace();
            throw new RuntimeException("Failed to load JDBC driver", e);
        }
                }

public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
            }
