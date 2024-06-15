package es.grancapitan.mymedickit.Objetos;

public class Mensaje {
    private String texto;
    private boolean esEnviado;
    private long timestamp;
    private String email;

    public Mensaje(String text, boolean isSent, long timestamp, String email) {
        this.texto = text;
        this.esEnviado = isSent;
        this.timestamp = timestamp;
        this.email = email;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isEsEnviado() {
        return esEnviado;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getEmail() {
        return email;
    }
}
