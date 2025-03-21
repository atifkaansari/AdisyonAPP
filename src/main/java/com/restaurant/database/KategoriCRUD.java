package com.restaurant.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriCRUD {

    // Kategori ekleme
    public static void addKategori(String kategoriAd) {
        String sql = "INSERT INTO kategoriler(ad) VALUES(?)";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kategoriAd);
            pstmt.executeUpdate();
            System.out.println("Kategori eklendi: " + kategoriAd);
        } catch (SQLException e) {
            System.out.println("Kategori eklenemedi: " + e.getMessage());
        }
    }

    // Kategorileri listeleme
    public static List<String> getKategoriler() {
        List<String> kategoriler = new ArrayList<>();
        String sql = "SELECT * FROM kategoriler";
        
        try (Connection conn = DatabaseConnection.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
            	int id = rs.getInt("id");
                String kategori = rs.getString("ad");
                kategoriler.add(id + " - " + kategori);  // Listeye kategori ekle
            }
        } catch (SQLException e) {
            System.out.println("Kategoriler listelenemedi: " + e.getMessage());
        }
        return kategoriler; // Kategorileri döndür
    }


        // Kategori silme
    public static void deleteKategori(int kategoriId) {
        // Kategorinin bağlı olduğu ürünleri kontrol et
        String checkUrunlerSQL = "SELECT COUNT(*) FROM urunler WHERE kategori_id = ?";
        String deleteKategoriSQL = "DELETE FROM kategoriler WHERE id = ?";
        String deleteUrunlerSQL = "DELETE FROM urunler WHERE kategori_id = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            // Bağlantıyı başlat
            conn.setAutoCommit(false);  // Auto-commit'i kapatıyoruz ki işlemleri kontrol edebilelim

            // Ürünler var mı diye kontrol et
            try (PreparedStatement pstmtCheckUrun = conn.prepareStatement(checkUrunlerSQL)) {
                pstmtCheckUrun.setInt(1, kategoriId);
                ResultSet rs = pstmtCheckUrun.executeQuery();
                rs.next();
                int urunCount = rs.getInt(1);

                // Ürünler varsa, önce onları sil
                if (urunCount > 0) {
                    try (PreparedStatement pstmtDeleteUrunler = conn.prepareStatement(deleteUrunlerSQL)) {
                        pstmtDeleteUrunler.setInt(1, kategoriId);
                        pstmtDeleteUrunler.executeUpdate();
                        System.out.println("Ürünler başarıyla silindi.");
                    }
                } else {
                    System.out.println("Kategoriye ait ürün bulunmamaktadır.");
                }

                // Kategoriyi sil
                try (PreparedStatement pstmtDeleteKategori = conn.prepareStatement(deleteKategoriSQL)) {
                    pstmtDeleteKategori.setInt(1, kategoriId);
                    int kategoriSilmeResult = pstmtDeleteKategori.executeUpdate();
                    if (kategoriSilmeResult == 0) {
                        System.out.println("Kategori silinemedi, böyle bir kategori yok.");
                    } else {
                        System.out.println("Kategori başarıyla silindi.");
                    }
                }

                // Eğer her şey başarılıysa commit işlemi yapılır
                conn.commit();  // Veritabanına işlemi kaydediyoruz

            } catch (SQLException e) {
                // Hata durumunda tüm işlemi geri alıyoruz
                conn.rollback();
                System.out.println("Silme işlemi başarısız: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Veritabanı hatası: " + e.getMessage());
        }
    }


    
    
    // Kategori güncelleme
    public static void updateKategori(int kategoriId, String yeniKategoriAd) {
        String sql = "UPDATE kategoriler SET ad = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, yeniKategoriAd);
            pstmt.setInt(2, kategoriId);
            pstmt.executeUpdate();
            System.out.println("Kategori güncellendi.");
        } catch (SQLException e) {
            System.out.println("Kategori güncellenemedi: " + e.getMessage());
        }
    }
}
