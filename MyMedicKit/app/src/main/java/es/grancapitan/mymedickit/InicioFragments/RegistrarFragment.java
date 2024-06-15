package es.grancapitan.mymedickit.InicioFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.util.Calendar;

import es.grancapitan.mymedickit.R;

public class RegistrarFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    private EditText campoEmail, campoNombre, campoApellidos, campoContrasena, campoNacimiento, campoConfirmarContrasena;
    private RequestQueue request;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        campoEmail = view.findViewById(R.id.editEmail);
        campoNombre = view.findViewById(R.id.editNombre);
        campoApellidos = view.findViewById(R.id.editApellido);
        campoContrasena = view.findViewById(R.id.editContrasena);
        campoConfirmarContrasena = view.findViewById(R.id.editText_pass2_registrarse2);
        campoNacimiento = view.findViewById(R.id.editNacimientoRegister);
        Button btnRegistrar = view.findViewById(R.id.btn_registrar);
        Button btnVolver = view.findViewById(R.id.btn_volver);
        CheckBox checkMostrarPass = view.findViewById(R.id.check_mostrar_pass);

        request = Volley.newRequestQueue(requireContext());

        btnRegistrar.setOnClickListener(v -> {
            if (validarDatos()) {
                cargarWebService();
            }
        });

        btnVolver.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_login));

        checkMostrarPass.setOnCheckedChangeListener((buttonView, isChecked) -> mostrarContrasena(isChecked));

        //configurar un TextWatcher para el campo de fecha de nacimiento
        campoNacimiento.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private final Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    String cleanC = current.replaceAll("[^\\d]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        String yyyymmdd = "YYYYMMDD";
                        clean = clean + yyyymmdd.substring(clean.length());
                    } else {
                        int day = Integer.parseInt(clean.substring(6, 8));
                        int mon = Integer.parseInt(clean.substring(4, 6));
                        int year = Integer.parseInt(clean.substring(0, 4));

                        mon = Math.max(1, Math.min(mon, 12));
                        cal.set(Calendar.MONTH, mon - 1);
                        year = Math.max(1900, Math.min(year, 2100));
                        cal.set(Calendar.YEAR, year);
                        day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
                        clean = String.format("%04d%02d%02d", year, mon, day);
                    }

                    clean = String.format("%s-%s-%s", clean.substring(0, 4),
                            clean.substring(4, 6),
                            clean.substring(6, 8));

                    sel = Math.max(0, sel);
                    current = clean;
                    campoNacimiento.setText(current);
                    campoNacimiento.setSelection(Math.min(sel, current.length()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void mostrarContrasena(boolean isChecked) {
        if (isChecked) {
            campoContrasena.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            campoConfirmarContrasena.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            campoContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            campoConfirmarContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        campoContrasena.setSelection(campoContrasena.getText().length());
        campoConfirmarContrasena.setSelection(campoConfirmarContrasena.getText().length());
    }

    //metodo validar los datos insertados
    private boolean validarDatos() {
        if (campoEmail.getText().toString().isEmpty() ||
                campoNombre.getText().toString().isEmpty() ||
                campoApellidos.getText().toString().isEmpty() ||
                campoContrasena.getText().toString().isEmpty() ||
                campoConfirmarContrasena.getText().toString().isEmpty() ||
                campoNacimiento.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!campoContrasena.getText().toString().equals(campoConfirmarContrasena.getText().toString())) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void cargarWebService() {
        String ip = getString(R.string.ip);
        String url = ip + "wsJSONRegistro.php?email=" + campoEmail.getText().toString() +
                "&contrasena=" + campoContrasena.getText().toString() +
                "&nombre=" + campoNombre.getText().toString() +
                "&apellidos=" + campoApellidos.getText().toString() +
                "&nacimiento=" + campoNacimiento.getText().toString();

        url = url.replace(" ", "%20"); //reemplazar espaciados por su equivalente en URL (%20)

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
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
                //mostrar mensaje exitoso y limpiar campos del formulario
                Toast.makeText(getContext(), "Se ha registrado correctamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                //navegar de vuelta al fragmento de login
                Navigation.findNavController(requireView()).navigate(R.id.navigation_login);
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //manejar errores en la solicitud al servicio web
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No se pudo conectar con la base de datos", Toast.LENGTH_SHORT).show();
    }

    //limpiar los campos del formulario
    private void limpiarCampos() {
        campoEmail.setText("");
        campoContrasena.setText("");
        campoConfirmarContrasena.setText("");
        campoNombre.setText("");
        campoApellidos.setText("");
        campoNacimiento.setText("");
    }
}
