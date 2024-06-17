package es.localhost.proyectofinal;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent cargaRoot = FXMLLoader.load(getClass().getResource("pantalla-carga-view.fxml"));
        Scene cargaScene = new Scene(cargaRoot);

        primaryStage.setScene(cargaScene);
        primaryStage.setTitle("Cargando...");
        primaryStage.show();

        PauseTransition pause = new PauseTransition(Duration.seconds(1)); //duracion de 1 segundos
        pause.setOnFinished(event -> {
            try {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("login-view.fxml"));
                Scene loginScene = new Scene(loginRoot);

                primaryStage.setScene(loginScene);
                primaryStage.setTitle("Inicio de sesión");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        pause.play();
    }

    public static void main(String[] args) {
        launch();
    }
}