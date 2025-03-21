package com.restaurant.RestaurantSystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.restaurant.database.MasaCRUD;

public class MasaPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Masa Sayfası");

        // Layout: VBox for table management
        VBox vbox = new VBox(15);
        vbox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20px;");

        // Masaları Yükleme
        ListView<String> masaList = new ListView<>();
        Button loadTablesButton = new Button("Masaları Yükle");
        loadTablesButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        loadTablesButton.setOnAction(e -> {
            masaList.getItems().clear();
            masaList.getItems().addAll(MasaCRUD.getMasalar()); // Masaları listele
        });

        // Yeni Masa Ekleme
        Button addTableButton = new Button("Yeni Masa Ekle");
        addTableButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addTableButton.setOnAction(e -> {
            // Yeni bir boş masa ekle
            MasaCRUD.addMasa("Boş");
            loadTablesButton.fire(); // Masaları yeniden yükle
        });
/*
        // Masa Seçimi - Çift Tıklama
        masaList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) { // Çift tıklama
                String selectedMasa = masaList.getSelectionModel().getSelectedItem();
                if (selectedMasa != null) {
                    String masaStatus = selectedMasa.split(" - ")[1]; // "Masa N - Dolu" formatında olacak
                    int masaId = Integer.parseInt(selectedMasa.split(" ")[1]); // Masa id'sini al

                    if (masaStatus.equals("Boş")) {
                        // Boş masa seçildiğinde Adisyon sayfasına yönlendir
                        BillPage billPage = new BillPage();
                        billPage.start(primaryStage);
                    } else {
                        // Masa doluysa kullanıcıya uyarı ver
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Bu masa dolu!", ButtonType.OK);
                        alert.show();
                    }
                }
            }
        });
*/
        // Masanın Durumunu Değiştirme (Boş/Dolu)
        Button updateMasaStatusButton = new Button("Masaların Durumunu Değiştir");
        updateMasaStatusButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        updateMasaStatusButton.setOnAction(e -> {
            String selectedMasa = masaList.getSelectionModel().getSelectedItem();
            if (selectedMasa != null) {
                int masaId = Integer.parseInt(selectedMasa.split(" ")[1]); // Masa id'sini al
                String masaStatus = selectedMasa.split(" - ")[1]; // "Boş" ya da "Dolu"
                if (masaStatus.equals("Boş")) {
                    MasaCRUD.updateMasaStatus(masaId, "Dolu");
                } else {
                    MasaCRUD.updateMasaStatus(masaId, "Boş");
                }
                loadTablesButton.fire(); // Masaları yeniden yükle
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Lütfen bir masa seçin!");
                alert.show();
            }
        });
        
        Button backButton = new Button("Menüye Dön");
        backButton.setOnAction(e -> {
            Main mainPage = new Main();
            mainPage.start(primaryStage);
        });


        // Scene ve Stage
        vbox.getChildren().addAll(loadTablesButton, addTableButton, masaList, updateMasaStatusButton, backButton);

        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
