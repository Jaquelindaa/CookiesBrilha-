package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectionFactory {

    private static final String URL = "jdbc:postgresql://localhost:5432/cookiesbrilha";
    private static final String USER = "postgres";
    private static final String PASS = "123456";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC PostgreSQL não encontrado.", e);
        }
    }
}
