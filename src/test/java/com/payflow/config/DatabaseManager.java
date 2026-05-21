package com.payflow.config;

import java.sql.*;

public class DatabaseManager {

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                ConfigManager.get("db.url"),
                ConfigManager.get("db.user"),
                ConfigManager.get("db.password")
            );
        }
        return connection;
    }

    public static void insertarTransaccion(String testCase, String stripeChargeId,
            int monto, String moneda, String estado, String descripcion) throws SQLException {
        String sql = """
            INSERT INTO transacciones 
            (test_case, stripe_charge_id, monto, moneda, estado, descripcion)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, testCase);
            stmt.setString(2, stripeChargeId);
            stmt.setInt(3, monto);
            stmt.setString(4, moneda);
            stmt.setString(5, estado);
            stmt.setString(6, descripcion);
            stmt.executeUpdate();
        }
    }

    public static ResultSet buscarTransaccion(String stripeChargeId) throws SQLException {
        String sql = "SELECT * FROM transacciones WHERE stripe_charge_id = ?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setString(1, stripeChargeId);
        return stmt.executeQuery();
    }

    public static void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
