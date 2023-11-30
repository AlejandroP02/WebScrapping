public class Genero {
    static int idMas = 1;
    private int id;
    private String nombre;
    private String link;
    private String descripcion;
    private int series;


    public Genero(String nombre, String link, String descripcion, int series) {
        id=idMas;
        idMas++;
        this.nombre = nombre;
        this.link = link;
        this.descripcion = descripcion;
        this.series = series;
    }

    public int getId() {
        return id;
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
        return ""+id;
    }
}
