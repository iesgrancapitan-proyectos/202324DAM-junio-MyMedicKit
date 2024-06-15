package es.grancapitan.mymedickit.Ajustes;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import es.grancapitan.mymedickit.Objetos.Mensaje;
import es.grancapitan.mymedickit.R;

import static android.content.Context.MODE_PRIVATE;

public class ChatFragment extends Fragment {

    private ListView chatLista;
    private EditText mensajeTextField;
    private ChatAdapter chatAdapter;
    private ArrayList<Mensaje> MensajesChat;
    private ArrayList<Mensaje> mensajesPendientes;
    private int userId;
    private String emailCliente;

    private static final String TAG = "ChatFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        chatLista = view.findViewById(R.id.chatListView);
        mensajeTextField = view.findViewById(R.id.messageTextField);
        Button sendButton = view.findViewById(R.id.sendButton);
        ImageView volverAtras = view.findViewById(R.id.volveratras);

        MensajesChat = new ArrayList<>();
        mensajesPendientes = new ArrayList<>();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        emailCliente = sharedPreferences.getString("userEmail", "");
        Log.d(TAG, "ID del usuario: " + userId);
        Log.d(TAG, "Email del cliente: " + emailCliente);

        chatAdapter = new ChatAdapter(getActivity(), MensajesChat, emailCliente);
        chatLista.setAdapter(chatAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mensajeTextField.getText().toString();
                if (!message.isEmpty()) {
                    //crear objeto Mensaje y añadirlo a la lista de mensajes pendientes
                    Mensaje sentMessage = new Mensaje(message, true, System.currentTimeMillis(), emailCliente);
                    mensajesPendientes.add(sentMessage);
                    MensajesChat.add(sentMessage);
                    chatAdapter.notifyDataSetChanged(); //notificar al adaptador que se añadió un mensaje
                    enviarMensaje(userId, message, emailCliente, sentMessage.getTimestamp());
                    mensajeTextField.setText(""); //limpiar el texto
                    chatLista.smoothScrollToPosition(MensajesChat.size() - 1); //desplazarse al último mensaje
                }
            }
        });

        volverAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverAtrasMethod(v);
            }
        });

        IniciarChat();

        return view;
    }
    private void volverAtrasMethod(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.navigation_ajustes);
    }
    private void enviarMensaje(int userId, String message, String email, long timestamp) {
        Log.d(TAG, "Enviando mensaje: " + message);
        new EnviarMensajes().execute(userId, message, email, timestamp);
    }

    private void recibirMensajes(int userId) {
        Log.d(TAG, "Recibiendo mensajes del usuario: " + userId);
        new RecibirMensajes().execute(userId);
    }

    private void IniciarChat() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                recibirMensajes(userId); //recibir mensajes periódicamente
                handler.postDelayed(this, 5000); //ejecutar cada 5 segundos
            }
        };
        handler.post(runnable);
    }

    private void actualizarChat(ArrayList<Mensaje> receivedMessages) {
        ArrayList<Mensaje> allMessages = new ArrayList<>(receivedMessages);
        for (Mensaje pending : mensajesPendientes) {
            boolean isDuplicate = false;
            for (Mensaje received : receivedMessages) {
                if (pending.getTexto().equals(received.getTexto()) && pending.getTimestamp() == received.getTimestamp()) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                allMessages.add(pending);
            }
        }

        MensajesChat.clear(); //limpiar mensajes actuales
        MensajesChat.addAll(allMessages); //agregar todos los mensajes al chat
        chatAdapter.notifyDataSetChanged(); //notificar al adaptador que se actualizó la lista de mensajes
    }

    @SuppressLint("StaticFieldLeak")
    private class EnviarMensajes extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            //enviar mensaje al servidor
            int userId = (int) params[0];
            String message = (String) params[1];
            String email = (String) params[2];
            long timestamp = (long) params[3];
            String url = "http://192.168.0.10/BDRemota/wsSendMessage.php";

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("user_id", userId);
                jsonParam.put("message", message);
                jsonParam.put("email", email);
                jsonParam.put("timestamp", timestamp);

                OutputStream os = connection.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Enviar código de respuesta al mensaje: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Mensaje enviado correctamente.");
                    Iterator<Mensaje> iterator = mensajesPendientes.iterator();
                    while (iterator.hasNext()) {
                        Mensaje pendingMessage = iterator.next();
                        if (pendingMessage.getTexto().equals(message) && pendingMessage.getTimestamp() == timestamp) {
                            iterator.remove();
                        }
                    }
                } else {
                    Log.d(TAG, "Error al enviar el mensaje. Código de respuesta: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Exception: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            recibirMensajes(userId); //recibir mensajes despues de enviar con exito
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RecibirMensajes extends AsyncTask<Integer, Void, ArrayList<Mensaje>> {
        @Override
        protected ArrayList<Mensaje> doInBackground(Integer... params) {
            //recibir mensajes del servidor
            int userId = params[0];
            String url = "http://192.168.0.10/BDRemota/wsReceiveMessages.php?user_id=" + userId;
            ArrayList<Mensaje> newMessages = new ArrayList<>();

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
                    for (int i = 0; i < messagesArray.length(); i++) {
                        JSONObject message = messagesArray.getJSONObject(i);
                        String text = message.getString("message");
                        long timestamp = message.optLong("timestamp", 0);
                        String email = message.getString("email");

                        boolean isDuplicate = false;
                        for (Mensaje sentMessage : mensajesPendientes) {
                            if (sentMessage.getTexto().equals(text) && sentMessage.getTimestamp() == timestamp && sentMessage.getEmail().equals(email)) {
                                isDuplicate = true;
                                break;
                            }
                        }

                        if (!isDuplicate) {
                            newMessages.add(new Mensaje(text, false, timestamp, email));
                        }
                    }
                } else if (jsonResponse.has("error")) {
                    Log.d(TAG, "Error: " + jsonResponse.getString("error"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Exception: " + e.getMessage());
            }

            return newMessages;
        }

        @Override
        protected void onPostExecute(ArrayList<Mensaje> newMessages) {
            if (newMessages != null && !newMessages.isEmpty()) {
                actualizarChat(newMessages); //actualizar la lista de mensajes recibidos
                Log.d(TAG, "Mensajes recibidos y lista actualizada");
            } else {
                Log.d(TAG, "No hay mensajes nuevos o no se pudieron recuperar mensajes");
            }
        }
    }
}
