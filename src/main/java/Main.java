import javax.xml.parsers.ParserConfigurationException;

public class Main {
  public static void main(String[] args) throws ParserConfigurationException {
    Controlador con = new Controlador();
    con.iniciarGecko();
    con.guardarCSV();
    con.guardarXML();
  }
}
