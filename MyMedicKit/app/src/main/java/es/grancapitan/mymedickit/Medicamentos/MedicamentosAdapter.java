package es.grancapitan.mymedickit.Medicamentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import es.grancapitan.mymedickit.Objetos.Medicamento;
import es.grancapitan.mymedickit.R;

public class MedicamentosAdapter extends RecyclerView.Adapter<MedicamentosAdapter.MedicamentosViewHolder> {

    private final List<Medicamento> listaMedicamentos;
    private final FragmentActivity context;

    public MedicamentosAdapter(FragmentActivity context, List<Medicamento> listaMedicamentos) {
        this.context = context;
        this.listaMedicamentos = listaMedicamentos;
    }

    //viewHolder para los elementos de la lista
    public class MedicamentosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtNombre;
        ImageView btnEliminar;
        RequestQueue requestQueue;

        //constructor del ViewHolder
        public MedicamentosViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.nombreMedicamento);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            itemView.setOnClickListener(this);

            requestQueue = Volley.newRequestQueue(context);

            btnEliminar.setOnClickListener(v -> {
                int position = getAdapterPosition(); //obtener la posicion del elemento en la lista
                if (position != RecyclerView.NO_POSITION) {
                    Medicamento medicamento = listaMedicamentos.get(position); //obtener el medicamento en esa posicion
                    eliminarMedicamento(medicamento.getMed_id(), position); //eliminar el medicamento
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); //obtener la posicion del elemento en la lista
            if (position != RecyclerView.NO_POSITION) {
                Medicamento medicamento = listaMedicamentos.get(position); //obtener el medicamento en esa posicion

                //crear el fragmento de detalle y pasar los datos usando un Bundle
                MedicamentoDetallesFragment medicamentoDetailFragment = new MedicamentoDetallesFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("med_id", medicamento.getMed_id());
                medicamentoDetailFragment.setArguments(bundle);

                NavController navController = Navigation.findNavController(context, R.id.nav_host_fragment);
                navController.navigate(R.id.action_navigation_inicio_to_navigation_detalle_medicamento, bundle);
            }
        }

        private void eliminarMedicamento(int medId, int position) {
            String url = "http://192.168.0.10/BDRemota/wsJSONEliminarMedicamento.php?med_id=" + medId;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        //eliminar el medicamento de la lista y notificar al adaptador
                        listaMedicamentos.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listaMedicamentos.size());
                    },
                    error -> Log.d("Error", "Error al eliminar el medicamento"));

            requestQueue.add(jsonObjectRequest);
        }
    }

    //crear nuevos ViewHolder
    @NonNull
    @Override
    public MedicamentosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.objeto_medicamento, parent, false);
        return new MedicamentosViewHolder(vista);
    }

    //asociar datos a los elementos de la lista
    @Override
    public void onBindViewHolder(MedicamentosViewHolder holder, int position) {
        Medicamento medicamento = listaMedicamentos.get(position); //obtener el medicamento en la posicion actual
        holder.txtNombre.setText(medicamento.getNombre()); //establecer el nombre del medicamento
    }

    @Override
    public int getItemCount() {
        return listaMedicamentos.size();
    }
}
