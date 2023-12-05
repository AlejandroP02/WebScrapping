import javax.xml.bind.*;
import java.io.File;
import java.util.List;

/**
 * Clase para generar el XML usando solamente JaxB
 */
public class JaxB_XML {
    /**
     * Lista con todas las series almacenadas.
     */
    private List<Serie> series;
    /**
     * Nombre del directorio donde se almacenan los
     * ficheros.
     */
    private String directorio;

    /**
     * Constructor de JaxB_XML para disponer de
     * datos de la clase Controlador.
     * @param series Lista de series.
     * @param directorio Nombre del directorio
     *                   del almacenamiento de
     *                   ficheros.
     */
    public JaxB_XML(List<Serie> series, String directorio) {
        this.series = series;
        this.directorio = directorio;
    }

    /**
     * Metodo para generar un XML usando JaxB.
     * @throws JAXBException Si ocurre un error durante el procesamiento JAXB.
     * Puede deberse a problemas de mapeo, validación o cualquier otra
     * anomalía relacionada con JAXB.
     */
    public void guardarXML() throws JAXBException {
        File outputFile = new File("src/main/" + directorio + "/series_JaxB.xml");
        JAXBContext context = JAXBContext.newInstance(Serie.class);

        // Crear un marshaller
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // Marshalling (convertir objetos Java a XML)
        marshaller.marshal(series, outputFile);
    }
}
