package es.grancapitan.mymedickit.Notificaciones;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.grancapitan.mymedickit.Objetos.Notificacion;
import es.grancapitan.mymedickit.R;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder> {

    private final List<Notificacion> notificationLista;

    public NotificacionAdapter(List<Notificacion> notificationList) {
        this.notificationLista = notificationList;
    }

    public static class NotificacionViewHolder extends RecyclerView.ViewHolder {
        public TextView tiempoTextView;
        public TextView infoAlarmaTextView;
        public ImageView iconoBorrar;

        public NotificacionViewHolder(View itemView) {
            super(itemView);
            tiempoTextView = itemView.findViewById(R.id.textViewTime);
            infoAlarmaTextView = itemView.findViewById(R.id.infoAlarmaTextView);
            iconoBorrar = itemView.findViewById(R.id.iconoBorrar);
        }
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.objeto_notificacion, parent, false);
        return new NotificacionViewHolder(itemView);
    }

    //enlazar los datos a las vistas del ViewHolder
    @Override
    public void onBindViewHolder(NotificacionViewHolder holder, @SuppressLint("RecyclerView") int posicion) {
        Notificacion notification = notificationLista.get(posicion);
        holder.tiempoTextView.setText(notification.getTiempo());
        String info = notification.getNombreMedicamento() + " - " + notification.getRepetition();
        holder.infoAlarmaTextView.setText(info);

        holder.iconoBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarNotificacion(posicion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationLista.size();
    }

    private void borrarNotificacion(int position) {
        notificationLista.remove(position);
        notifyItemRemoved(position);
        NotificationMemoria.guardarNotificacion(notificationLista);
    }
}