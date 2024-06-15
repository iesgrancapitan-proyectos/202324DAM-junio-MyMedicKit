package es.grancapitan.mymedickit.Notificaciones;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import es.grancapitan.mymedickit.Objetos.Notificacion;
import es.grancapitan.mymedickit.R;

public class NotificacionReceptor extends BroadcastReceiver {

    private static final String CANAL_ID = "canalId";
    private static final int NOTIFICACION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        //verificar si las notificaciones estan habilitadas
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            if (intent != null) {
                crearCanalNotificacion(context);
                crearNotificacion(context, intent);

                //programar la siguiente notificacion
                if ("com.proyect.proyecto.REPETIR_ALARMA".equals(intent.getAction())) {
                    programarSiguienteNotificacion(context);
                } else {
                    //eliminar la notificacion si es solo para hoy
                    eliminarNotificacionHoy(context, intent);
                }
            }
        } else {
            Log.e("NotificacionReceiver", "Las notificaciones no están habilitadas.");
        }
    }

    //crear el canal de notificacion (si la version de Android es Oreo o superior)
    @SuppressLint("ObsoleteSdkInt")
    private void crearCanalNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel canal = new NotificationChannel(CANAL_ID, "Notificacion", importancia);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(canal);
            }
        }
    }

    private void crearNotificacion(Context context, Intent intent) {
        String nombreMedicamento = intent.getStringExtra("nombreMedicamento");
        if (nombreMedicamento == null || nombreMedicamento.isEmpty()) {
            nombreMedicamento = "medicamento";
        }
        String contentText = "Medicamento: " + nombreMedicamento;

        //configurar el builder de la notificacion
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setContentTitle("Recordatorio de Medicamento")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.logo_app)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(NOTIFICACION_ID, builder.build());
    }

    //programar la siguiente notificacion para el día siguiente
    private void programarSiguienteNotificacion(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        Intent nextIntent = new Intent(context, NotificacionReceptor.class);
        nextIntent.setAction("com.proyect.proyecto.REPETIR_ALARMA");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        Log.d("NotificacionReceiver", "Siguiente notificación programada para: " + calendar.getTimeInMillis());
    }

    private void eliminarNotificacionHoy(Context context, Intent intent) {
        long timeInMillis = intent.getLongExtra("timeInMillis", 0);
        if (timeInMillis != 0) {
            NotificationMemoria.init(context);
            List<Notificacion> notificaciones = NotificationMemoria.getNotificacion();

            //crear un iterator para eliminar la notificacion
            Iterator<Notificacion> iterator = notificaciones.iterator();
            while (iterator.hasNext()) {
                Notificacion notificacion = iterator.next();
                String notificationTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timeInMillis));
                if (notificacion.getTiempo().equals(notificationTime) &&
                        notificacion.getRepetition().equals(context.getString(R.string.hoy))) {
                    iterator.remove(); //eliminar la notificacion de la lista
                    cancelarNotificacion(context, timeInMillis);
                }
            }
            //guardar la lista de notificaciones actualizada
            NotificationMemoria.guardarNotificacion(notificaciones);
        }
    }

    private void cancelarNotificacion(Context context, long timeInMillis) {
        Intent intent = new Intent(context, NotificacionReceptor.class);
        intent.putExtra("timeInMillis", timeInMillis);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        Log.d("NotificacionReceiver", "Notificación cancelada: " + timeInMillis);
    }
}