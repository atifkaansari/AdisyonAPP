package com.restaurant.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UrunCRUD {

    // Ürün ekleme
    public static void addUrun(String urunAd, int kategoriId, double fiyat) {
        String sql = "INSERT INTO urunler(ad, kategori_id, fiyat) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, urunAd);
            pstmt.setInt(2, kategoriId);
            pstmt.setDouble(3, fiyat);
            pstmt.executeUpdate();
            System.out.println("Ürün eklendi: " + urunAd);
        } catch (SQLException e) {
            System.out.println("Ürün eklenemedi: " + e.getMessage());
        }
    }

    // Ürünleri listeleme
    public static List<String> getUrunler() {
        List<String> urunler = new ArrayList<>();
        String sql = "SELECT * FROM urunler";
        
        try (Connection conn = DatabaseConnection.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String urun = rs.getInt("id") + " - " + rs.getString("ad") + " - " + rs.getDouble("fiyat") + " TL";
                urunler.add(urun);
            }
        } catch (SQLException e) {
            System.out.println("Ürünler listelenemedi: " + e.getMessage());
        }
        return urunler; // Ürünleri döndür
    }

    // Ürün silme
    public static void deleteUrun(int urunId) {
        String sql = "DELETE FROM urunler WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, urunId);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("Ürün başarıyla silindi.");
            } else {
                System.out.println("Ürün silinemedi, böyle bir ürün yok.");
            }
        } catch (SQLException e) {
            System.out.println("Ürün silinemedi: " + e.getMessage());
        }
    }

    public static void updateUrun(int urunId, String yeniAd, int kategoriId, double yeniFiyat) {
        String query = "UPDATE urunler SET ad = ?, kategori_id = ?, fiyat = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, yeniAd);
            stmt.setInt(2, kategoriId);
            stmt.setDouble(3, yeniFiyat);
            stmt.setInt(4, urunId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static List<String> getUrunlerByKategori(int kategoriId) {
        List<String> urunler = new ArrayList<>();
        String query = "SELECT id, ad, fiyat FROM urunler WHERE kategori_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, kategoriId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String ad = rs.getString("ad");
                double fiyat = rs.getDouble("fiyat");
                urunler.add(id + " - " + ad + " - " + fiyat + "₺"); // Format: id - ad - fiyat
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return urunler;
    }
}
