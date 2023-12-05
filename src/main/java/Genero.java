import javax.xml.bind.annotation.*;
/**
 * Subclase que se encuentra dentro
 * de la clase Serie
 */
@XmlRootElement(name = "Genero")
@XmlAccessorType(XmlAccessType.FIELD)
public class Genero {
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
     * Nombre del género.
     */
    @XmlElement
    protected String nombre;
    /**
     * Enlace del género.
     */
    @XmlElement
    protected String link;
    /**
     * Descripción o explicación del
     * género, algunos géneros no
     * contienen descripción.
     */
    @XmlElement
    protected String descripcion;
    /**
     * Cantidad de series que pertenecen
     * al género.
     */
    @XmlElement
    protected int series;
    /**
     * Constructor vació porque JaxB lo solicita.
     */
    public Genero() {
    }

    /**
     * Constructor de la clase Género, con todos los atributos
     * necesarios. El identificador es incremental por ello no
     * lo necesita como parámetro.
     * @param nombre Nombre del género.
     * @param link Enlace de la página del género de la que se obtiene
     *             información en el programa.
     * @param descripcion Descripción o explicación de que significa el
     *                    género.
     * @param series Cantidad de series que pertenecen al género.
     */
    public Genero(String nombre, String link, String descripcion, int series) {
        id=idMas;
        idMas++;
        this.nombre = nombre;
        this.link = link;
        this.descripcion = descripcion;
        this.series = series;
    }
    /**
     * Getter del identificador del género.
     * @return Devuelve el ID del género.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter del enlace del género, se utiliza principalmente
     * para evitar entrar en páginas en las ya se ha entrado.
     * @return Retorna un String con el enlace del género.
     */
    public String getLink() {
        return link;
    }

    /**
     * Getter del nombre del género.
     * @return Retorna un String con el nombre del género.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Getter de la explicación del género. Algunos géneros son tan
     * conocidos que no tienen explicación.
     * @return Retorna en forma de String una explicación de que
     * significa el género.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Getter del número de series del género.
     * @return Retorna en int el número de series que contiene
     * el género.
     */
    public int getSeries() {
        return series;
    }

    /**
     * Método toString autogenerado, con un par de retoques
     * para que solo muestre el ID del género.
     * @return Retorna como String el identificador
     * del género para poder identificar a que series
     * pertenece en el CSV.
     */
    @Override
    public String toString() {
        return ""+id;
    }
}
