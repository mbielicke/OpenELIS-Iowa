package org.openelis.modules.main.client;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    
    private final WebDriver driver;
    private final String id = "main";
        
    By user = By.name("username");
    By pass = By.name("j_password");
    By submit = By.className("submit");
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }
    
    public MainPage login() {
        driver.findElement(user).sendKeys("demo");
        driver.findElement(pass).sendKeys("demo");
        driver.findElement(submit).click();
        
        return new MainPage(driver);
    }

}
