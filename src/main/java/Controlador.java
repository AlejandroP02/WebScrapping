import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.ArrayList;
import java.util.List;

public class Controlador {
    List<String> linksSeries = new ArrayList<>();
    List<String> linksEstudios = new ArrayList<>();
    List<String> linksGeneros = new ArrayList<>();
    List<String> series = new ArrayList<>();

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
        guardiaSeries(driver);
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

    public void guardiaSeries(WebDriver driver){
        for (String a:linksSeries) {
            driver.get(a);
            sleep(5000);
            WebElement serie = driver.findElement(By.className("h1_bold_none"));
            String titulo = serie.findElement(By.tagName("strong")).getText();
            List<WebElement> series = driver.findElements(By.className("spaceit_pad"));

            for (WebElement e:series) {
                if (e.("Type")){
                    System.out.println(e);
                }
            }
        }
    }

}
