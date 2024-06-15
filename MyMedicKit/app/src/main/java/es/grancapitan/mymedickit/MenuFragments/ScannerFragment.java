package es.grancapitan.mymedickit.MenuFragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import es.grancapitan.mymedickit.R;

public class ScannerFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    private TextView nombreTextView, laboratorioTextView, cnTextView;
    private EditText cnEditText;
    private String prescripcionText, principioActivoText, formaFarmaceuticaText, viaAdministracionText, dosisText, pdfTipo1Text, pdfTipo2Text;

    //ActivityResultLauncher para manejar el resultado de la actividad de escaneo
    private ActivityResultLauncher<Intent> scanLauncher;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scanner_fragment, container, false);

        nombreTextView = view.findViewById(R.id.nombreTextView);
        laboratorioTextView = view.findViewById(R.id.laboratorioTextView);
        cnTextView = view.findViewById(R.id.cnTextView);
        cnEditText = view.findViewById(R.id.CodigoNacionalEditText);

        Button btnGuardarMedicamento = view.findViewById(R.id.btnGuardarMedicamento);
        ImageView btnCN = view.findViewById(R.id.btnBuscarCN);
        Button scanButton = view.findViewById(R.id.scanButton);

        request = Volley.newRequestQueue(requireContext());

        btnGuardarMedicamento.setOnClickListener(v -> cargarWebService());
        btnCN.setOnClickListener(v -> extraerMedicamentoCIMA(cnEditText.getText().toString()));
        scanButton.setOnClickListener(v -> iniciarScanner());

        //inicia ActivityResultLauncher para manejar el resultado del escaneo
        scanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    getActivity();
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), data);
                            if (intentResult.getContents() != null) {
                                String scannedData = intentResult.getContents();
                                String cn = extractCN(scannedData);
                                if (cn != null) {
                                    cnTextView.setText(cn);
                                    extraerMedicamentoCIMA(cn);
                                } else {
                                    Toast.makeText(getContext(), "Código escaneado no es un GTIN válido", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Escaneo cancelado", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
        );

        return view;
    }

    private void iniciarScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        Intent intent = integrator.createScanIntent();
        scanLauncher.launch(intent);
    }

    //extraer el cñdigo nacional (CN) del codigo GTIN escaneado
    private String extractCN(String scannedData) {
        //verifica si el GTIN tiene el prefijo correcto para medicamentos en España
        if (scannedData.startsWith("847000") && scannedData.length() == 13) {
            //wxtrae los digitos del CN (excluyendo el ultimo digito de control)
            String cn = scannedData.substring(6, 12);
            if (esValidoCN(cn)) {
                return cn;
            }
        }
        return null;
    }

    private boolean esValidoCN(String cn) {
        //verifica si el CN tiene 6 digitos extraidos del GTIN
        return cn.matches("\\d{6}");
    }

    //obtener datos de un medicamento de la API de CIMA
    private void extraerMedicamentoCIMA(String cn) {
        new Thread(() -> {
            try {
                URL url = new URL("https://cima.aemps.es/cima/rest/medicamento?cn=" + cn);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String response = leerStream(in);
                    requireActivity().runOnUiThread(() -> procesarRespuestaCIMA(response));
                } else {
                    Log.e("fetchCIMAData", "Error en la conexión: " + responseCode);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error en la conexión: " + responseCode, Toast.LENGTH_LONG).show()
                    );
                }
            } catch (Exception e) {
                Log.e("fetchCIMAData", "Exception: " + e.getMessage());
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    //leer el contenido de un InputStream
    private String leerStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    //procesar la respuesta de la API de CIMA
    private void procesarRespuestaCIMA(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray presentacionesArray = jsonObject.optJSONArray("presentaciones");
            String presentacionNombre = "Nombre de presentación no disponible";
            if (presentacionesArray != null && presentacionesArray.length() > 0) {
                JSONObject presentacion = presentacionesArray.getJSONObject(0);
                presentacionNombre = presentacion.optString("nombre", presentacionNombre);
            }

            String descripcion = jsonObject.optString("cpresc", "Descripción no disponible");
            String principioActivo = jsonObject.optString("pactivos", "Principio activo no disponible");
            String formaFarmaceutica = jsonObject.optString("formaFarmaceutica", "Forma Farmacéutica no disponible");
            String dosis = jsonObject.optString("dosis", "Dosis no disponible");

            JSONArray viasArray = jsonObject.optJSONArray("viasAdministracion");
            StringBuilder viasAdministracion = new StringBuilder();
            if (viasArray != null) {
                for (int i = 0; i < viasArray.length(); i++) {
                    if (i > 0) {
                        viasAdministracion.append(", ");
                    }
                    viasAdministracion.append(viasArray.getJSONObject(i).optString("nombre", "Vía no disponible"));
                }
            }

            String laboratorio = jsonObject.optString("labtitular", "Laboratorio no disponible");

            JSONArray docsArray = jsonObject.optJSONArray("docs");
            String pdfTipo1 = "PDF Tipo 1 no disponible";
            String pdfTipo2 = "PDF Tipo 2 no disponible";
            if (docsArray != null) {
                for (int i = 0; i < docsArray.length(); i++) {
                    JSONObject doc = docsArray.getJSONObject(i);
                    int tipo = doc.optInt("tipo");
                    if (tipo == 1) {
                        pdfTipo1 = doc.optString("url", pdfTipo1);
                    } else if (tipo == 2) {
                        pdfTipo2 = doc.optString("url", pdfTipo2);
                    }
                }
            }

            nombreTextView.setText(presentacionNombre);
            prescripcionText = descripcion;
            principioActivoText = principioActivo;
            formaFarmaceuticaText = formaFarmaceutica;
            dosisText = dosis;
            viaAdministracionText = viasAdministracion.toString();
            laboratorioTextView.setText(laboratorio);
            pdfTipo1Text = pdfTipo1;
            pdfTipo2Text = pdfTipo2;

        } catch (JSONException e) {
            Log.e("processCIMAResponse", "JSON Exception: " + e.getMessage());
            Toast.makeText(getContext(), "Error al procesar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarWebService() {
        //obtener el userId almacenado previamente en SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        //verificar si se encontro el userId
        if (userId == -1) {
            Toast.makeText(getContext(), "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
            return; //salir del metodo porque no se puede ejecutar sin un userId valido
        }

        String ip = getString(R.string.ip);
        String url = ip + "wsJSONInsertarMedicamento.php?user_id=" + userId +
                "&cn=" + cnTextView.getText().toString() +
                "&nombre=" + nombreTextView.getText().toString() +
                "&laboratorio=" + laboratorioTextView.getText().toString() +
                "&pactivos=" + principioActivoText +
                "&presc=" + prescripcionText +
                "&formaFarmaceutica=" + formaFarmaceuticaText +
                "&dosis=" + dosisText +
                "&viaAdministracion=" + viaAdministracionText +
                "&pdf_1=" + pdfTipo1Text +
                "&pdf_2=" + pdfTipo2Text;

        //reemplazar espacios en blanco con %20 para evitar errores de URL
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(getContext(), getString(R.string.error_registrar_medicamento), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        Toast.makeText(getContext(), getString(R.string.medicamento_registrado), Toast.LENGTH_SHORT).show();

        //limpiar los campos de entrada y los textos almacenados despues de registrar el medicamento
        cnEditText.setText("");
        cnTextView.setText("");
        nombreTextView.setText("");
        laboratorioTextView.setText("");
        prescripcionText = "";
        principioActivoText = "";
        formaFarmaceuticaText = "";
        dosisText = "";
        viaAdministracionText = "";
        pdfTipo1Text = "";
        pdfTipo2Text = "";
    }
}
