package es.grancapitan.mymedickit.MenuFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.grancapitan.mymedickit.Objetos.Notificacion;
import es.grancapitan.mymedickit.Notificaciones.NotificacionAdapter;
import es.grancapitan.mymedickit.Notificaciones.NotificationMemoria;
import es.grancapitan.mymedickit.R;

public class ListaNotificacionesFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lista_notificacion_fragment, container, false);

        //inicializa NotificationMemoria para gestionar las notificaciones guardadas
        NotificationMemoria.init(requireContext());

        RecyclerView recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        ImageView addAlarma = view.findViewById(R.id.addAlarma);

        List<Notificacion> notificationList = NotificationMemoria.getNotificacion();

        NotificacionAdapter adapter = new NotificacionAdapter(notificationList);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNotifications.setAdapter(adapter);

        NavController navController = NavHostFragment.findNavController(this);

        addAlarma.setOnClickListener(v -> navController.navigate(R.id.action_navigation_alarmas_to_navigation_configurar_alarmas));

        return view;
    }
}
