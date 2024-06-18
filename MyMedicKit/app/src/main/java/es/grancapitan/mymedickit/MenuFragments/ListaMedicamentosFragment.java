package es.grancapitan.mymedickit.MenuFragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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

import es.grancapitan.mymedickit.Medicamentos.MedicamentosAdapter;
import es.grancapitan.mymedickit.Objetos.Medicamento;
import es.grancapitan.mymedickit.R;

public class ListaMedicamentosFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    private RecyclerView recyclerMedicamentos;
    private ArrayList<Medicamento> listaMedicamentos;
    private MedicamentosAdapter adapter;
    private RequestQueue request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.lista_medicamentos_fragment, container, false);

        recyclerMedicamentos = vista.findViewById(R.id.recyclerViewMedicamentos);
        recyclerMedicamentos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerMedicamentos.setHasFixedSize(true);

        listaMedicamentos = new ArrayList<>();
        adapter = new MedicamentosAdapter(requireActivity(), listaMedicamentos);
        recyclerMedicamentos.setAdapter(adapter);

        request = Volley.newRequestQueue(requireContext());

        cargarWebService();

        SearchView searchView = vista.findViewById(R.id.searchViewMedicamentos);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText); //filtrar la lista cada vez que cambie el texto en el SearchView
                return true;
            }
        });

        return vista;
    }

    private void cargarWebService() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("userId", -1);

        String ip = getString(R.string.ip);
        String url = ip + "wsJSONListarMedicamentos.php?user_id=" + user_id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);

        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(requireContext(), getString(R.string.error_list_medicamentos), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResponse(JSONObject jsonObject) {
        listaMedicamentos.clear(); //limpiar la lista actual antes de cargar nuevos datos

        JSONArray json = jsonObject.optJSONArray("medicamentos");

        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                try {
                    JSONObject jsonObject1 = json.getJSONObject(i);
                    int medId = jsonObject1.getInt("med_id");
                    String nombre = jsonObject1.optString("nombre");

                    Medicamento medicamento = new Medicamento();
                    medicamento.setMed_id(medId);
                    medicamento.setNombre(nombre);

                    listaMedicamentos.add(medicamento);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        adapter.notifyDataSetChanged(); //notificar al adaptador que los datos han cambiado
        adapter = new MedicamentosAdapter(requireActivity(), listaMedicamentos);
        recyclerMedicamentos.setAdapter(adapter);
    }
}
