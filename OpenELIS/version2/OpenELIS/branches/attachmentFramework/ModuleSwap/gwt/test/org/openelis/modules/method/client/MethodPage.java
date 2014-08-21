package org.openelis.modules.method.client;

import org.openelis.meta.MethodMeta;
import org.openelis.modules.main.client.Ids;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MethodPage {

    private final WebDriver driver;
    private final String id = "method";
    
    public MethodPage(WebDriver driver) {
        this.driver = driver;
    }
    
    By activeBegin = By.id(id + MethodMeta.getActiveBegin());
    By activeEnd = By.id(id + MethodMeta.getActiveEnd());
    By name = By.id(id + MethodMeta.getName());
    By description = By.id(id + MethodMeta.getDescription());
    By reportingDescription = By.id(id + MethodMeta.getReportingDescription());
    By isActive = By.id(id + MethodMeta.getIsActive());
    By query = By.id(id + Ids.QUERY);
    By previous = By.id(id + Ids.PREVIOUS);
    By next = By.id(id + Ids.NEXT);
    By add = By.id(id + Ids.ADD);
    By update = By.id(id + Ids.UPDATE);
    By commit = By.id(id + Ids.COMMIT);
    By abort = By.id(id + Ids.ABORT);
    By atozNext = By.id(id + Ids.A_TO_Z_NEXT);
    By atozPrev = By.id(id + Ids.A_TO_Z_PREV);
    By optionsButton = By.id(id + Ids.OPTIONS_BUTTON);
    By optionsMenu = By.id(id + Ids.OPTIONS_MENU);
    By history = By.id(id + Ids.HISTORY);
    By atozButtons = By.id(id + Ids.A_TO_Z_Buttons);
    By atozTable = By.id(id + Ids.A_TO_Z_TABLE);
    
    
    public MethodPage enterName(String text) {
        driver.findElement(name).sendKeys(text);
        return this;
    }
    
    public MethodPage clickAdd() {
        driver.findElement(add).click();
        return this;
    }
}
