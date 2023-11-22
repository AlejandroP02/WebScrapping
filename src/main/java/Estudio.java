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
}
