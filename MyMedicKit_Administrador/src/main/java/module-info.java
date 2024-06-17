module es.localhost.proyectofinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;

    opens es.localhost.proyectofinal to javafx.fxml;
    exports es.localhost.proyectofinal;
}
