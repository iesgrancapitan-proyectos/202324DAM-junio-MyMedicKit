package es.grancapitan.mymedickit.Notificaciones;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import es.grancapitan.mymedickit.Objetos.Notificacion;

public class NotificationMemoria {
    private static final String PREFS_NOMBRE = "notifications_prefs";
    private static final String NOTIFICATION_KEY = "notifications";
    private static SharedPreferences sharedPreferences;
    private static Gson gson = new Gson();

    private static List<Notificacion> notificaciones = new ArrayList<>();

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE);
        cargarNotificacion();
    }

    public static void AñadirNotificacion(Notificacion notificacion) {
        NotificationMemoria.notificaciones.add(notificacion);
        guardarNotificacion();
    }

    public static List<Notificacion> getNotificacion() {
        return notificaciones;
    }

    public static void guardarNotificacion(List<Notificacion> notificaciones) {
        String json = gson.toJson(notificaciones); //convertir la lista a JSON
        sharedPreferences.edit().putString(NOTIFICATION_KEY, json).apply();
    }

    private static void guardarNotificacion() {
        guardarNotificacion(notificaciones);
    }

    private static void cargarNotificacion() {
        String json = sharedPreferences.getString(NOTIFICATION_KEY, null);
        if (json != null) {
            //convertir el JSON a una lista de notificaciones
            Type type = new TypeToken<List<Notificacion>>() {}.getType();
            notificaciones = gson.fromJson(json, type);
        }
    }
}