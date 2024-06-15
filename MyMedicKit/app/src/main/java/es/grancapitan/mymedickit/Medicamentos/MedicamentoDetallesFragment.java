package es.grancapitan.mymedickit.Medicamentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import es.grancapitan.mymedickit.R;

public class MedicamentoDetallesFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    private TextView txtNombre, txtLaboratorio, txtPactivos, txtPresc, txtFormaFarmaceutica, txtDosis, txtViaAdministracion, txtPdf1, txtPdf2;
    private RequestQueue request;
    private static final String TAG = "MedicamentoDetail";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detalles_medicamento_fragment, container, false);

        txtNombre = view.findViewById(R.id.txtNombre);
        txtLaboratorio = view.findViewById(R.id.txtLaboratorio);
        txtPactivos = view.findViewById(R.id.txtPactivos);
        txtPresc = view.findViewById(R.id.txtPresc);
        txtFormaFarmaceutica = view.findViewById(R.id.txtFormaFarmaceutica);
        txtDosis = view.findViewById(R.id.txtDosis);
        txtViaAdministracion = view.findViewById(R.id.txtViaAdministracion);
        txtPdf1 = view.findViewById(R.id.txtPdf1);
        txtPdf2 = view.findViewById(R.id.txtPdf2);

        ImageView imgVolverAtras = view.findViewById(R.id.img_volver_perfil);

        imgVolverAtras.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        request = Volley.newRequestQueue(requireContext());

        //obtener el id del medicamento del Bundle y cargar sus datos
        int medId = requireArguments().getInt("med_id", -1);
        Log.d(TAG, "Received med_id: " + medId);

        if (medId != -1) {
            cargarDatosMedicamento(medId);
        } else {
            Toast.makeText(getContext(), "ID de medicamento no válido", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void cargarDatosMedicamento(int medId) {
        String ip = getString(R.string.ip);
        String url = ip + "wsJSONConsultarMedicamento.php?med_id=" + medId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.has("medicamento")) {
                JSONObject json = response.getJSONObject("medicamento");

                //sacar los datos del medicamento del JSON
                String nombre = json.getString("nombre");
                String laboratorio = json.getString("laboratorio");
                String pactivos = json.getString("pactivos");
                String presc = json.getString("presc");
                String formaFarmaceutica = json.getString("formaFarmaceutica");
                String dosis = json.getString("dosis");
                String viaAdministracion = json.getString("viaAdministracion");
                String pdf1 = json.getString("pdf_1");
                String pdf2 = json.getString("pdf_2");

                txtNombre.setText(nombre);
                txtLaboratorio.setText(laboratorio);
                txtPactivos.setText(pactivos);
                txtPresc.setText(presc);
                txtFormaFarmaceutica.setText(formaFarmaceutica);
                txtDosis.setText(dosis);
                txtViaAdministracion.setText(viaAdministracion);
                txtPdf1.setText(pdf1);
                txtPdf2.setText(pdf2);

            } else {
                Toast.makeText(getContext(), "No se encontró el medicamento", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al procesar los datos", Toast.LENGTH_SHORT).show();
        }
    }
}
