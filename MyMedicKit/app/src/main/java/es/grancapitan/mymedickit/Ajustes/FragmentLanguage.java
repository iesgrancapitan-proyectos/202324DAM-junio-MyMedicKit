package es.grancapitan.mymedickit.Ajustes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Locale;

import es.grancapitan.mymedickit.R;

public class FragmentLanguage extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leguaje_fragment, container, false);

        Button btnSpanish = view.findViewById(R.id.btnEspanol);
        Button btnEnglish = view.findViewById(R.id.btnIngles);
        Button btnItalian = view.findViewById(R.id.btnItaliano);
        Button btnFrench = view.findViewById(R.id.btnFrances);
        ImageView volverAtras = view.findViewById(R.id.img_volver_perfil);

        btnSpanish.setOnClickListener(v -> ponerLocal("es"));
        btnEnglish.setOnClickListener(v -> ponerLocal("en"));
        btnItalian.setOnClickListener(v -> ponerLocal("it"));
        btnFrench.setOnClickListener(v -> ponerLocal("fr"));

        volverAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAtras(v);
            }
        });

        return view;
    }

    private void irAtras(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.navigation_ajustes); // Navegar al fragmento de ajustes
    }

    private void ponerLocal(String languageCode) {

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        //crear una nueva configuracion y establecer el idioma
        Configuration config = new Configuration();
        config.locale = locale;

        //actualizar la configuracion
        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());

        //guardar el codigo de idioma seleccionado en SharedPreferences
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_key_language), languageCode);
        editor.apply();

        //reiniciar la actividad menu para aplicar el cambio de idioma
        requireActivity().recreate();
    }
}
