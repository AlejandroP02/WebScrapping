import javax.xml.parsers.ParserConfigurationException;

/**
 * Llama a los métodos del controlador y cuenta
 * el tiempo que tarda el código en acabar.
 */
public class Main {
  /**
   * El método predefinido para poder lanzar un programa.
   * @param args El código no recibe argumentos.
   * @throws ParserConfigurationException Si ocurre un error durante la
   *         configuración del parser XML. Puede deberse a problemas con la
   *         configuración del parser.
   * @throws javax.xml.bind.JAXBException Si ocurre un error durante el
   *         procesamiento JAXB. Puede deberse a problemas de mapeo, validación
   *         u otras anomalías relacionadas con JAXB.
   */
  public static void main(String[] args) throws ParserConfigurationException, javax.xml.bind.JAXBException {
    Controlador con = new Controlador();
    long startTime = System.currentTimeMillis();
    con.iniciarGecko();
    con.guardarCSV();
    con.guardarXML();
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    System.out.println("Tiempo: "+duration);
  }
}
