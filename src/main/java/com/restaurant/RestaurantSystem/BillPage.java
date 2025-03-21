package com.restaurant.RestaurantSystem;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.restaurant.database.AdisyonCRUD;
import java.util.List;

public class BillPage extends Application {

    private final ObservableList<Urun> urunListesi = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Adisyon Sayfası");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f4f4;");

        Label titleLabel = new Label("Adisyon İşlemleri");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Masa Seçimi
        ComboBox<String> masaComboBox = new ComboBox<>();
        masaComboBox.setPromptText("Masa Seç");
        List<String> masalar = AdisyonCRUD.getMasalar();
        masaComboBox.getItems().addAll(masalar);
        masaComboBox.setPrefWidth(200);

        // Kategori Seçimi
        ComboBox<String> kategoriComboBox = new ComboBox<>();
        kategoriComboBox.setPromptText("Kategori Seç");
        Button loadCategoriesButton = new Button("Kategorileri Yükle");
        loadCategoriesButton.setStyle("-fx-background-color: #5C67F2; -fx-text-fill: white;");

        loadCategoriesButton.setOnAction(e -> {
            List<String> kategoriler = AdisyonCRUD.getKategoriler();
            kategoriComboBox.getItems().clear();
            kategoriComboBox.getItems().addAll(kategoriler);
        });

        // Ürün Seçimi
        ComboBox<String> urunComboBox = new ComboBox<>();
        urunComboBox.setPromptText("Ürün Seç");
        kategoriComboBox.setOnAction(e -> {
            String selectedCategory = kategoriComboBox.getValue();
            if (selectedCategory != null) {
                List<String> urunler = AdisyonCRUD.getUrunlerByKategori(selectedCategory);
                urunComboBox.getItems().clear();
                urunComboBox.getItems().addAll(urunler);
            }
        });

        // Adet Girişi
        TextField adetField = new TextField();
        adetField.setPromptText("Adet girin");
        adetField.setPrefWidth(100);

        // Ürün Listesi Tablosu
        TableView<Urun> urunTable = new TableView<>();
        TableColumn<Urun, String> urunAdColumn = new TableColumn<>("Ürün");
        urunAdColumn.setCellValueFactory(new PropertyValueFactory<>("urunAdi"));
        urunAdColumn.setPrefWidth(200);

        TableColumn<Urun, Integer> adetColumn = new TableColumn<>("Adet");
        adetColumn.setCellValueFactory(new PropertyValueFactory<>("adet"));
        adetColumn.setPrefWidth(80);

        TableColumn<Urun, Void> silColumn = new TableColumn<>("Sil");
        silColumn.setCellFactory(col -> new TableCell<>() {
            private final Button silButton = new Button("❌");
            {
                silButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                silButton.setOnAction(e -> {
                    Urun urun = getTableView().getItems().get(getIndex());
                    urunListesi.remove(urun);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(silButton);
                }
            }
        });

        urunTable.getColumns().addAll(urunAdColumn, adetColumn, silColumn);
        urunTable.setItems(urunListesi);
        urunTable.setPrefHeight(200);

        // Adisyona Ekleme Butonu
        Button urunEkleButton = new Button("Ürün Ekle");
        urunEkleButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        urunEkleButton.setOnAction(e -> {
            String selectedMasa = masaComboBox.getValue();
            String selectedUrun = urunComboBox.getValue();
            String adetStr = adetField.getText();
            
            if (selectedMasa != null && selectedUrun != null && !adetStr.isEmpty()) {
                try {
                    int adet = Integer.parseInt(adetStr);
                    AdisyonCRUD.adisyonaUrunEkle(selectedMasa, selectedUrun, adet);
                    urunListesi.add(new Urun(selectedUrun, adet));
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Geçerli bir adet giriniz!");
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Tüm alanları doldurun!");
                alert.show();
            }
        });

        // Yazdırma Butonu
        Button yazdirButton = new Button("🖨️ Yazdır");
        yazdirButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        yazdirButton.setOnAction(e -> {
            String selectedMasa = masaComboBox.getValue();
            if (selectedMasa != null) {
                AdisyonCRUD.yazdirAdisyon(selectedMasa);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Adisyon PDF olarak yazdırıldı!");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Masa seçiniz!");
                alert.show();
            }
        });

        // Menüye Dön Butonu
        Button backButton = new Button("↩️ Menüye Dön");
        backButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            Main mainPage = new Main();
            mainPage.start(primaryStage);
        });

        // Layout Düzeni
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(masaComboBox, 0, 0);
        gridPane.add(loadCategoriesButton, 1, 0);
        gridPane.add(kategoriComboBox, 0, 1);
        gridPane.add(urunComboBox, 1, 1);
        gridPane.add(adetField, 0, 2);
        gridPane.add(urunEkleButton, 1, 2);
        gridPane.add(urunTable, 0, 3, 2, 1);
        gridPane.add(yazdirButton, 0, 4);
        gridPane.add(backButton, 1, 4);

        root.getChildren().addAll(titleLabel, gridPane);
        Scene scene = new Scene(root, 650, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class Urun {
        private final String urunAdi;
        private final int adet;

        public Urun(String urunAdi, int adet) {
            this.urunAdi = urunAdi;
            this.adet = adet;
        }

        public String getUrunAdi() {
            return urunAdi;
        }

        public int getAdet() {
            return adet;
        }
    }
}
