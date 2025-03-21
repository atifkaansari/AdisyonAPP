package com.restaurant.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:restaurant.db";

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            if (conn != null) {
                System.out.println("Bağlantı başarılı!");
            }
            return conn;
        } catch (SQLException e) {
            System.out.println("Bağlantı hatası: " + e.getMessage());
            return null;
        }
    }

    public static void createTables() {
        String masalarTable = "CREATE TABLE IF NOT EXISTS masalar (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "status TEXT NOT NULL)";

        String kategorilerTable = "CREATE TABLE IF NOT EXISTS kategoriler (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ad TEXT NOT NULL UNIQUE)";

        String urunlerTable = "CREATE TABLE IF NOT EXISTS urunler (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ad TEXT NOT NULL, " +
                "kategori_id INTEGER, " +
                "fiyat REAL NOT NULL, " +
                "FOREIGN KEY(kategori_id) REFERENCES kategoriler(id))";

        String adisyonTable = "CREATE TABLE IF NOT EXISTS adisyon (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "masa_no INTEGER NOT NULL, " +
                "toplam_tutar REAL NOT NULL, " +
                "tarih TEXT DEFAULT CURRENT_TIMESTAMP," +
                "odeme TEXT NOT NULL)";

        String adisyonUrunleriTable = "CREATE TABLE IF NOT EXISTS adisyon_urunleri (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "adisyon_id INTEGER NOT NULL, " +
                "urun_id INTEGER NOT NULL, " +
                "urun_adet INTEGER NOT NULL, " +
                "fiyat REAL NOT NULL, " +
                "FOREIGN KEY(adisyon_id) REFERENCES adisyon(id), " +
                "FOREIGN KEY(urun_id) REFERENCES urunler(id))";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(masalarTable);
            stmt.execute(kategorilerTable);
            stmt.execute(urunlerTable);
            stmt.execute(adisyonTable);
            stmt.execute(adisyonUrunleriTable);
            System.out.println("Tablolar oluşturuldu.");
        } catch (SQLException e) {
            System.out.println("Tablo oluşturma hatası: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}
