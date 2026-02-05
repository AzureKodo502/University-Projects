module com.example.progetto_ecommerce_v3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires spring.security.crypto;
    requires java.desktop;
    requires javafx.graphics;
    requires org.xerial.sqlitejdbc;
    requires java.net.http;

    opens com.example.progetto_ecommerce_v3 to javafx.fxml;
    exports com.example.progetto_ecommerce_v3;

    opens com.example.progetto_ecommerce_v3.Controller to javafx.fxml;
    exports com.example.progetto_ecommerce_v3.Controller;
    exports com.example.progetto_ecommerce_v3.View;
    opens com.example.progetto_ecommerce_v3.View to javafx.fxml;

}