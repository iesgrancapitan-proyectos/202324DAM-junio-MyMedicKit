package es.grancapitan.mymedickit.Ajustes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import es.grancapitan.mymedickit.Activities.InicioActivity;
import es.grancapitan.mymedickit.R;

public class FragmentAjustes extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ajustes_fragment, container, false);

        ImageView volverAtrasAjustes = view.findViewById(R.id.img_volver_perfil);
        Button cerrarSesion = view.findViewById(R.id.btn_cerrarSesion);
        Button btnSeguridad = view.findViewById(R.id.btn_chat_ajustes);
        Button btnLanguage = view.findViewById(R.id.btn_lenguaje);

        cerrarSesion.setOnClickListener(v -> mostrarConfirmacionCerrarSesion());

        btnSeguridad.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_chat); // Navegar al fragmento de chat
        });

        btnLanguage.setOnClickListener(this::irLenguajeFragment);

        volverAtrasAjustes.setOnClickListener(this::irAtras);

        return view;
    }

    //cerrar sesion
    private void salir() {
        Intent intent = new Intent(getActivity(), InicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void mostrarConfirmacionCerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom);
        builder.setTitle(getString(R.string.confirmacion_title))
                .setMessage(getString(R.string.confirmacion_cerrar_sesion_message))
                .setPositiveButton(getString(R.string.confirmacion_cerrar_sesion_positive), (dialog, which) -> limpiarDatosYCerrarSesion())
                .setNegativeButton(getString(R.string.confirmacion_cerrar_sesion_negative), null)
                .show();
    }

    private void limpiarDatosYCerrarSesion() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UsuarioPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        Log.d("DEBUG", "isLoggedIn despues de limpiar: " + isLoggedIn);

        salir();
    }

    private void irAtras(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.navigation_perfil);
    }

    private void irLenguajeFragment(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.navigation_language);
    }
}
