package com.grdvp.config;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    
    private static Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            
            if (input == null) {
                // System.err.println("❌ Fichier config.properties introuvable !");
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            // System.out.println("✅ Connexion réussie à PostgreSQL !");
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trouvé", e);
        }
    }
    
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                // System.out.println("🔒 Connexion fermée.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}