package org.openelis.modules.main.client;

import org.openelis.modules.method.client.MethodPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class OpenELISPage {
    
    private final WebDriver driver;
    private final String id = "openelis";
    private WebDriverWait wait;
    
    public OpenELISPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver,30);
    }
    
    By maintenance = By.id(id+".maintenanceMenu");
    By method = By.id(id+".method");
    
    public OpenELISPage clickMaintenance() {
        wait.until(presenceOfElementLocated(maintenance)).click();
        return this;
    }
    
    public MethodPage clickMethod() {
        wait.until(presenceOfElementLocated(method)).click();
        return new MethodPage(driver);
    }
   
}
