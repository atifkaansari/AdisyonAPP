package com.restaurant.RestaurantSystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.restaurant.database.UrunCRUD;
import com.restaurant.database.KategoriCRUD;

import java.util.List;

public class ProductPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ürün Sayfası");

        // Ana Layout (VBox)
        VBox vbox = new VBox(15);
        vbox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20px;");

        // **Ürün Ekleme Bölümü**
        HBox urunEkleBox = new HBox(10);
        TextField urunAdField = new TextField();
        urunAdField.setPromptText("Ürün Adı");

        TextField urunFiyatField = new TextField();
        urunFiyatField.setPromptText("Fiyat");

        ComboBox<String> kategoriComboBox = new ComboBox<>();
        kategoriComboBox.setPromptText("Kategori Seç");

        // Kategorileri ComboBox'a Yükleyelim
        List<String> kategoriler = KategoriCRUD.getKategoriler();
        kategoriComboBox.getItems().addAll(kategoriler);

        // Ürün Ekle Butonu
        Button urunEkleButton = new Button("Ürün Ekle");
        urunEkleButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        urunEkleButton.setOnAction(e -> {
            try {
                String urunAd = urunAdField.getText();
                double urunFiyat = Double.parseDouble(urunFiyatField.getText());
                String selectedKategori = kategoriComboBox.getSelectionModel().getSelectedItem();

                if (urunAd.isEmpty() || selectedKategori == null) {
                    showAlert(Alert.AlertType.WARNING, "Hata", "Lütfen tüm alanları doldurun!");
                    return;
                }

                int kategoriId = Integer.parseInt(selectedKategori.split(" - ")[0]); // ID al
                UrunCRUD.addUrun(urunAd, kategoriId, urunFiyat);

                // Alanları Temizleyelim
                urunAdField.clear();
                urunFiyatField.clear();
                kategoriComboBox.getSelectionModel().clearSelection();

                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Ürün eklendi!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Geçersiz giriş!");
            }
        });

        urunEkleBox.getChildren().addAll(urunAdField, urunFiyatField, kategoriComboBox, urunEkleButton);

        // **Ürün Listeleme Bölümü**
        ComboBox<String> kategoriFilterComboBox = new ComboBox<>();
        kategoriFilterComboBox.setPromptText("Kategori Seç");
        kategoriFilterComboBox.getItems().addAll(KategoriCRUD.getKategoriler()); // Kategorileri ekle

        Button urunListeleButton = new Button("Ürünleri Listele");
        urunListeleButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        ListView<String> urunList = new ListView<>();

        urunListeleButton.setOnAction(e -> {
            urunList.getItems().clear();
            String selectedKategori = kategoriFilterComboBox.getSelectionModel().getSelectedItem();

            if (selectedKategori != null) {
                int kategoriId = Integer.parseInt(selectedKategori.split(" - ")[0]);
                List<String> urunler = UrunCRUD.getUrunlerByKategori(kategoriId);
                urunList.getItems().addAll(urunler);
            } else {
                showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen bir kategori seçin!");
            }
        });

        // Ürün Silme Butonu
        Button deleteUrunButton = new Button("Ürün Sil");
        deleteUrunButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteUrunButton.setOnAction(e -> {
            String selectedUrun = urunList.getSelectionModel().getSelectedItem();
            if (selectedUrun != null) {
                int urunId = Integer.parseInt(selectedUrun.split(" - ")[0]);
                UrunCRUD.deleteUrun(urunId);
                urunList.getItems().remove(selectedUrun);
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Ürün silindi!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Hata", "Lütfen bir ürün seçin!");
            }
        });

        // Ürün Güncelleme Butonu
        Button updateUrunButton = new Button("Ürün Güncelle");
        updateUrunButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white;");
        updateUrunButton.setOnAction(e -> {
            String selectedUrun = urunList.getSelectionModel().getSelectedItem();
            if (selectedUrun != null) {
                int urunId = Integer.parseInt(selectedUrun.split(" - ")[0]);
                openUpdateWindow(urunId, urunList);
            } else {
                showAlert(Alert.AlertType.WARNING, "Hata", "Lütfen güncellenecek ürünü seçin!");
            }
        });

        // **Menüye Dön Butonu**
        Button backButton = new Button("Menüye Dön");
        backButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            Main mainPage = new Main();
            mainPage.start(primaryStage);
        });

        // **Layout'a Elemanları Ekleyelim**
        vbox.getChildren().addAll(
                new Label("Ürün Ekle"), urunEkleBox,
                new Label("Ürünleri Listele"), kategoriFilterComboBox, urunListeleButton, urunList,
                deleteUrunButton, updateUrunButton, backButton
        );

        Scene scene = new Scene(vbox, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Güncelleme Penceresini Açma
     */
    private void openUpdateWindow(int urunId, ListView<String> urunList) {
        Stage updateStage = new Stage();
        updateStage.setTitle("Ürün Güncelle");

        VBox updateVBox = new VBox(10);
        updateVBox.setStyle("-fx-padding: 20px;");

        TextField adField = new TextField();
        adField.setPromptText("Yeni Ürün Adı");

        TextField fiyatField = new TextField();
        fiyatField.setPromptText("Yeni Fiyat");

        ComboBox<String> kategoriComboBox = new ComboBox<>();
        kategoriComboBox.setPromptText("Yeni Kategori Seç");
        kategoriComboBox.getItems().addAll(KategoriCRUD.getKategoriler());

        Button guncelleButton = new Button("Güncelle");
        guncelleButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white;");

        guncelleButton.setOnAction(event -> {
            try {
                String yeniAd = adField.getText();
                double yeniFiyat = Double.parseDouble(fiyatField.getText());
                int yeniKategoriId = Integer.parseInt(kategoriComboBox.getSelectionModel().getSelectedItem().split(" - ")[0]);

                UrunCRUD.updateUrun(urunId, yeniAd, yeniKategoriId, yeniFiyat);
                updateStage.close();
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Ürün güncellendi!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Geçersiz giriş!");
            }
        });

        updateVBox.getChildren().addAll(adField, fiyatField, kategoriComboBox, guncelleButton);
        Scene updateScene = new Scene(updateVBox, 300, 250);
        updateStage.setScene(updateScene);
        updateStage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type, content, ButtonType.OK);
        alert.setTitle(title);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
