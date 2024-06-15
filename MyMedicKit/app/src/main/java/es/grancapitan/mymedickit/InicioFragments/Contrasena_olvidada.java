package es.grancapitan.mymedickit.InicioFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import es.grancapitan.mymedickit.R;

public class Contrasena_olvidada extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contrasena_fragment, container, false);

        Button btnVolver = view.findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);

            navController.popBackStack();
        });

        return view;
    }
}
