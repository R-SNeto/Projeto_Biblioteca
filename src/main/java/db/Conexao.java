package db;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public static Connection getConnection() {

        Dotenv dotenv = Dotenv.load();

        String dbUrl = dotenv.get("DB_URL");
        String dbUser = dotenv.get("DB_USER");
        String dbPass = dotenv.get("DB_PASS");

        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPass);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
