import java.time.LocalDate;

public class Estudio {
    private int idSerie;
    private String nombre;
    private String link;
    private LocalDate fechaCreacion;
    private int series;

    public Estudio(int idSerie, String nombre, String link, LocalDate fechaCreacion, int series) {
        this.idSerie = idSerie;
        this.nombre = nombre;
        this.link = link;
        this.fechaCreacion = fechaCreacion;
        this.series = series;
    }
    public Estudio(String nombre, String link, LocalDate fechaCreacion, int series) {
        this.nombre = nombre;
        this.link = link;
        this.fechaCreacion = fechaCreacion;
        this.series = series;
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
        return "Estudio{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}
