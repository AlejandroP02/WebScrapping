import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

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
            String descripcion = driver.findElement(By.className("rightside")).findElement(By.tagName("p")).getText().replaceAll("\n\n","\n");;
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
            System.out.println(serie1.toString());

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
                        descripcion = driver.findElement(By.className("genre-description")).getText().replaceAll("\n\n","\n");
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

}
