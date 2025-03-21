package com.restaurant.database;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AdisyonCRUD {

    // Masaları Getirme
    public static List<String> getMasalar() {
        List<String> masalar = new ArrayList<>();
        String sql = "SELECT id FROM masalar WHERE status = 'Dolu'";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                masalar.add("Masa " + rs.getInt("id"));
            }

            if (masalar.isEmpty()) {
                System.out.println("Dolu masa bulunamadı!");
            }

        } catch (SQLException e) {
            System.out.println("Masalar alınamadı: " + e.getMessage());
        }

        return masalar;
    }

    // Kategorileri Getirme
    public static List<String> getKategoriler() {
        List<String> kategoriler = new ArrayList<>();
        String sql = "SELECT ad FROM kategoriler";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                kategoriler.add(rs.getString("ad"));
            }
        } catch (SQLException e) {
            System.out.println("Kategoriler alınamadı: " + e.getMessage());
        }
        return kategoriler;
    }

    // Seçilen Kategorinin Ürünlerini Getirme
    public static List<String> getUrunlerByKategori(String kategoriAd) {
        List<String> urunler = new ArrayList<>();
        String sql = "SELECT ad FROM urunler WHERE kategori_id = (SELECT id FROM kategoriler WHERE ad = ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kategoriAd);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                urunler.add(rs.getString("ad"));
            }
        } catch (SQLException e) {
            System.out.println("Ürünler alınamadı: " + e.getMessage());
        }
        return urunler;
    }
    
    
    
    public static void adisyonaUrunEkle(String masaNo, String urunAd, int adet) {
        String checkAdisyonSql = "SELECT id FROM adisyon WHERE masa_no = ? AND odeme = 'Ödenmedi' LIMIT 1";
        String insertUrunSql = "INSERT INTO adisyon_urunleri (adisyon_id, urun_id, urun_adet, fiyat) " +
                               "VALUES (?, (SELECT id FROM urunler WHERE ad = ?), ?, (SELECT fiyat FROM urunler WHERE ad = ?) * ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkAdisyonSql);
             PreparedStatement insertUrunStmt = conn.prepareStatement(insertUrunSql)) {

            // Adisyon ID'sini kontrol et
            checkStmt.setInt(1, Integer.parseInt(masaNo.replace("Masa ", "")));
            ResultSet rs = checkStmt.executeQuery();

            int adisyonId;
            if (rs.next()) {
                adisyonId = rs.getInt("id");
            } else {
                // Eğer aktif adisyon yoksa, yeni adisyon başlatılıyor
                adisyonaBaslat(masaNo); // Adisyon başlatılacak
                adisyonId = getAdisyonIdForMasa(masaNo); // Yeni adisyon ID'sini al
            }

            // Ürünü adisyona ekle
            insertUrunStmt.setInt(1, adisyonId);
            insertUrunStmt.setString(2, urunAd);
            insertUrunStmt.setInt(3, adet);
            insertUrunStmt.setString(4, urunAd);
            insertUrunStmt.setInt(5, adet);
            insertUrunStmt.executeUpdate();
            System.out.println("Ürün başarıyla eklendi!");

        } catch (SQLException e) {
            System.out.println("Ürün eklenemedi: " + e.getMessage());
        }
    }

    private static int getAdisyonIdForMasa(String masaNo) {
        String sql = "SELECT id FROM adisyon WHERE masa_no = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(masaNo.replace("Masa ", "")));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Adisyon ID alınamadı: " + e.getMessage());
        }
        return -1; // Eğer adisyon bulunamazsa
    }


    
    
    public static void adisyonaBaslat(String masaNo) {
        String insertAdisyonSql = "INSERT INTO adisyon (masa_no, toplam_tutar, odeme) VALUES (?, 0, 'Ödenmedi')";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertAdisyonSql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, Integer.parseInt(masaNo.replace("Masa ", "")));
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int adisyonId = generatedKeys.getInt(1);
                System.out.println("Yeni adisyon oluşturuldu. Adisyon ID: " + adisyonId);
            }
        } catch (SQLException e) {
            System.out.println("Yeni adisyon oluşturulamadı: " + e.getMessage());
        }
    }

    




    public static void yazdirAdisyon(String masaNo) {
        String sql = "SELECT u.ad, a.urun_adet, a.fiyat FROM adisyon_urunleri a " +
                     "JOIN urunler u ON a.urun_id = u.id " +
                     "WHERE a.adisyon_id = (SELECT id FROM adisyon WHERE masa_no = ? AND odeme = 'Ödenmedi' LIMIT 1)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(masaNo.replace("Masa ", "")));
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Adisyon Detayları:");

            // **Boş adisyon kontrolü**
            if (!rs.isBeforeFirst()) { 
                System.out.println("Bu masanın adisyonu bulunamadı. Yeni adisyon oluşturulacak.");
                adisyonaBaslat(masaNo); // Yeni adisyon başlat
                return;
            }

            // **PDF oluşturma**
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            PDType0Font font = PDType0Font.load(document, new File("C:/Windows/Fonts/arial.ttf"));
            contentStream.setFont(font, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Adisyon Detayları:");
            contentStream.endText();

            int y = 680;
            double toplamTutar = 0;

            while (rs.next()) {
                String urunAdi = rs.getString("ad");
                int adet = rs.getInt("urun_adet");
                double fiyat = rs.getDouble("fiyat");
                toplamTutar += fiyat;

                System.out.println(urunAdi + " x " + adet + " - " + fiyat + " TL");

                contentStream.beginText();
                contentStream.newLineAtOffset(100, y);
                contentStream.showText(urunAdi + " - " + adet + " adet - " + fiyat + " TL");
                contentStream.endText();
                y -= 20;
            }

            // **Toplam tutarı ekle**
            contentStream.beginText();
            contentStream.newLineAtOffset(100, y - 20);
            contentStream.showText("------------------------------");
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(100, y - 40);
            contentStream.showText("Toplam: " + toplamTutar + " TL");
            contentStream.endText();

            contentStream.close();

            String pdfPath = "C:\\Users\\kaans\\OneDrive\\Desktop/adisyon_" + masaNo.replace("Masa ", "") + ".pdf";
            document.save(pdfPath);
            document.close();

            System.out.println("Adisyon PDF olarak yazdırıldı!");

            // **Masa kapanacak, ancak ödeme 'yapılmadı' olarak kalacak**
            String masaKapatmaSql = "UPDATE masalar SET status = 'Boş' WHERE id = ?";
            try (PreparedStatement updateMasaStmt = conn.prepareStatement(masaKapatmaSql)) {
                updateMasaStmt.setInt(1, Integer.parseInt(masaNo.replace("Masa ", "")));
                updateMasaStmt.executeUpdate();
            }

            String updateOdemeSql = "UPDATE adisyon SET odeme = 'Yapılmadı' WHERE masa_no = ?";
            try (PreparedStatement updateOdemeStmt = conn.prepareStatement(updateOdemeSql)) {
                updateOdemeStmt.setInt(1, Integer.parseInt(masaNo.replace("Masa ", "")));
                updateOdemeStmt.executeUpdate();
            }

            System.out.println("Masa kapandı, ödeme durumu 'yapılmadı' olarak kaldı.");

        } catch (IOException | SQLException e) {
            System.out.println("PDF oluşturulamadı: " + e.getMessage());
        }
    }


}
