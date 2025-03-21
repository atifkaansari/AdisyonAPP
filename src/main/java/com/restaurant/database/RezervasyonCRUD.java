package com.restaurant.database;

import java.sql.*;

public class RezervasyonCRUD {

    public static void addRezervasyon(String ad, String telefon, int masaNo, String tarih) {
        String sql = "INSERT INTO rezervasyon(ad, telefon, masa_no, tarih) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ad);
            pstmt.setString(2, telefon);
            pstmt.setInt(3, masaNo);
            pstmt.setString(4, tarih);
            pstmt.executeUpdate();
            System.out.println("Rezervasyon yapıldı!");
        } catch (SQLException e) {
            System.out.println("Rezervasyon yapılamadı: " + e.getMessage());
        }
    }

    public static void getRezervasyonlar() {
        String sql = "SELECT * FROM rezervasyon";
        try (Connection conn = DatabaseConnection.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Rezervasyon ID: " + rs.getInt("id") + ", Ad: " + rs.getString("ad") + ", Masa No: " + rs.getInt("masa_no") + ", Tarih: " + rs.getString("tarih"));
            }
        } catch (SQLException e) {
            System.out.println("Rezervasyonlar listelenemedi: " + e.getMessage());
        }
    }
}
