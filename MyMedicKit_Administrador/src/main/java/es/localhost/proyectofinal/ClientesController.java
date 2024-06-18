package es.localhost.proyectofinal;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClientesController {

    private final String ip = "http://192.168.0.22/WebServices/";

    @FXML
    private Button btnVolver;
    @FXML
    private Button btnDetalles;
    @FXML
    private ListView<Cliente> clientesListView;
    @FXML
    private TextField nombreTextField;
    @FXML
    private TextField apellidosTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField nacimientoTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField filtroNombreTextField;

    @FXML
    private ListView<ChatMessage> chatListView;
    @FXML
    private TextField messageTextField;

    @FXML
    private GridPane detallesPanel;
    @FXML
    private Pane chatPanel;

    private List<Cliente> clientes = new ArrayList<>();
    private Cliente clienteSeleccionado;

    private Timer chatTimer;

    @FXML
    private void initialize() {
        clientes = obtenerClientes();
        mostrarClientes(clientes);
        clientesListView.setCellFactory(listView -> new ClienteListCell());

        // Configuración de eventos
        clientesListView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 0) {
                mostrarDetallesCliente(clientesListView.getSelectionModel().getSelectedIndex());
            }
        });

        filtroNombreTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarClientes(newValue);
        });

        chatListView.setCellFactory(listView -> new ChatMessageCell());

        ActualizarChat();
        configurarBotonVolver();
        configurarDetalles();

    }
    private void configurarBotonVolver() {
        Image image = new Image(getClass().getResourceAsStream("/es/localhost/proyectofinal/Imagenes/perfil.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        btnVolver.setGraphic(imageView);
    }
    private void configurarDetalles() {
        Image image = new Image(getClass().getResourceAsStream("/es/localhost/proyectofinal/Imagenes/detalles.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        btnDetalles.setGraphic(imageView);
    }
    private List<Cliente> obtenerClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String url = ip + "wsJSONListarUsuarios.php";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());

            if (jsonResponse.has("users")) {
                JSONArray usersArray = jsonResponse.getJSONArray("users");

                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject user = usersArray.getJSONObject(i);

                    int id = user.getInt("user_id");
                    String nombre = user.getString("nombre");
                    String apellidos = user.getString("apellidos");
                    String email = user.getString("email");
                    String nacimiento = user.getString("nacimiento");

                    Cliente cliente = new Cliente(id, nombre, apellidos, email, nacimiento);
                    clientes.add(cliente);
                }
            } else if (jsonResponse.has("error")) {
                System.out.println("Error: " + jsonResponse.getString("error"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientes;
    }

    private void filtrarClientes(String nombre) {
        List<Cliente> clientesFiltrados = new ArrayList<>();

        for (Cliente cliente : clientes) {
            if (cliente.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                clientesFiltrados.add(cliente);
            }
        }

        mostrarClientes(clientesFiltrados);
    }

    private void mostrarClientes(List<Cliente> clientes) {
        //ordenar clientes si tienen mensajes nuevos
        clientes.sort(Comparator.comparing(Cliente::hasNewMessage).reversed());
        clientesListView.getItems().clear();
        clientesListView.getItems().addAll(clientes);

        if (clienteSeleccionado != null) {
            for (int i = 0; i < clientes.size(); i++) {
                if (clientes.get(i).getId() == clienteSeleccionado.getId()) {
                    clientesListView.getSelectionModel().select(i);
                    break;
                }
            }
        }
    }

    private void mostrarDetallesCliente(int index) {
        if (index >= 0 && index < clientes.size()) {
            clienteSeleccionado = clientes.get(index);
            nombreTextField.setText(clienteSeleccionado.getNombre());
            apellidosTextField.setText(clienteSeleccionado.getApellidos());
            emailTextField.setText(clienteSeleccionado.getEmail());
            nacimientoTextField.setText(clienteSeleccionado.getNacimiento());
            detallesPanel.setVisible(true);
            chatPanel.setVisible(false);

            clientesListView.getSelectionModel().select(index);
        }
    }

    private void actualizarCliente(Cliente cliente) {
        String url = ip + "wsJSONActualizarUsuario.php";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("user_id", cliente.getId());
            jsonParam.put("nombre", cliente.getNombre());
            jsonParam.put("apellidos", cliente.getApellidos());
            jsonParam.put("email", cliente.getEmail());
            jsonParam.put("nacimiento", cliente.getNacimiento());

            if (cliente.getContrasena() != null && !cliente.getContrasena().isEmpty()) {
                jsonParam.put("contrasena", cliente.getContrasena());
            }

            OutputStream os = connection.getOutputStream();
            os.write(jsonParam.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Cliente actualizado correctamente.");
                refrescarListaClientes();
            } else {
                System.out.println("Error al actualizar el cliente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void BotonActualizar() {
        if (clienteSeleccionado != null) {
            String nuevoNombre = nombreTextField.getText();
            String nuevosApellidos = apellidosTextField.getText();
            String nuevoEmail = emailTextField.getText();
            String nuevaFechaNacimiento = nacimientoTextField.getText();
            String nuevaContrasena = passwordField.getText();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de Actualización");
            alert.setHeaderText("Actualizar Cliente");
            alert.setContentText("¿Estás seguro de que deseas actualizar los datos de este cliente?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                clienteSeleccionado.setNombre(nuevoNombre);
                clienteSeleccionado.setApellidos(nuevosApellidos);
                clienteSeleccionado.setEmail(nuevoEmail);
                clienteSeleccionado.setNacimiento(nuevaFechaNacimiento);

                if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
                    clienteSeleccionado.setContrasena(nuevaContrasena);
                }

                actualizarCliente(clienteSeleccionado);
            }
        }
    }

    private void refrescarListaClientes() {
        Cliente clienteSeleccionadoTemp = clienteSeleccionado;
        clientes = obtenerClientes();
        mostrarClientes(clientes);

        if (clienteSeleccionadoTemp != null) {
            for (int i = 0; i < clientes.size(); i++) {
                if (clientes.get(i).getId() == clienteSeleccionadoTemp.getId()) {
                    clientesListView.getSelectionModel().select(i);
                    break;
                }
            }
        }
    }

    @FXML
    private void BotonEliminar() {
        if (clienteSeleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de Eliminación");
            alert.setHeaderText("Eliminar Cliente");
            alert.setContentText("¿Estás seguro de que deseas eliminar este cliente?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                eliminarCliente(clienteSeleccionado);
            }
        }
    }

    private void eliminarCliente(Cliente cliente) {
        String url = ip + "wsJSONEliminarUsuario.php";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("user_id", cliente.getId());

            OutputStream os = connection.getOutputStream();
            os.write(jsonParam.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Cliente eliminado correctamente.");
                clientes.remove(cliente);
                clientesListView.getItems().remove(cliente);
                limpiarDetallesCliente();
                refrescarListaClientes();
            } else {
                System.out.println("Error al eliminar el cliente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void limpiarDetallesCliente() {
        nombreTextField.setText("");
        apellidosTextField.setText("");
        emailTextField.setText("");
        nacimientoTextField.setText("");
        passwordField.setText("");
    }

    @FXML
    private void BotonEnviar() {
        String message = messageTextField.getText();
        if (message != null && !message.isEmpty() && clienteSeleccionado != null) {
            enviarMensaje(clienteSeleccionado.getId(), message, LoginController.adminEmail);
            messageTextField.clear();
        }
    }

    private void enviarMensaje(int userId, String message, String email) {
        String url = ip + "wsSendMessage.php";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("user_id", userId);
            jsonParam.put("message", message);
            jsonParam.put("email", email);

            OutputStream os = connection.getOutputStream();
            os.write(jsonParam.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Mensaje enviado correctamente.");
                recibirMensajes(userId);
            } else {
                System.out.println("Error al enviar el mensaje.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recibirMensajes(int userId) {
        String url = ip + "wsReceiveMessages.php?user_id=" + userId;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.has("messages")) {
                JSONArray messagesArray = jsonResponse.getJSONArray("messages");
                Platform.runLater(() -> {
                    boolean hasNewMessages = false;
                    chatListView.getItems().clear();
                    for (int i = 0; i < messagesArray.length(); i++) {
                        JSONObject message = messagesArray.getJSONObject(i);
                        String text = message.getString("message");
                        String email = message.getString("email");

                        if (!email.equals(LoginController.adminEmail)) {
                            hasNewMessages = true;
                        }

                        chatListView.getItems().add(new ChatMessage(text, email));
                    }

                    for (Cliente cliente : clientes) {
                        if (cliente.getId() == userId) {
                            if (clienteSeleccionado != null && clienteSeleccionado.getId() == userId) {
                                cliente.setHasNewMessage(false);
                            } else {
                                cliente.setHasNewMessage(hasNewMessages);
                            }
                            break;
                        }
                    }
                    mostrarClientes(clientes);
                });
            } else if (jsonResponse.has("error")) {
                System.out.println("Error: " + jsonResponse.getString("error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ActualizarChat() {
        chatTimer = new Timer(true);
        chatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> revisarNuevosMensajes());
            }
        }, 0, 5000);//actualiza cada 5 segundos
    }

    private void revisarNuevosMensajes() {
        List<Cliente> copiaClientes = new ArrayList<>(clientes);
        for (Cliente cliente : copiaClientes) {
            verificarMensajesCliente(cliente);
        }
    }

    private void verificarMensajesCliente(Cliente cliente) {
        String url = ip + "wsReceiveMessages.php?user_id=" + cliente.getId();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.has("messages")) {
                JSONArray messagesArray = jsonResponse.getJSONArray("messages");
                boolean hasNewMessages = false;

                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject message = messagesArray.getJSONObject(i);
                    String email = message.getString("email");

                    if (!email.equals(LoginController.adminEmail)) {
                        hasNewMessages = true;
                        break;
                    }
                }

                if (cliente.hasNewMessage() != hasNewMessages) {
                    cliente.setHasNewMessage(hasNewMessages);
                    mostrarClientes(clientes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void MostrarChat() {
        if (clienteSeleccionado != null) {
            detallesPanel.setVisible(false);
            chatPanel.setVisible(true);

            //marcar mensajes como leídos y actualizar estado
            clienteSeleccionado.setHasNewMessage(false);
            mostrarClientes(clientes); //reordenar la lista

            recibirMensajes(clienteSeleccionado.getId());
        }
    }

    @FXML
    private void MostrarDetalle() {
        detallesPanel.setVisible(true);
        chatPanel.setVisible(false);
    }

    @FXML
    private void BotonLimpiarChat() {
        if (clienteSeleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de Limpieza");
            alert.setHeaderText("Limpiar Chat");
            alert.setContentText("¿Estás seguro de que deseas limpiar el chat con este cliente?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                limpiarChat(clienteSeleccionado);
            }
        }
    }

    private void limpiarChat(Cliente cliente) {
        String url = ip + "wsClearChat.php";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("user_id", cliente.getId());

            OutputStream os = connection.getOutputStream();
            os.write(jsonParam.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Chat limpiado correctamente.");
                chatListView.getItems().clear();
            } else {
                System.out.println("Error al limpiar el chat.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
