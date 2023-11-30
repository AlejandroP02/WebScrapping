import java.time.LocalDate;

public class Estudio {
    static int idMas = 1;
    private int id;
    private String nombre;
    private String link;
    private LocalDate fechaCreacion;
    private int series;

    public Estudio(String nombre, String link, LocalDate fechaCreacion, int series) {
        id=idMas;
        idMas++;
        this.nombre = nombre;
        this.link = link;
        this.fechaCreacion = fechaCreacion;
        this.series = series;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getLink() {
        return link;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public int getSeries() {
        return series;
    }

    @Override
    public String toString() {
        return ""+id;
    }
}
