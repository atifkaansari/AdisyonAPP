package com.restaurant.RestaurantSystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Restoran Sistemi");

        // Ana Sayfa GridPane
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        gridPane.setStyle("-fx-background-color: #f4f4f4;");

        // Butonlar
        Button categoryPageButton = createStyledButton("📂 Kategoriler");
        categoryPageButton.setOnAction(e -> {
            CategoryPage categoryPage = new CategoryPage();
            categoryPage.start(primaryStage);
        });

        Button productPageButton = createStyledButton("🛒 Ürünler");
        productPageButton.setOnAction(e -> {
            ProductPage productPage = new ProductPage();
            productPage.start(primaryStage);
        });

        Button billPageButton = createStyledButton("🧾 Adisyonlar");
        billPageButton.setOnAction(e -> {
            BillPage billPage = new BillPage();
            billPage.start(primaryStage);
        });

        Button masaPageButton = createStyledButton("🍽 Masalar");
        masaPageButton.setOnAction(e -> {
            MasaPage masaPage = new MasaPage();
            masaPage.start(primaryStage);
        });

        // Butonları GridPane içine yerleştir
        gridPane.add(categoryPageButton, 0, 0);
        gridPane.add(productPageButton, 1, 0);
        gridPane.add(billPageButton, 0, 1);
        gridPane.add(masaPageButton, 1, 1);

        // Scene ve Stage ayarları
        Scene scene = new Scene(gridPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Butonları stilize eden yardımcı metod
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(180, 50);
        button.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10px;"
        );
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
