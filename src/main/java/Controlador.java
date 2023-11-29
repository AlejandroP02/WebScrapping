import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import com.opencsv.CSVWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Controlador {
    private  List<String> linksSeries = new ArrayList<>();
    private List<String> linksEstudios = new ArrayList<>();
    private List<String> linksGeneros = new ArrayList<>();
    private List<Serie> series = new ArrayList<>();
    private List<Estudio> estudios = new ArrayList<>();
    private List<Genero> generos = new ArrayList<>();
    private final Map<String, String> map_mes = new HashMap<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    Document document;

    public Controlador() {
        map_mes();
    }

    public void map_mes(){
        map_mes.put("Jan", "01");
        map_mes.put("Feb", "02");
        map_mes.put("Mar", "03");
        map_mes.put("Apr", "04");
        map_mes.put("May", "05");
        map_mes.put("Jun", "06");
        map_mes.put("Jul", "07");
        map_mes.put("Aug", "08");
        map_mes.put("Sep", "09");
        map_mes.put("Oct", "10");
        map_mes.put("Nov", "11");
        map_mes.put("Dec", "12");
    }

    public void sleep(int a){
        try {
            Thread.sleep(a);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void iniciarGecko(){
        System.out.println(System.getenv("PATH"));
        System.out.println(System.getenv("HOME"));

        System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver");
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary("/home/usuario/Descargas/firefox-118.0.2/firefox/firefox");

        WebDriver driver = new FirefoxDriver(options);
        driver.get("https://myanimelist.net/anime/genre/69/Otaku_Culture");
        guardarLinksSeries(driver);
        guardarSeries(driver);
        driver.quit();
    }

    public void guardarLinksSeries(WebDriver driver){
        sleep(5000);
        WebElement cookies = driver.findElement(By.className("css-47sehv"));
        cookies.click();
        List<WebElement> box = driver.findElements(By.className("js-anime-category-producer"));
        for (WebElement a:box) {
            WebElement title = a.findElement(By.className("title"));
            linksSeries.add(title.findElement(By.className("link-title")).getAttribute("href"));
        }
    }

    public void guardarSeries(WebDriver driver){
        for (String a:linksSeries) {
            driver.get(a);
            sleep(7000);
            WebElement serie = driver.findElement(By.className("h1_bold_none"));
            String descripcion = driver.findElement(By.className("rightside")).findElement(By.tagName("p")).getText().replaceAll("\n"," ");;
            String titulo = serie.findElement(By.tagName("strong")).getText();
            String imagen = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[3]/div[2]/table/tbody/tr/td[1]/div/div[1]/a/img")).getAttribute("src");
            List<WebElement> series = driver.findElements(By.className("spaceit_pad"));
            String tipo="";
            int episodios=0;
            String estado="";
            String fecha="";
            List<String> estudiosL = new ArrayList<>();
            List<String> generoL = new ArrayList<>();
            LocalDate fechaEstreno = null;
            String licencia="";
            String src="";
            float duracion=0f;
            for (WebElement e:series) {
                if(e.getText().contains("Type")){
                    tipo = e.getText().split(": ")[1];
                }else if(e.getText().contains("Episodes")){
                    if(e.getText().split(": ")[1].contains("Unknown")){
                        episodios=0;
                    }else{
                        episodios = Integer.parseInt(e.getText().split(": ")[1]);
                    }
                }if(e.getText().contains("Status")){
                    estado = e.getText().split(": ")[1];
                }else if(e.getText().contains("Aired")){
                    if(e.getText().split(": ")[1].contains("Not available") || e.getText().split(": ")[1].contains("?")){
                        fechaEstreno=null;
                    }else{
                        fecha= e.getText().split(": ")[1].split("to")[0].replaceAll(",", "");
                        fecha = fecha.split(" ")[1]+"/"+map_mes.get(fecha.split(" ")[0])+"/"+fecha.split(" ")[2];
                        fechaEstreno = LocalDate.parse(fecha, formatter);
                    }
                }else if(e.getText().contains("Producers") || e.getText().contains("Studios")){
                    if(!e.getText().contains("add some")){
                        estudiosL.addAll(guardarLinksEstudios(e));
                    }
                }else if(e.getText().contains("Licensors")){
                    licencia = e.getText().split(": ")[1].replaceAll(", add some", "");
                }else if(e.getText().contains("Source")){
                    src = e.getText().split(": ")[1];
                }else if(e.getText().contains("Genre") || e.getText().contains("Themes") || e.getText().contains("Demographic")){
                    if(!e.getText().contains("add some")){
                        generoL.addAll(guardarLinksGeneros(e));
                    }
                }else if(e.getText().contains("Duration")){
                    if(e.getText().split(": ")[1].contains("Unknown")){
                        duracion=0;
                    }else{
                        duracion = Float.parseFloat(e.getText().split(": ")[1].split(" ")[0]);
                    }
                }
            }
            Serie serie1 = new Serie(titulo, imagen, tipo, episodios, estado, fechaEstreno, licencia, src, duracion, descripcion);
            serie1.setEstudios(guardarEstudio(driver, serie1, estudiosL));
            serie1.setGeneros(guardarGeneros(driver, serie1, generoL));
            this.series.add(serie1);
            System.out.println(this.series.size());
        }
    }

    public List<String> guardarLinksEstudios(WebElement estudio){
        List<WebElement> box = estudio.findElements(By.tagName("a"));
        List<String> links = new ArrayList<>();
        for (WebElement a:box) {
            linksEstudios.add(a.getAttribute("href"));
            links.add(a.getAttribute("href"));
        }
        return links;
    }
    public List<String> guardarLinksGeneros(WebElement genero){
        List<WebElement> box = genero.findElements(By.tagName("a"));
        List<String> links = new ArrayList<>();
        for (WebElement a:box) {
            linksGeneros.add(a.getAttribute("href"));
            links.add(a.getAttribute("href"));
        }
        return links;
    }

    public List<Estudio> guardarEstudio(WebDriver driver, Serie serie, List<String> links){
        String fecha="";
        List<Estudio> estudios = new ArrayList<>();
        for (String a:links) {
            if(Collections.frequency(linksEstudios, a)<=1){
                driver.get(a);
                sleep(7000);
                String titulo = driver.findElement(By.className("h1-title")).getText();
                LocalDate fechaCreacion=null;
                WebElement cuadroHorizontal = driver.findElement(By.className("navi-seasonal"));
                int series = Integer.parseInt(cuadroHorizontal.findElement(By.className("on")).getText().replaceAll("All \\(", "").replace(")", ""));
                List<WebElement> contentLeft = driver.findElements(By.className("spaceit_pad"));
                for (WebElement b:contentLeft) {
                    if(b.getText().contains("Established")){
                        if(b.getText().split(": ")[1].split(" ").length==2){
                            fecha= b.getText().split(": ")[1].replaceAll(",", "");
                            fecha = "01/"+map_mes.get(fecha.split(" ")[0])+"/"+fecha.split(" ")[1];
                            fechaCreacion= LocalDate.parse(fecha, formatter);
                        }else if(b.getText().split(": ")[1].split(" ").length==1){
                            fecha= b.getText().split(": ")[1].replaceAll(",", "");
                            fecha = "01/01/"+fecha.split(" ")[0];
                            fechaCreacion= LocalDate.parse(fecha, formatter);
                        }else{
                            fecha= b.getText().split(": ")[1].replaceAll(",", "");
                            fecha = fecha.split(" ")[1]+"/"+map_mes.get(fecha.split(" ")[0])+"/"+fecha.split(" ")[2];
                            fechaCreacion = LocalDate.parse(fecha, formatter);
                        }
                    }
                }
                estudios.add(new Estudio(serie.getId(), titulo, a, fechaCreacion, series));
                this.estudios.add(new Estudio(titulo, a, fechaCreacion, series));
            }else{
                for (Estudio b:this.estudios) {
                    if(b.getLink().equals(a)){
                        estudios.add(new Estudio(serie.getId(), b.getNombre(), a, b.getFechaCreacion(), b.getSeries()));
                    }
                }
            }

        }
        return estudios;
    }


    public List<Genero> guardarGeneros(WebDriver driver, Serie serie, List<String> links){
        List<Genero> genero = new ArrayList<>();
        for (String a:links) {
            if(Collections.frequency(linksGeneros, a)<=1){
                driver.get(a);
                sleep(7000);
                String titulo = driver.findElement(By.className("h1")).getText();
                List<WebElement> num = driver.findElements(By.className("fw-n"));
                int series = 0;
                for (WebElement s:num) {
                    if(s.getText().contains("(") && s.getText().contains(")")){
                        series = Integer.parseInt(s.getText().replaceAll("\\(","").replaceAll("\\)","").replaceAll(",",""));
                    }
                }
                String descripcion="";
                List<WebElement> ps = driver.findElements(By.tagName("p"));
                for (WebElement p:ps) {
                    if(p.getAttribute("class").contains("genre-description")){
                        descripcion = driver.findElement(By.className("genre-description")).getText().replaceAll("\n"," ");
                    }

                }
                genero.add(new Genero(serie.getId(), titulo, a, descripcion, series));
                this.generos.add(new Genero(titulo, a, descripcion, series));
            }else{
                for (Genero b:this.generos) {
                    if(b.getLink().equals(a)){
                        genero.add(new Genero(serie.getId(), b.getNombre(), a, b.getDescripcion(), b.getSeries()));
                    }
                }
            }

        }
        return genero;
    }

    public void guardarCSV(){
        // En la siguiente variable es necesario poner la ruta en la que deseas guardar el fichero.
        char nuevoSeparador = ';';
        String csvSeries="src/main/series.csv";
        String csvEstudios="src/main/estudios.csv";
        String csvGeneros="src/main/generos.csv";

        try {
            CSVWriter writer1 = new CSVWriter(new FileWriter(csvSeries), nuevoSeparador,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.NO_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            CSVWriter writer2 = new CSVWriter(new FileWriter(csvEstudios), nuevoSeparador,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.NO_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            CSVWriter writer3 = new CSVWriter(new FileWriter(csvGeneros), nuevoSeparador,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.NO_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            String[] data1 = {"ID", "TITULO", "IMAGEN", "TIPO", "EPISODIOS", "ESTADO", "FECHA_ESTRENO", "SRC", "DURACION"};
            writer1.writeNext(data1);
            data1 = new String[]{"ID_SERIE", "NOMBRE", "LINK", "FECHA_CREACION", "SERIES"};
            writer2.writeNext(data1);
            data1 = new String[]{"ID_SERIE", "NOMBRE", "LINK", "DESCRIPCION", "SERIES"};
            writer3.writeNext(data1);
            // Escribe las líneas de datos al archivo CSV
            for (Serie serie : this.series) {
                String[] data = {
                        String.valueOf(serie.getId()),
                        serie.getTitulo(),
                        serie.getImagen(),
                        serie.getTipo(),
                        String.valueOf(serie.getEpisodios()),
                        serie.getEstado(),
                        String.valueOf(serie.getFechaEstreno()),
                        serie.getSrc(),
                        String.valueOf(serie.getDuracion()),
                        serie.getDescripcion()
                };
                //System.out.println(Arrays.toString(data));
                writer1.writeNext(data);
                for (Estudio estudio:serie.getEstudios()) {
                    data = new String[]{String.valueOf(serie.getId()), estudio.getNombre(), estudio.getLink(), String.valueOf(estudio.getFechaCreacion()), String.valueOf(estudio.getSeries())};
                    writer2.writeNext(data);
                }
                for (Genero genero:serie.getGeneros()) {
                    data = new String[]{String.valueOf(serie.getId()), genero.getNombre(), genero.getLink(), genero.getDescripcion(), String.valueOf(genero.getSeries())};
                    writer3.writeNext(data);
                }
            }
            writer3.close();
            writer2.close();
            writer1.close();


            System.out.println("Datos guardados correctamente en el archivo CSV.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarXML() throws ParserConfigurationException {
        File outpuFile = new File("src/main/series.xml");
        crearDocument();
        createRootNode();
        addSerieToDOM();
        saveDOMAsFile(outpuFile);

    }

    public void crearDocument(){
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            document = builder.newDocument();
        }catch (Exception e){
            throw new RuntimeException();
        }
    };
    public Node createNode(String name, String content){
        Node MyNode = document.createElement(name);
        Node MyNodeText = document.createTextNode(content);
        MyNode.appendChild(MyNodeText);
        return MyNode;
    }
    public void afegeixNode(Node pare, Node fill){
        pare.appendChild(fill);
    }
    public int addSerieToDOM() {
        try {
            for (Serie serie:series) {
                Node nodeSerie = document.createElement("Serie");

                Node nodeID = createNode("ID", String.valueOf(serie.getId()));
                afegeixNode(nodeSerie, nodeID);
                Node nodeTitulo = createNode("Titulo", serie.getTitulo());
                afegeixNode(nodeSerie, nodeTitulo);
                Node nodeImg = createNode("Imagen", serie.getImagen());
                afegeixNode(nodeSerie, nodeImg);
                Node nodeTipo = createNode("Tipo", serie.getTipo());
                afegeixNode(nodeSerie, nodeTipo);
                Node nodeEstado = createNode("Estado", serie.getEstado());
                afegeixNode(nodeSerie, nodeEstado);
                Node nodeFecha = createNode("FechaEstreno", String.valueOf(serie.getFechaEstreno()));
                afegeixNode(nodeSerie, nodeFecha);
                Node nodeLicencia = createNode("Licencia", serie.getLicencia());
                afegeixNode(nodeSerie, nodeLicencia);
                Node nodeEstudios = afegeixEstudios(nodeSerie, serie.getEstudios());
                afegeixNode(nodeSerie, nodeEstudios);
                Node nodeSrc = createNode("Src", serie.getSrc());
                afegeixNode(nodeSerie, nodeSrc);
                Node nodeGeneros = afegeixGeneros(nodeSerie, serie.getGeneros());
                afegeixNode(nodeSerie, nodeGeneros);
                Node nodeDuracion = createNode("Duracion", String.valueOf(serie.getDuracion()));
                afegeixNode(nodeSerie, nodeDuracion);
                Node nodeDescripcion = createNode("Descripcion", serie.getDescripcion());
                afegeixNode(nodeSerie, nodeDescripcion);



                Node nodeArrel = document.getChildNodes().item(0);

                nodeArrel.appendChild(nodeSerie);
            }
            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private Node afegeixEstudios(Node nodeSerie, List<Estudio> estudios){
        Node nodeEstudios = document.createElement("Estudios");
        for (Estudio estudio:estudios) {
            Node nodeEstudio = document.createElement("Estudio");
            afegeixNode(nodeEstudios, nodeEstudio);
            Node nodeNombre = createNode("Nombre", estudio.getNombre());
            afegeixNode(nodeEstudio, nodeNombre);
            Node nodeLink = createNode("Link", estudio.getLink());
            afegeixNode(nodeEstudio, nodeLink);
            Node nodeFecha = createNode("FechaCreacion", String.valueOf(estudio.getFechaCreacion()));
            afegeixNode(nodeEstudio, nodeFecha);
            Node nodeSeries = createNode("Series", String.valueOf(estudio.getSeries()));
            afegeixNode(nodeEstudio, nodeSeries);
        }
        return nodeEstudios;
    }
    private Node afegeixGeneros(Node nodeSerie, List<Genero> generos){
        Node nodeGeneros = document.createElement("Generos");
        for (Genero genero:generos) {
            Node nodeGenero = document.createElement("Genero");
            afegeixNode(nodeGeneros, nodeGenero);
            Node nodeNombre = createNode("Nombre", genero.getNombre());
            afegeixNode(nodeGenero, nodeNombre);
            Node nodeLink = createNode("Link", genero.getLink());
            afegeixNode(nodeGenero, nodeLink);
            Node nodeDescripcion = createNode("Descripcion", genero.getDescripcion());
            afegeixNode(nodeGenero, nodeDescripcion);
            Node nodeSeries = createNode("Series", String.valueOf(genero.getSeries()));
            afegeixNode(nodeGenero, nodeSeries);
        }
        return nodeGeneros;
    }


    public int saveDOMAsFile(File file) {

        // Write the content into an XML file
        try {
            // Prepare the transformation
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(file);
            // Execute the transform
            transformer.transform(domSource, streamResult);

            // Output to console (testing)
            System.out.println("\n## DOM saved as file in: "+file.getPath()+"\n");
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(domSource, consoleResult);

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public int createRootNode(){
        try {
            Element rootElement = document.createElement("Series");
            document.appendChild(rootElement);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
