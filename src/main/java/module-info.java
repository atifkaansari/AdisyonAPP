module restaurantsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
	requires org.apache.pdfbox; 

    opens com.restaurant.RestaurantSystem to javafx.fxml;
    exports com.restaurant.RestaurantSystem;
}
