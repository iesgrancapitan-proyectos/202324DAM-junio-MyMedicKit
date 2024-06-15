package es.grancapitan.mymedickit.Objetos;

public class Medicamento {

    private int med_id;
    private String nombre;

    public Medicamento() {
    }

    public int getMed_id() {
        return med_id;
    }

    public void setMed_id(int med_id) {
        this.med_id = med_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
