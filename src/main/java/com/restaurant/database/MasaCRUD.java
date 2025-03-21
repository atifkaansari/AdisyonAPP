package com.restaurant.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MasaCRUD {

    public static List<String> getMasalar() {
        List<String> masalar = new ArrayList<>();
        String sql = "SELECT * FROM masalar";

        try (Connection conn = DatabaseConnection.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int masaId = rs.getInt("id");
                String status = rs.getString("status");
                masalar.add("Masa " + masaId + " - " + status); // Masa N - Boş/Dolu
            }
        } catch (SQLException e) {
            System.out.println("Masalar listelenemedi: " + e.getMessage());
        }

        return masalar;
    }

    public static void updateMasaStatus(int masaId, String status) {
        String sql = "UPDATE masalar SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, masaId);
            pstmt.executeUpdate();
            System.out.println("Masa durumu güncellendi!");
        } catch (SQLException e) {
            System.out.println("Masa durumu güncellenemedi: " + e.getMessage());
        }
    }

    public static void addMasa(String status) {
        String sql = "INSERT INTO masalar(status) VALUES(?)";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.executeUpdate();
            System.out.println("Yeni masa eklendi!");
        } catch (SQLException e) {
            System.out.println("Masa eklenemedi: " + e.getMessage());
        }
    }

    public static void deleteMasa(int masaId) {
        String sql = "DELETE FROM masalar WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, masaId);
            pstmt.executeUpdate();
            System.out.println("Masa silindi!");
        } catch (SQLException e) {
            System.out.println("Masa silinemedi: " + e.getMessage());
        }
    }
}
