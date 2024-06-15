package es.grancapitan.mymedickit.InicioFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import es.grancapitan.mymedickit.Activities.MenuActivity;
import es.grancapitan.mymedickit.R;

public class LoginFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    private EditText campoEmail, campoContrasena;
    private RequestQueue request;
    private boolean isPasswordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        campoEmail = view.findViewById(R.id.editEmail);
        campoContrasena = view.findViewById(R.id.editContrasena);
        TextView contrasenaOlvidada = view.findViewById(R.id.contrasenaOlvidada);
        Button btnRegistrar = view.findViewById(R.id.btnRegister);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        ImageButton mostrarPassButton = view.findViewById(R.id.mostrar_pass);

        request = Volley.newRequestQueue(requireContext());

        btnLogin.setOnClickListener(v -> cargarWebService());
        btnRegistrar.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_register));
        mostrarPassButton.setOnClickListener(v -> mostrarContrasena());
        contrasenaOlvidada.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_contrasena_olvidada));

        return view;
    }

    private void mostrarContrasena() {
        if (isPasswordVisible) {
            campoContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            campoContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        isPasswordVisible = !isPasswordVisible;
        campoContrasena.setSelection(campoContrasena.getText().length());
    }

    private void cargarWebService() {
        String ip = getString(R.string.ip);

        String url = ip + "wsJSONConsultarUsuario.php?email=" + campoEmail.getText().toString() +
                "&contrasena=" + campoContrasena.getText().toString();

        //crear la solicitud JSON utilizando Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);

        //agregar la solicitud a la cola de peticiones de Volley
        request.add(jsonObjectRequest);
    }

    //metodo manejar respuesta exitosa del web service
    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.has("error")) {
                String error = response.getString("error");
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            } else {
                //obtener el objeto usuario del JSON
                JSONObject usuarioJson = response.getJSONArray("usuario").getJSONObject(0);

                //extraer datos del usuario
                int userId = usuarioJson.getInt("user_id");
                String email = usuarioJson.getString("email");
                String nombre = usuarioJson.getString("nombre");
                String apellidos = usuarioJson.getString("apellidos");
                String nacimiento = usuarioJson.getString("nacimiento");

                //guardar los datos del usuario en SharedPreferences
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("userId", userId);
                editor.putString("email", email);
                editor.putString("nombre", nombre);
                editor.putString("apellidos", apellidos);
                editor.putString("nacimiento", nacimiento);
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                //acceder al menu principal
                Toast.makeText(getContext(), getString(R.string.toast_login_correcto), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MenuActivity.class);
                startActivity(intent);
                requireActivity().finish(); //finalizar la actividad inicio para evitar regresar con el boton de atras
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Error en la solicitud al servicio web
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error en inicio de sesión", Toast.LENGTH_SHORT).show();
    }
}
