package es.grancapitan.mymedickit.Objetos;

public class Notificacion {

    private String tiempo;
    private String repetition;
    private String nombreMedicamento;

    public Notificacion(String time, String repetition, String nombreMedicamento) {
        this.tiempo = time;
        this.repetition = repetition;
        this.nombreMedicamento = nombreMedicamento;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getRepetition() {
        return repetition;
    }

    public void setRepetition(String repetition) {
        this.repetition = repetition;
    }

    public String getNombreMedicamento() {
        return nombreMedicamento;
    }

    public void setNombreMedicamento(String nombreMedicamento) {
        this.nombreMedicamento = nombreMedicamento;
    }
}
