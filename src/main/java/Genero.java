public class Genero {
    private int idSerie;
    private String nombre;
    private String link;
    private String descripcion;
    private int series;

    public Genero(int idSerie, String nombre, String link, String descripcion, int series) {
        this.idSerie = idSerie;
        this.nombre = nombre;
        this.link = link;
        this.descripcion = descripcion;
        this.series = series;
    }

    public Genero(String nombre, String link, String descripcion, int series) {
        this.nombre = nombre;
        this.link = link;
        this.descripcion = descripcion;
        this.series = series;
    }

    public String getLink() {
        return link;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getSeries() {
        return series;
    }

    @Override
    public String toString() {
        return "Genero{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}
