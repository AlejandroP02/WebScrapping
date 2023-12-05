import javax.xml.bind.annotation.*;
import java.time.LocalDate;

/**
 * Subclase que se encuentra dentro
 * de la clase Serie
 */
@XmlRootElement(name = "Estudio")
@XmlAccessorType(XmlAccessType.FIELD)
public class Estudio {
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
     * Nombre del estudio.
     */
    @XmlElement
    protected String nombre;
    /**
     * Enlace del estudio.
     */
    @XmlElement
    protected String link;
    /**
     * Fecha en la que se creó el
     * estudio.
     */
    @XmlElement
    protected LocalDate fechaCreacion;
    /**
     * Cantidad de series que pertenecen
     * al estudio.
     */
    @XmlElement
    protected int series;
    /**
     * Constructor vació porque JaxB lo solicita.
     */
    public Estudio() {
    }

    /**
     * Constructor de la clase Estudio, con todos los atributos
     * necesarios. El identificador es incremental por ello no
     * lo necesita como parámetro.
     * @param nombre Nombre del estudio.
     * @param link Enlace de la página del estudio de la que se obtiene
     *             información en el programa.
     * @param fechaCreacion Fecha en la que se creó el estudio.
     * @param series Cantidad de series en las que ha trabajado el estudio.
     */
    public Estudio(String nombre, String link, LocalDate fechaCreacion, int series) {
        id=idMas;
        idMas++;
        this.nombre = nombre;
        this.link = link;
        this.fechaCreacion = fechaCreacion;
        this.series = series;
    }
    /**
     * Getter del identificador del estudio.
     * @return Devuelve el ID del estudio.
     */
    public int getId() {
        return id;
    }
    /**
     * Getter del nombre del estudio.
     * @return Retorna un String con el nombre del estudio.
     */
    public String getNombre() {
        return nombre;
    }
    /**
     * Getter del enlace del estudio, se utiliza principalmente
     * para evitar entrar en páginas en las ya se ha entrado.
     * @return Retorna un String con el enlace del estudio.
     */
    public String getLink() {
        return link;
    }

    /**
     * Getter de la fecha de creación del estudio.
     * @return Retorna como LocalDate la fecha en la que se
     * creó el estudio.
     */
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    /**
     * Getter del número de series del estudio.
     * @return Retorna en int el número de series que contiene
     * el estudio.
     */
    public int getSeries() {
        return series;
    }
    /**
     * Método toString autogenerado, con un par de retoques
     * para que solo muestre el ID del estudio.
     * @return Retorna como String el identificador
     * del estudio para poder identificar a que series
     * pertenece en el CSV.
     */
    @Override
    public String toString() {
        return ""+id;
    }
}
