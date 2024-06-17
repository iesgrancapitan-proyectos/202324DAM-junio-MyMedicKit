package es.grancapitan.mymedickit.MenuFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
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

public class PerfilFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    EditText campoEmail, campoNombre, campoApellidos, campoNacimiento;
    Button btnGuardar;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ImageView imgVolver;
    TextView textViewSaludo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.perfil_fragment, container, false);

        btnGuardar = view.findViewById(R.id.btn_guardarPerfil);
        imgVolver = view.findViewById(R.id.img_ajustes);

        campoEmail = view.findViewById(R.id.editEmail);
        campoNombre = view.findViewById(R.id.editNombre);
        campoApellidos = view.findViewById(R.id.editApellidos);
        campoNacimiento = view.findViewById(R.id.editNacimientoRegister);
        textViewSaludo = view.findViewById(R.id.textViewSaludo);
        campoEmail.setEnabled(false);

        //cargar datos del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "No registrado");
        int userId = sharedPreferences.getInt("userId", -1);
        String nombre = sharedPreferences.getString("nombre", "No registrado");
        String apellidos = sharedPreferences.getString("apellidos", "No registrado");
        String nacimiento = sharedPreferences.getString("nacimiento", "No registrado");

        campoEmail.setText(email);
        campoNombre.setText(nombre);
        campoApellidos.setText(apellidos);
        campoNacimiento.setText(nacimiento);

        String saludo = getString(R.string.hola, nombre);
        textViewSaludo.setText(saludo);

        request = Volley.newRequestQueue(requireContext());

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarDatosUsuario(userId);
            }
        });

        imgVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAjustesFragment(v);
            }
        });

        //agregar TextWatcher para el campo de nacimiento
        campoNacimiento.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private final Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }

                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        String yyyymmdd = "YYYYMMDD";
                        clean = clean + yyyymmdd.substring(clean.length());
                    } else {
                        int day  = Integer.parseInt(clean.substring(6,8));
                        int mon  = Integer.parseInt(clean.substring(4,6));
                        int year = Integer.parseInt(clean.substring(0,4));

                        mon = mon < 1 ? 1 : Math.min(mon, 12);
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900: Math.min(year, 2100);
                        cal.set(Calendar.YEAR, year);
                        day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
                        clean = String.format("%02d%02d%02d",year, mon, day);
                    }

                    clean = String.format("%s-%s-%s", clean.substring(0,4),
                            clean.substring(4,6),
                            clean.substring(6,8));

                    sel = Math.max(sel, 0);
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

    private void irAjustesFragment(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.navigation_ajustes);
    }

    private void actualizarDatosUsuario(int userId) {
        String ip = getString(R.string.ip);
        String url = ip + "wsJSONActualizarPerfil.php";
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("user_id", userId);
            jsonParam.put("nombre", campoNombre.getText().toString());
            jsonParam.put("apellidos", campoApellidos.getText().toString());
            jsonParam.put("nacimiento", campoNacimiento.getText().toString());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, this, this);
            request.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.has("success")) {
                Toast.makeText(getContext(), response.getString("success"), Toast.LENGTH_SHORT).show();
            } else if (response.has("error")) {
                Toast.makeText(getContext(), response.getString("error"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), getString(R.string.error_actualizar), Toast.LENGTH_SHORT).show();
    }
}
