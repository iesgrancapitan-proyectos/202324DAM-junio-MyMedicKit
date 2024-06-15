package es.grancapitan.mymedickit.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import es.grancapitan.mymedickit.R;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_activity);

        //obtener datos de sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String email = sharedPreferences.getString("email", "");

        //verifica inicio sesion y email
        if (isLoggedIn && !email.isEmpty()) {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish(); //finaliza esta actividad para evitar volver atras
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            if (savedInstanceState == null) {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.contenedor);
                if (navHostFragment == null) {
                    navHostFragment = NavHostFragment.create(R.navigation.nav_graph_inicio);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contenedor, navHostFragment)
                            .setPrimaryNavigationFragment(navHostFragment)
                            .commit();
                }
            }
        }
    }
}
