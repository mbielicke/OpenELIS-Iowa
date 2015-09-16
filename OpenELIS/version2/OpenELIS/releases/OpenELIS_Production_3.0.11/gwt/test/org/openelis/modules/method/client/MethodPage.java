package org.openelis.modules.method.client;

import org.openelis.meta.MethodMeta;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MethodPage {
    
    private final WebDriver driver;
    private final String id = "method";
    private WebDriverWait wait;
    
    public MethodPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver,10);
    }
    
    By activeBegin = By.id(id+"."+MethodMeta.getActiveBegin());
    By activeEnd = By.id(id+"."+MethodMeta.getActiveEnd());
    By name = By.id(id+"."+MethodMeta.getName());
    By description = By.id(id+"."+MethodMeta.getDescription());
    By reportingDescription = By.id(id+"."+MethodMeta.getReportingDescription());
    By isActive = By.id(id+"."+MethodMeta.getIsActive());
    By query = By.id(id+"."+"query");
    By previous = By.id(id+"."+"previous");
    By next = By.id(id+".next");
    By add = By.id(id+".add");
    By update = By.id(id+".update");
    By commit = By.id(id+".commit");
    By abort = By.id(id+".abort");
    By atozNext = By.id(id+".atozNext");
    By atozPrev = By.id(id+".atozPrev");
    By optionsButton = By.id(id+".optionsButton");
    By optionsMenu = By.id(id+".optionsMenu");
    By history = By.id(id+".history");
    By atozButtons = By.id(id+".atozButtons");
    By atozTable = By.id(id+".atozTable");
    
    public MethodPage clickAdd() {
        wait.until(ExpectedConditions.presenceOfElementLocated(add)).click();
        return this;
    }
    
    public MethodPage typeInName(String text) {
        driver.findElement(name).sendKeys(text);
        return this;
    }
    
    public String getNameText() {
        return driver.findElement(name).getAttribute("value");
    }
}
