import javax.xml.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Clase principal que contiene las
 * demás clases.
 */
@XmlRootElement(name = "Serie")
@XmlAccessorType(XmlAccessType.FIELD)
public class Serie {
    /**
     * Identificador automático.
     */
    static int idMas = 1;
    /**
     * Identificador de la clase.
     */
    @XmlElement
    protected int id;
    /**
     * Nombre principal de la serie.
     */
    @XmlElement
    protected String titulo;
    /**
     * URL con la portada de la serie.
     */
    @XmlElement
    protected String imagen;
    /**
     * El tipo que es, puede ser:
     * OVA, TV, MOVIE....
     */
    @XmlElement
    protected String tipo;
    /**
     * La cantidad de episodios de los
     * que dispone la serie.
     */
    @XmlElement
    protected int episodios;
    /**
     * El estado de emisión de la serie.
     */
    @XmlElement
    protected String estado;
    /**
     * La fecha en la que se estrenó la serie,
     * algunas series aún no se han estrenado,
     * por ello no tienen fecha de estreno.
     */
    @XmlElement
    protected LocalDate fechaEstreno;
    /**
     * Licencias con la que cuenta la serie.
     */
    @XmlElement
    protected String licencia;
    /**
     * Lista de estudios que han trabajado en la producción
     * de la serie, algunas series no cuentan con estudios
     * porque aún no se han estrenado y no se sabe que estudios
     * trabajan en ellas.
     */
    @XmlElementWrapper(name = "Estudios")
    @XmlElement(name = "Estudio")
    protected List<Estudio> estudios;
    /**
     * El lugar de donde proviene la serie, es decir
     * de donde se ha sacado el material para hacer la
     * serie, ejemplo: manga, novela, web comic...
     */
    @XmlElement
    protected String src;
    /**
     * Los géneros de los que dispone la serie.
     */
    @XmlElementWrapper(name = "Generos")
    @XmlElement(name = "Genero")
    protected List<Genero> generos;
    /**
     * Duración aproximada por episodio.
     */
    @XmlElement
    protected float duracion;
    /**
     * Sinopsis de la serie.
     */
    @XmlElement
    protected String descripcion;

    /**
     * Constructor vació porque JaxB lo solicita.
     */
    public Serie() {
    }

    /**
     * Constructor con todos los parámetros necesarios para crear una serie sin problemas,
     * no guarda los géneros y los estudios porque esos parámetros se guardan más tarde.
     * El identificador es incremental por ello no se pasa como parámetro.
     * @param titulo Nombre principal.
     * @param imagen URL de la portada.
     * @param tipo Tipo de serie/película.
     * @param episodios Cantidad de episodios.
     * @param estado Estado de emisión.
     * @param fechaEstreno Fecha de salida.
     * @param licencia licencias de la serie.
     * @param src Lugar del que se ha sacado el material para adaptar la serie.
     * @param duracion Duración por episodio.
     * @param descripcion Sinopsis o resumen.
     */
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

    /**
     * Getter del identificador de la serie.
     * @return Devuelve el ID de la serie como int.
     */
    public int getId() {
        return id;
    }

    /**
     * Sirve para almacenar la lista de estudios que no
     * se guarda en el constructor.
     * @param estudios Lista de estudios de la serie.
     */
    public void setEstudios(List<Estudio> estudios) {
        this.estudios = estudios;
    }
    /**
     * Sirve para almacenar la lista de géneros que no
     * se guarda en el constructor.
     * @param generos Lista de géneros de la serie.
     */
    public void setGeneros(List<Genero> generos) {
        this.generos = generos;
    }

    /**
     * Getter del nombre de la serie.
     * @return Retorna el nombre de la serie en String.
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Getter de la imagen de la serie.
     * @return Retorna la URL de la portada de la serie en formato
     * de texto (String).
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * Getter del tipo de serie/película
     * @return retorna un String con el tipo de serie.
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Getter del numero de episodios de la serie.
     * @return Retorna como int el número de episodios con
     * los que cuenta la serie.
     */
    public int getEpisodios() {
        return episodios;
    }

    /**
     * Getter del estado de la serie.
     * @return Retorna en forma de String el estado de la serie.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Getter de la fecha de estreno de la serie.
     * @return Retorna un tipo LocalDate con la fecha de estreno
     * de la serie.
     */
    public LocalDate getFechaEstreno() {
        return fechaEstreno;
    }

    /**
     * Getter de las licencias de la serie.
     * @return Retorna como String las licencias que contiene la
     * serie.
     */
    public String getLicencia() {
        return licencia;
    }

    /**
     * Getter de los identificadores de todos los estudios de la
     * serie.
     * @return Devuelve el identificado de todos los estudios
     * para evitar redundancia en el CSV y relacionar las clases.
     */
    public List<Estudio> getEstudios() {
        return estudios;
    }

    /**
     * Getter del material original de la serie.
     * @return Retorna como String el lugar de donde se ha adaptado
     * la serie
     */
    public String getSrc() {
        return src;
    }

    /**
     * Getter de los identificadores de todos los géneros de la
     * serie.
     * @return Devuelve el identificado de todos los géneros
     * para evitar redundancia en el CSV y relacionar las clases.
     */
    public List<Genero> getGeneros() {
        return generos;
    }

    /**
     * Getter de la duración de cada episodio.
     * @return Retorna en float los minutos que suele durar cada
     * episodio.
     */
    public float getDuracion() {
        return duracion;
    }

    /**
     * Getter de la sinopsis de la serie.
     * @return Retorna como String la sinopsis o descripción de
     * la serie.
     */
    public String getDescripcion() {
        return descripcion;
    }
}
