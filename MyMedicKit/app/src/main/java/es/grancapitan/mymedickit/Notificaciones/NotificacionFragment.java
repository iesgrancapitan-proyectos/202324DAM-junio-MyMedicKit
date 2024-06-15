package es.grancapitan.mymedickit.Notificaciones;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.grancapitan.mymedickit.Objetos.Notificacion;
import es.grancapitan.mymedickit.R;

public class NotificacionFragment extends Fragment {

    private static final String REPETIR_NOTIFICACION_ACTION = "com.proyect.proyecto.REPETIR_ALARMA";

    private AutoCompleteTextView autoCompletarMedicamento;
    private CheckBox checkBoxToday,checkBoxDiariamente;
    private TimePicker timePicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notificaciones_fragment, container, false);
        initVistas(view);
        ConfiguracionListeners();
        return view;
    }

    private void initVistas(View view) {
        autoCompletarMedicamento = view.findViewById(R.id.autoCompleteMedicamento);
        checkBoxDiariamente = view.findViewById(R.id.checkBoxDiariamente);
        checkBoxToday = view.findViewById(R.id.checkBoxHoy);
        timePicker = view.findViewById(R.id.timePicker);
        ImageView imgVolver = view.findViewById(R.id.img_volver);
        Button btnNotificacion = view.findViewById(R.id.btnNotificacion);

        timePicker.setIs24HourView(true);

        imgVolver.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_navigation_configurar_notificaiones));

        btnNotificacion.setOnClickListener(v -> ConfiguracionBotonNotificacion());
    }

    private void ConfiguracionListeners() {
        checkBoxDiariamente.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkBoxToday.setChecked(false);
        });

        checkBoxToday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkBoxDiariamente.setChecked(false);
        });
    }

    private void ConfiguracionBotonNotificacion() {
        String nombreMedicamento = autoCompletarMedicamento.getText().toString().trim();
        if (nombreMedicamento.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.error_nombre_med), Toast.LENGTH_SHORT).show();
            return;
        }

        //obtener el tiempo en milisegundos
        long timeInMillis = TiempoEnMilis();
        boolean isDaily = checkBoxDiariamente.isChecked();
        boolean isToday = checkBoxToday.isChecked();

        //programar notificación diaria
        if (isDaily) {
            Toast.makeText(requireContext(), getString(R.string.notificacion_programada), Toast.LENGTH_SHORT).show();
            ProgramarNotificacionDiaria(timeInMillis, nombreMedicamento);
        } else if (isToday) {
            //programar notificación solo para hoy
            Toast.makeText(requireContext(), getString(R.string.notificacion_programada), Toast.LENGTH_SHORT).show();
            ProgramarNotificacionSoloHoy(timeInMillis, nombreMedicamento);
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_notificacion), Toast.LENGTH_SHORT).show();
        }
    }

    private long TiempoEnMilis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    private void ProgramarNotificacionSoloHoy(long timeInMillis, String nombreMedicamento) {
        PendingIntent pendingIntent = CrearIntent(timeInMillis, nombreMedicamento, null);
        setNotificacion(timeInMillis, pendingIntent);

        GuardarNotificacionesMemoria(timeInMillis, getString(R.string.hoy));
    }

    private void ProgramarNotificacionDiaria(long timeInMillis, String nombreMedicamento) {
        PendingIntent pendingIntent = CrearIntent(timeInMillis, nombreMedicamento, REPETIR_NOTIFICACION_ACTION);
        setNotificacion(timeInMillis, pendingIntent);

        GuardarNotificacionesMemoria(timeInMillis, getString(R.string.diariamente));
    }

    private PendingIntent CrearIntent(long timeInMillis, String nombreMedicamento, @Nullable String action) {
        Intent intent = new Intent(requireContext(), NotificacionReceptor.class);
        if (action != null) intent.setAction(action);
        intent.putExtra("timeInMillis", timeInMillis);
        intent.putExtra("nombreMedicamento", nombreMedicamento);
        return PendingIntent.getBroadcast(
                requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void setNotificacion(long timeInMillis, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            //configurar la alarma exacta y permitir que se ejecute mientras el dispositivo esta inactivo
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
    }

    private void GuardarNotificacionesMemoria(long timeInMillis, String repetition) {
        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timeInMillis));
        String nombreMedicamento = autoCompletarMedicamento.getText().toString().trim();
        Notificacion notification = new Notificacion(hora, repetition, nombreMedicamento);
        NotificationMemoria.AñadirNotificacion(notification);
    }
}