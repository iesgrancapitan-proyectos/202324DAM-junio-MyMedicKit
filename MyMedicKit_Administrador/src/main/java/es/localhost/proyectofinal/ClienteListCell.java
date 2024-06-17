package es.localhost.proyectofinal;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ClienteListCell extends ListCell<Cliente> {
    private final Label nombreLabel = new Label();
    private final Label emailLabel = new Label();
    private final Label newMessageIndicator = new Label("⚠");
    private final HBox hbox = new HBox();

    public ClienteListCell() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT);
        nombreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        emailLabel.setStyle("-fx-font-size: 12px;");
        newMessageIndicator.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        newMessageIndicator.setVisible(false);

        vbox.getChildren().addAll(nombreLabel, emailLabel);
        hbox.getChildren().addAll(vbox, newMessageIndicator);
        hbox.setSpacing(10);
    }

    @Override
    protected void updateItem(Cliente cliente, boolean empty) {
        super.updateItem(cliente, empty);
        if (empty || cliente == null) {
            setText(null);
            setGraphic(null);
        } else {
            nombreLabel.setText(cliente.getNombre() + " " + cliente.getApellidos());
            emailLabel.setText(cliente.getEmail());

            //mostrar indicador si el admin tiene nuevos mensajes
            newMessageIndicator.setVisible(cliente.hasNewMessage());

            setGraphic(hbox);
        }
    }
}
