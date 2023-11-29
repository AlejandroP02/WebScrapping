import java.time.LocalDate;
import java.util.List;

public class Serie {
    static int idMas = 1;
    private int id;
    private String titulo;
    private String imagen;
    private String tipo;
    private int episodios;
    private String estado;
    private LocalDate fechaEstreno;
    private String licencia;
    private List<Estudio> estudios;
    private String src;
    private List<Genero> generos;
    private float duracion;
    private String descripcion;

    public Serie(String titulo, String imagen, String tipo, int episodios, String estado, LocalDate fechaEstreno, String licencia, String src, float duracion, String descripcion) {
        id = idMas;
        idMas++;
        this.titulo = titulo;
        this.imagen = imagen;
        this.tipo = tipo;
        this.episodios = episodios;
        this.estado = estado;
        this.fechaEstreno = fechaEstreno;
        this.licencia = licencia;
        this.src = src;
        this.duracion = duracion;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setEstudios(List<Estudio> estudios) {
        this.estudios = estudios;
    }

    public void setGeneros(List<Genero> generos) {
        this.generos = generos;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getImagen() {
        return imagen;
    }

    public String getTipo() {
        return tipo;
    }

    public int getEpisodios() {
        return episodios;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDate getFechaEstreno() {
        return fechaEstreno;
    }

    public String getLicencia() {
        return licencia;
    }

    public List<Estudio> getEstudios() {
        return estudios;
    }

    public String getSrc() {
        return src;
    }

    public List<Genero> getGeneros() {
        return generos;
    }

    public float getDuracion() {
        return duracion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
