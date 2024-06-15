package es.grancapitan.mymedickit.Ajustes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import es.grancapitan.mymedickit.Objetos.Mensaje;
import es.grancapitan.mymedickit.R;

public class ChatAdapter extends BaseAdapter {
    private final List<Mensaje> mensajes;
    private final LayoutInflater inflater;
    private final String emailCliente;


    public ChatAdapter(Context context, List<Mensaje> messages, String clientEmail) {
        this.mensajes = messages;
        this.inflater = LayoutInflater.from(context);
        this.emailCliente = clientEmail;
    }

    @Override
    public int getCount() {
        return mensajes.size();
    }

    @Override
    public Object getItem(int position) {
        return mensajes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //devuelve el tipo de mensaje enviado o recibido
    @Override
    public int getItemViewType(int position) {
        Mensaje message = mensajes.get(position);
        return message.getEmail().equals(emailCliente) ? 1 : 0; // 1 para mensajes enviados, 0 para recibidos
    }

    @Override
    public int getViewTypeCount() {
        return 2; //mensajes recibidos y enviados
    }

    // Crea o recibe un mensaje segun su tipo (enviado o recibido)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (convertView == null) { //si convertView es nulo, inflar un nuevo diseño según el tipo de mensaje
            if (viewType == 1) {
                convertView = inflater.inflate(R.layout.objeto_mensaje_enviado, parent, false); //mensaje enviado
            } else {
                convertView = inflater.inflate(R.layout.objeto_mensaje_recibido, parent, false); //mensaje recibido
            }
        }

        TextView textMessage = convertView.findViewById(R.id.textoMensaje);

        textMessage.setText(mensajes.get(position).getTexto());

        return convertView;
    }
}
