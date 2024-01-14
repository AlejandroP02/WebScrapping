import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import com.opencsv.CSVWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Contiene todos los métodos funcionales
 * del programa
 */
public class Controlador {
    /**
     * Contiene los enlaces de todas las series de la web.
     */
    final private List<String> linksSeries = new ArrayList<>();
    /**
     * Contiene los enlaces de todos los estudios para evitar
     * entrar a páginas en las que ya ha entrado.
     */
    private final List<String> linksEstudios = new ArrayList<>();
    /**
     * Contiene los enlaces de todos los géneros para evitar
     * entrar a páginas en las que ya ha entrado.
     */
    final private List<String> linksGeneros = new ArrayList<>();
    /**
     * Contiene todos los elementos Serie con toda su información.
     */
    @XmlElementWrapper(name = "seriesList")
    @XmlElement(name = "Serie")
    protected List<Serie> series = new ArrayList<>();
    /**
     * Contiene todos los elementos Estudio sin identificador
     * para tener la información de cada uno guardada y evitar
     * entrar a páginas en las que ya ha entrado.
     */
    private final List<Estudio> estudios = new ArrayList<>();
    /**
     * Contiene todos los elementos Género sin identificador
     * para tener la información de cada uno guardada y evitar
     * entrar a páginas en las que ya ha entrado.
     */
    private final List<Genero> generos = new ArrayList<>();
    /**
     * Un identificado numérico para mes.
     */
    private final Map<String, String> map_mes = new HashMap<>();
    /**
     * Formato dia/mes/año para LocalDate.
     */
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    /**
     * Sirve para contener la estructura del XML.
     */
    Document document;
    /**
     * Si en el enlace hay más de 100 series se separa en páginas
     * y entonces se usa esta variable.
     */
    int pages;
    /**
     * Contiene el nombre del directorio donde se guardan los
     * ficheros XML y CSV de cada página que se busque
     */
    String directorio;

    /**
     * El constructor del controlador en el que se llama al
     * método map_mes.
     */
    public Controlador() {
        map_mes();
    }

    /**
     * Asigna un numero a cada mes.
     */
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

    /**
     * Pone al programa en espera para dejar que los elementos de la Web carguen.
     * Se creó para ahorrar tiempo al escribir.
     * @param milesegundos Son los milisegundos que el programa esperara.
     */
    public void sleep(int milesegundos){
        try {
            Thread.sleep(milesegundos);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicia el Gecko en la página especificada
     * y llama a los métodos necesarios para realizar
     * la tarea.
     */
    public void iniciarGecko(){
        System.out.println(System.getenv("PATH"));
        System.out.println(System.getenv("HOME"));

        System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver");
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary("/home/usuario/Descargas/firefox-118.0.2/firefox/firefox");

        WebDriver driver = new FirefoxDriver(options);
        String link = "https://myanimelist.net/anime/genre/65/Magical_Sex_Shift";
        String[] text = link.split("/");
        directorio = text[text.length-1];
        crearDirectorio();
        driver.get(link);
        paginas(driver);
        System.out.println("paginas: "+pages);
        for (int i = 1; i <= pages ; i++) {
            driver.get(link+"?page="+i);
            guardarLinksSeries(driver);
        }
        guardarSeries(driver);
        driver.quit();
    }

    public void crearDirectorio(){
        String rutaCarpeta = "src/main/"+directorio+"/";
        File crearCarpeta = new File(rutaCarpeta);
        if (!crearCarpeta.exists()) {
            crearCarpeta.mkdir();
        }
    }
    
    /**
     * Acepta las cookies y luego en base a las series que hay
     * en el género los divide entre 100 y redondea hacia
     * arriba para saber cuantas páginas hay.
     * @param driver Contiene la página principal de la que se
     *               extrae información.
     */
    public void paginas(WebDriver driver){
        sleep(2000);
        WebElement cookies = driver.findElement(By.className("css-47sehv"));
        cookies.click();
        List<WebElement> num = driver.findElements(By.className("fw-n"));
        int series = 0;
        for (WebElement s:num) {
            if(s.getText().contains("(") && s.getText().contains(")")){
                series = Integer.parseInt(s.getText().replaceAll("\\(","").replaceAll("\\)","").replaceAll(",",""));
            }
        }
        pages= (int)Math.ceil(series/100f);
    }

    /**
     * Guarda el enlace a cada serie de la página.
     * @param driver Es el elemento que permite entrar y navegar por la web,
     *               es necesario para que el método vea los elementos
     *               que contiene él enlaces.
     */
    public void guardarLinksSeries(WebDriver driver){
        sleep(3000);
        List<WebElement> box = driver.findElements(By.className("js-anime-category-producer"));
        for (WebElement a:box) {
            WebElement title = a.findElement(By.className("title"));
            linksSeries.add(title.findElement(By.className("link-title")).getAttribute("href"));
        }
    }

    /**
     * Va guardando las series una por una
     * y llama a los métodos necesarios
     * para guardar otros datos.
     * @param driver Es el elemento que permite entrar y navegar por la web,
     *               es necesario para que el método vea los elementos
     *               que contienen los enlaces.
     */
    public void guardarSeries(WebDriver driver){
        for (String a:linksSeries) {
            driver.get(a);
            sleep(10000);
            WebElement serie = driver.findElement(By.className("h1_bold_none"));
            String descripcion = driver.findElement(By.className("rightside")).findElement(By.tagName("p")).getText().replaceAll("\n"," ");
            String titulo = serie.findElement(By.tagName("strong")).getText();
            String imagen = driver.findElement(By.className("leftside")).findElement(By.tagName("img")).getAttribute("src");
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
                if(e.getText().contains("Type: ")){
                    tipo = e.getText().split(": ")[1];
                }else if(e.getText().contains("Episodes: ")){
                    if(e.getText().split(": ")[1].contains("Unknown")){
                        episodios=0;
                    }else{
                        episodios = Integer.parseInt(e.getText().split(": ")[1]);
                    }
                }if(e.getText().contains("Status: ")){
                    estado = e.getText().split(": ")[1];
                }else if(e.getText().contains("Aired: ")){
                    if(e.getText().split(": ")[1].contains("Not available") || e.getText().split(": ")[1].contains("?")){
                        fechaEstreno=null;
                    }else if(e.getText().split(": ")[1].split(" ").length==2){
                        fecha= e.getText().split(": ")[1].replaceAll(",", "");
                        fecha = "01/"+map_mes.get(fecha.split(" ")[0])+"/"+fecha.split(" ")[1];
                        fechaEstreno= LocalDate.parse(fecha, formatter);
                    }else if(e.getText().split(": ")[1].split(" ").length==1){
                        fecha= e.getText().split(": ")[1].replaceAll(",", "");
                        fecha = "01/01/"+fecha.split(" ")[0];
                        fechaEstreno= LocalDate.parse(fecha, formatter);
                    }else{
                        fecha= e.getText().split(": ")[1].split("to")[0].replaceAll(",", "");
                        fecha = fecha.split(" ")[1]+"/"+map_mes.get(fecha.split(" ")[0])+"/"+fecha.split(" ")[2];
                        fechaEstreno = LocalDate.parse(fecha, formatter);
                    }
                }else if(e.getText().contains("Producers: ") || e.getText().contains("Studios: ")){
                    if(!e.getText().contains("add some")){
                        estudiosL.addAll(guardarLinksEstudios(e));
                    }
                }else if(e.getText().contains("Licensors: ")){
                    licencia = e.getText().split(": ")[1].replaceAll(", add some", "");
                }else if(e.getText().contains("Source: ")){
                    src = e.getText().split(": ")[1];
                }else if(e.getText().contains("Genres: ") || e.getText().contains("Genre: ") || e.getText().contains("Themes: ") || e.getText().contains("Theme: ") || e.getText().contains("Demographic: ") || e.getText().contains("Demographics: ")){
                    if(!e.getText().contains("add some")){
                        generoL.addAll(guardarLinksGeneros(e));
                    }
                }else if(e.getText().contains("Duration: ")){
                    if(e.getText().split(": ")[1].contains("Unknown")){
                        duracion=0;
                    }else{
                        duracion = Float.parseFloat(e.getText().split(": ")[1].split(" ")[0]);
                    }
                }
            }
            Serie serie1 = new Serie(titulo, imagen, tipo, episodios, estado, fechaEstreno, licencia, src, duracion, descripcion);
            serie1.setEstudios(guardarEstudio(driver, estudiosL));
            serie1.setGeneros(guardarGeneros(driver, generoL));
            this.series.add(serie1);
            System.out.println(this.series.size());
        }
    }

    /**
     * Guarda el enlace de los estudios que pertenecen a la serie y al mismo tiempo
     * guarda todos los enlaces en una array para evitar volver a entrar en páginas
     * en las que ya haya entrado.
     * @param estudio Contiene los elementos donde se encuentra
     *                los enlaces de los estudios.
     * @return Devuelve la lista de enlaces de los estudios de la serie en especifica.
     */
    public List<String> guardarLinksEstudios(WebElement estudio){
        List<WebElement> box = estudio.findElements(By.tagName("a"));
        List<String> links = new ArrayList<>();
        for (WebElement a:box) {
            linksEstudios.add(a.getAttribute("href"));
            links.add(a.getAttribute("href"));
        }
        return links;
    }

    /**
     * Guarda el enlace de los géneros que pertenecen a la serie y al mismo tiempo
     * guarda todos los enlaces en una array para evitar volver a entrar en páginas
     * en las que ya haya entrado.
     * @param genero Contiene los elementos donde se encuentra
     *               los enlaces de los géneros.
     * @return Devuelve la lista de enlaces de los estudios de la serie en especifica.
     */
    public List<String> guardarLinksGeneros(WebElement genero){
        List<WebElement> box = genero.findElements(By.tagName("a"));
        List<String> links = new ArrayList<>();
        for (WebElement a:box) {
            linksGeneros.add(a.getAttribute("href"));
            links.add(a.getAttribute("href"));
        }
        return links;
    }

    /**
     * Guarda toda la información de los estudios antes comprobando si el estudio
     * ya se ha guardado con anterioridad para evitar volver a entrar en la página
     * del estudio y recoge los datos de una lista que contiene todos los estudios,
     * si el estudio no ha aparecido antes entonces si entra en su página guarda
     * su información y lo guarda en dos lista, la que contiene todos los estudios
     * y la que retorna.
     * @param driver Sirve para que vaya navegando entre los distintos estudios.
     * @param links La lista que contiene todos los enlaces de los estudios de los
     *              cuales tiene que guardar información.
     * @return Devuelve una lista que contiene todos los estudios con su información
     * correspondiente.
     */
    public List<Estudio> guardarEstudio(WebDriver driver, List<String> links){
        String fecha="";
        List<Estudio> estudios = new ArrayList<>();
        for (String a:links) {
            if(Collections.frequency(linksEstudios, a)<=1){
                driver.get(a);
                sleep(9000);
                String titulo = driver.findElement(By.className("h1-title")).getText();
                LocalDate fechaCreacion=null;
                WebElement cuadroHorizontal = driver.findElement(By.className("navi-seasonal"));
                int series = Integer.parseInt(cuadroHorizontal.findElement(By.className("on")).getText().replaceAll("All \\(", "").replace(")", ""));
                List<WebElement> contentLeft = driver.findElements(By.className("spaceit_pad"));
                for (WebElement b:contentLeft) {
                    if(b.getText().contains("Established: ")){
                        if(b.getText().contains("Unknown")){
                            fechaCreacion=null;
                        }else if(b.getText().split(": ")[1].split(" ").length==2){
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
                Estudio estudio = new Estudio(titulo, a, fechaCreacion, series);
                estudios.add(estudio);
                this.estudios.add(estudio);
            }else{
                for (Estudio b:this.estudios) {
                    if(b.getLink().equals(a)){
                        estudios.add(b);
                    }
                }
            }

        }
        return estudios;
    }

    /**
     * Guarda toda la información de los géneros antes comprobando si el género
     * ya se ha guardado con anterioridad para evitar volver a entrar en la página
     * del género y recoge los datos de una lista que contiene todos los géneros,
     * si el género no ha aparecido antes entonces si entra en su página guarda
     * su información y lo guarda en dos lista, la que contiene todos los géneros
     * y la que retorna.
     * @param driver Sirve para que vaya navegando entre los distintos géneros.
     * @param links La lista que contiene todos los enlaces de los géneros de los
     *              cuales tiene que guardar información.
     * @return Devuelve una lista que contiene todos los géneros con su información
     * correspondiente.
     */
    public List<Genero> guardarGeneros(WebDriver driver, List<String> links){
        List<Genero> generos = new ArrayList<>();
        for (String a:links) {
            if(Collections.frequency(linksGeneros, a)<=1){
                driver.get(a);
                sleep(9000);
                String titulo = driver.findElement(By.className("h1")).getText();
                List<WebElement> num = driver.findElements(By.className("fw-n"));
                int series = 0;
                for (WebElement s:num) {
                    if(s.getText().contains("(") && s.getText().contains(")")){
                        series = Integer.parseInt(s.getText().replaceAll("\\(","").replaceAll("\\)","").replaceAll(",",""));
                    }
                }
                String descripcion="Descripcion generica";
                List<WebElement> ps = driver.findElements(By.tagName("p"));
                for (WebElement p:ps) {
                    if(p.getAttribute("class").contains("genre-description")){
                        descripcion = driver.findElement(By.className("genre-description")).getText().replaceAll("\n"," ");
                    }

                }
                Genero genero = new Genero(titulo, a, descripcion, series);
                generos.add(genero);
                this.generos.add(genero);
            }else{
                for (Genero b:this.generos) {
                    if(b.getLink().equals(a)){
                        generos.add(b);
                    }
                }
            }

        }
        return generos;
    }

    /**
     * Género un CSV para cada clase que se almacena
     * ordenados basándonos en el identificador que tienen
     * todas las clases, que están relacionadas con
     * la clase Serie.
     */
    public void guardarCSV(){
        // En la siguiente variable es necesario poner la ruta en la que deseas guardar el fichero.
        String csvSeries="src/main/"+directorio+"/series.csv";
        String csvEstudios="src/main/"+directorio+"/estudios.csv";
        String csvGeneros="src/main/"+directorio+"/generos.csv";

        try {
            CSVWriter writer1 = new CSVWriter(new FileWriter(csvSeries), ',',
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            CSVWriter writer2 = new CSVWriter(new FileWriter(csvEstudios), ',',
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            CSVWriter writer3 = new CSVWriter(new FileWriter(csvGeneros), ',',
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            String[] data1 = {"ID", "TITULO", "IMAGEN", "TIPO", "EPISODIOS", "ESTADO", "FECHA_ESTRENO", "LICENCIA", "ID_ESTUDIOS", "SRC", "ID_GENEROS", "DURACION","DESCRIPCION"};
            writer1.writeNext(data1);
            data1 = new String[]{"ID", "NOMBRE", "LINK", "FECHA_CREACION", "SERIES"};
            writer2.writeNext(data1);
            data1 = new String[]{"ID", "NOMBRE", "LINK", "DESCRIPCION", "SERIES"};
            writer3.writeNext(data1);
            String[] data;
            // Escribe las líneas de datos al archivo CSV
            for (Serie serie : this.series) {
                data = new String[]{
                        String.valueOf(serie.getId()),
                        serie.getTitulo(),
                        serie.getImagen(),
                        serie.getTipo(),
                        String.valueOf(serie.getEpisodios()),
                        serie.getEstado(),
                        String.valueOf(serie.getFechaEstreno()),
                        serie.getLicencia(),
                        serie.getEstudios().toString(),
                        serie.getSrc(),
                        serie.getGeneros().toString(),
                        String.valueOf(serie.getDuracion()),
                        serie.getDescripcion()
                };
                writer1.writeNext(data);
            }
            for (Estudio estudio:estudios) {
                data = new String[]{String.valueOf(estudio.getId()), estudio.getNombre(), estudio.getLink(), String.valueOf(estudio.getFechaCreacion()), String.valueOf(estudio.getSeries())};
                writer2.writeNext(data);
            }
            for (Genero genero:generos) {
                data = new String[]{String.valueOf(genero.getId()), genero.getNombre(), genero.getLink(), genero.getDescripcion(), String.valueOf(genero.getSeries())};
                writer3.writeNext(data);
            }
            writer3.close();
            writer2.close();
            writer1.close();


            System.out.println("Datos guardados correctamente en el archivo CSV.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Llama a los métodos necesarios para guardar toda la información en el XML.
     * @throws ParserConfigurationException Esta excepción es lanzada cuando ocurre
     * un error durante la configuración de un parser de XML.
     */
    public void guardarXML() throws ParserConfigurationException{
        File outpuFile = new File("src/main/"+directorio+"/series.xml");
        crearDocument();
        createRootNode();
        addSerieToDOM();
        saveDOMAsFile(outpuFile);
        //JaxB_XML jaxB_xml=new JaxB_XML(series, directorio);
        //jaxB_xml.guardarXML();
    }

    /**
     * Crea el document que se escribirá en el XML.
     */
    public void crearDocument(){
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            document = builder.newDocument();
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    /**
     * Crea un nodo/elemento listo para ser añadido con la información
     * recibida.
     * @param name El nombre que tendrá el elemento/nodo XML.
     * @param content Texto plano o información que ira dentro del
     *                elemento/nod XML.
     * @return Retorna un nodo/elemento XML listo para ser añadido.
     */
    public Node createNode(String name, String content){
        Node MyNode = document.createElement(name);
        Node MyNodeText = document.createTextNode(content);
        MyNode.appendChild(MyNodeText);
        return MyNode;
    }

    /**
     * Añade el nodo/elemento XML a la estructura.
     * @param pare Elemento/nodo XML una capa superior a fill.
     * @param fill Elemento/nodo XML que se encuentra dentro de
     *             padre.
     */
    public void afegeixNode(Node pare, Node fill){
        pare.appendChild(fill);
    }

    /**
     * Añade las series a la estructura del XML
     * @return Si retorna 0 es que se ha ejecutado correctamente,
     * en cambio, si devuelve 1 es que ha surgido un error.
     */
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
                Node nodeEstudios = afegeixEstudios(serie.getEstudios());
                afegeixNode(nodeSerie, nodeEstudios);
                Node nodeSrc = createNode("Src", serie.getSrc());
                afegeixNode(nodeSerie, nodeSrc);
                Node nodeGeneros = afegeixGeneros(serie.getGeneros());
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

    /**
     * Guarda todos los estudios en una lista de nodos
     * dentro del nodo estudios, en el que cada uno
     * pertenece a un nodo estudio que contiene la
     * información del estudio.
     * @param estudios Es la lista de estudios de
     *                 la serie a la que pertenece.
     * @return devuelve el nodo que contiene todos
     * los estudios de la serie a la que pertenecen.
     */
    private Node afegeixEstudios(List<Estudio> estudios){
        Node nodeEstudios = document.createElement("Estudios");
        for (Estudio estudio:estudios) {
            Node nodeEstudio = document.createElement("Estudio");
            afegeixNode(nodeEstudios, nodeEstudio);
            Node nodeId = createNode("ID", String.valueOf(estudio.getId()));
            afegeixNode(nodeEstudio, nodeId);
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
    /**
     * Guarda todos los géneros en una lista de nodos
     * dentro del nodo géneros, en el que cada uno
     * pertenece a un nodo género que contiene la
     * información del género.
     * @param generos Es la lista de géneros de
     *                 la serie a la que pertenece.
     * @return devuelve el nodo que contiene todos
     * los géneros de la serie a la que pertenecen.
     */
    private Node afegeixGeneros(List<Genero> generos){
        Node nodeGeneros = document.createElement("Generos");
        for (Genero genero:generos) {
            Node nodeGenero = document.createElement("Genero");
            afegeixNode(nodeGeneros, nodeGenero);
            Node nodeId = createNode("ID", String.valueOf(genero.getId()));
            afegeixNode(nodeGenero, nodeId);
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

    /**
     * Guarda toda la estructura guardada en el XML con el formato
     * correcto.
     * @param file Fichero en el que se guarda el XML
     * @return Si retorna 0 es que se ha ejecutado correctamente,
     * en cambio, si devuelve 1 es que ha surgido un error.
     */
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

    /**
     * Crea la raíz del XML.
     * @return Si retorna 0 es que se ha ejecutado correctamente,
     * en cambio, si devuelve 1 es que ha surgido un error.
     */
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
