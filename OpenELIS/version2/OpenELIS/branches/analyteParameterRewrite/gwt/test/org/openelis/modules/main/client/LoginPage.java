package org.openelis.modules.main.client;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class LoginPage {
    
    private final WebDriver driver;
    private WebDriverWait wait;
        
    By user = By.name("username");
    By pass = By.name("j_password");
    By submit = By.className("submit");
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver,30);
    }
    
    public OpenELISPage login() {
        wait.until(presenceOfElementLocated(user)).sendKeys("demo");
        wait.until(presenceOfElementLocated(pass)).sendKeys("demo");
        wait.until(presenceOfElementLocated(submit)).click();
        
        return new OpenELISPage(driver);
    }
}
