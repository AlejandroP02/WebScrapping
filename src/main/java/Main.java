import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class Main {

  public static void main(String[] args) {
    System.out.println(System.getenv("PATH"));
    System.out.println(System.getenv("HOME"));

    System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver");
    FirefoxOptions options = new FirefoxOptions();
    options.setBinary("/home/usuario/Descargas/firefox-118.0.2/firefox/firefox");
  
    WebDriver driver = new FirefoxDriver(options);
    driver.get("https://www3.animeflv.net/browse?page=1");
  }
}
