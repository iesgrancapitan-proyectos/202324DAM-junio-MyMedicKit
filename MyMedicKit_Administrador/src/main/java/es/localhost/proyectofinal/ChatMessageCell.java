package es.localhost.proyectofinal;

import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ChatMessageCell extends ListCell<ChatMessage> {
    private final HBox hbox = new HBox();
    private final Label messageLabel = new Label();
    private final Region spacer = new Region();

    public ChatMessageCell() {
        super();

        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300); //tamaño maximo
        messageLabel.setStyle("-fx-background-radius: 10px; -fx-padding: 8px;");

        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox vbox = new VBox();
        vbox.getChildren().add(messageLabel);
        vbox.setFillWidth(true);

        HBox.setHgrow(vbox, Priority.ALWAYS);
        hbox.setPadding(new Insets(5, 10, 5, 10));
    }

    @Override
    protected void updateItem(ChatMessage item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        hbox.getChildren().clear(); //limpiar los mensajes antes de añadir nuevos

        if (empty || item == null) {
            setGraphic(null);
        } else {
            messageLabel.setText(item.getMessage());

            if (item.getEmail().equals(LoginController.adminEmail)) {
                //mensajes enviados por el admin
                hbox.setStyle("-fx-background-color: transparent; -fx-alignment: center-right;");
                messageLabel.setStyle("-fx-background-color: #C4E1E5; -fx-background-radius: 10px; -fx-padding: 8px;");
                hbox.getChildren().addAll(spacer, messageLabel);
            } else {
                //mensajes recibidos
                hbox.setStyle("-fx-background-color: transparent; -fx-alignment: center-left;");
                messageLabel.setStyle("-fx-background-color: #7FE0ED; -fx-background-radius: 10px; -fx-padding: 8px;");
                hbox.getChildren().addAll(messageLabel, spacer);
            }

            setGraphic(hbox);
        }
    }
}
