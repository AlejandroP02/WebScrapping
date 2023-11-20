import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.By.*;


public class Main {
  public static void sleep(int a){
    try {
      Thread.sleep(a);
    }catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {

    Controlador con = new Controlador();
    con.iniciarGecko();
  }
}
