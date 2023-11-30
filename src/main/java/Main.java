import javax.xml.parsers.ParserConfigurationException;

public class Main {
  public static void main(String[] args) throws ParserConfigurationException {
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
