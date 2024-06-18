package es.localhost.proyectofinal;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LoginController implements Initializable {

    private static final String ip = "http://192.168.0.22/WebServices/";

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Text Titulo;

    public static String adminEmail;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> Titulo.requestFocus());
    }

    @FXML
    protected void handleLoginButtonAction() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Por favor, rellena todos los campos");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("Por favor, introduce un correo electrónico válido");
        } else {
            boolean loginSuccessful = authenticateUser(email, password);
            if (loginSuccessful) {
                adminEmail = email; //guardar el email en la variable estática
                navigateToMainView();
            }
        }
    }

    private boolean authenticateUser(String email, String password) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(ip + "consultarAdmin.php?email=" + email + "&contrasena=" + password);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.getBoolean("success")) {
                    return true;
                } else {
                    showError(jsonResponse.getString("error"));
                }
            } else {
                System.out.println("La solicitud GET no funcionó. Código de respuesta: " + responseCode);
            }

        } catch (UnknownHostException e) {
            showError("No se pudo resolver el host: " + e.getMessage());
        } catch (Exception e) {
            showError("Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return false;
    }

    private void navigateToMainView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("inicio-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de inicio de sesión");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
