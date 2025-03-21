package com.restaurant.RestaurantSystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.restaurant.database.DatabaseConnection;
import com.restaurant.database.KategoriCRUD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoryPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Kategori Sayfası");

        // Layout: VBox for category management
        VBox vbox = new VBox(15);
        vbox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20px;");

        // Kategori Ekleme
        HBox kategoriEkleBox = new HBox(10);
        TextField kategoriAdField = new TextField();
        kategoriAdField.setPromptText("Kategori Adı");
        Button kategoriEkleButton = new Button("Kategori Ekle");
        kategoriEkleButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        kategoriEkleButton.setOnAction(e -> {
            String kategoriAd = kategoriAdField.getText();
            KategoriCRUD.addKategori(kategoriAd);
            kategoriAdField.clear();
        });

        kategoriEkleBox.getChildren().addAll(kategoriAdField, kategoriEkleButton);
        
        // Kategori Listeleme
        ListView<String> kategoriList = new ListView<>();
        Button kategoriListeleButton = new Button("Kategorileri Listele");
        kategoriListeleButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        kategoriListeleButton.setOnAction(e -> {
            kategoriList.getItems().clear(); // Önceki öğeleri temizle
            List<String> kategoriler = KategoriCRUD.getKategoriler(); // Veritabanından kategoriler alın
            kategoriList.getItems().addAll(kategoriler); // Kategorileri ListView'a ekle
        });

        // Kategori Seçimi - Seçilen Kategori Adını Düzenlemek
        TextField kategoriAdDuzenleField = new TextField();
        kategoriAdDuzenleField.setPromptText("Yeni Kategori Adı");
        Button kategoriDuzenleButton = new Button("Kategori Düzenle");
        kategoriDuzenleButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");

        kategoriDuzenleButton.setOnAction(e -> {
            String selectedKategori = kategoriList.getSelectionModel().getSelectedItem();
            if (selectedKategori != null) {
                String yeniKategoriAd = kategoriAdDuzenleField.getText();
            	int kategoriId = Integer.parseInt(selectedKategori.split(" - ")[0]);

                // Kategori adını güncelle
                if (!yeniKategoriAd.isEmpty()) {
                    KategoriCRUD.updateKategori(kategoriId, yeniKategoriAd);
                    kategoriAdDuzenleField.clear();
                    
                    // Listeyi yeniden güncelle
                    kategoriList.getItems().clear();
                    kategoriList.getItems().addAll(KategoriCRUD.getKategoriler());
                    
                    // Kullanıcıya bilgi ver
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Kategori başarıyla güncellendi!", ButtonType.OK);
                    alert.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Kategori adı boş olamaz!", ButtonType.OK);
                    alert.show();
                }
            }
        });

        // Kategori Silme
        Button deleteKategoriButton = new Button("Kategori Sil");
        deleteKategoriButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteKategoriButton.setOnAction(e -> {
            String selectedKategori = kategoriList.getSelectionModel().getSelectedItem();
            if (selectedKategori != null) {
            	int kategoriId = Integer.parseInt(selectedKategori.split(" - ")[0]); // ID'yi al
            	String kategoriAd = selectedKategori.split(" - ")[1]; // Kategori adını al
                if (kategoriId != -1) {
                    // Kategori silme işlemi
                    KategoriCRUD.deleteKategori(kategoriId);

                    // Silinen kategoriyi ListView'den kaldır
                    kategoriList.getItems().remove(selectedKategori);

                    // Kullanıcıya bilgi ver
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Kategori başarıyla silindi!", ButtonType.OK);
                    alert.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Kategori bulunamadı, silme işlemi başarısız!", ButtonType.OK);
                    alert.show();
                }
            }
        });

        vbox.getChildren().addAll(
            new Label("Kategori Ekle"), 
            kategoriEkleBox, 
            kategoriListeleButton, 
            kategoriList, 
            kategoriAdDuzenleField, 
            kategoriDuzenleButton, 
            deleteKategoriButton
        );

        // Menüye Dön Button
        Button backButton = new Button("Menüye Dön");
        backButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            Main mainPage = new Main();
            mainPage.start(primaryStage);
        });

        vbox.getChildren().add(backButton);

        // Scene ve Stage
        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void showUrunler(List<String> urunler) {
        // Ürünler ekranı
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ürünler");
        alert.setHeaderText("Kategoriye Ait Ürünler");
        alert.setContentText(String.join("\n", urunler));
        alert.showAndWait();
    }

    
    public static void main(String[] args) {
        launch(args);
    }
}
