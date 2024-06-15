package es.grancapitan.mymedickit.MenuFragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import es.grancapitan.mymedickit.Objetos.Medicamento;
import es.grancapitan.mymedickit.Medicamentos.MedicamentosAdapter;
import es.grancapitan.mymedickit.R;

public class ListaMedicamentosFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    RecyclerView recyclerMedicamentos;
    ArrayList<Medicamento> listaMedicamentos;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.lista_medicamentos_fragment, container, false);

        recyclerMedicamentos = vista.findViewById(R.id.recyclerViewMedicamentos);
        recyclerMedicamentos.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerMedicamentos.setHasFixedSize(true);

        listaMedicamentos = new ArrayList<>();

        request = Volley.newRequestQueue(requireContext());

        cargarWebService(); //lista los medicamentos del usuario logueado

        return vista;
    }

    private void cargarWebService() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("userId", -1);

        String ip = getString(R.string.ip);
        String url = ip + "wsJSONListarMedicamentos.php?user_id=" + user_id;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);

        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(getContext(), getString(R.string.error_list_medicamentos), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        Medicamento medicamento;

        JSONArray json = jsonObject.optJSONArray("medicamentos");

        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                medicamento = new Medicamento();
                JSONObject jsonObject1;
                try {
                    jsonObject1 = json.getJSONObject(i);
                    medicamento.setMed_id(jsonObject1.getInt("med_id"));
                    medicamento.setNombre(jsonObject1.optString("nombre"));
                    listaMedicamentos.add(medicamento);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        MedicamentosAdapter adapter = new MedicamentosAdapter((FragmentActivity) getContext(), listaMedicamentos);
        recyclerMedicamentos.setAdapter(adapter);
    }
}
