package org.openelis.modules.method.client;

import org.openelis.modules.method.client.MethodScreenUI.Ids;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MethodPage {

    private final WebDriver driver;
    private final String id = "main";
    
    public MethodPage(WebDriver driver) {
        this.driver = driver;
    }
    
    By activeBegin = By.id(id + Ids.ACTIVE_BEGIN);
    By activeEnd = By.id(id + Ids.ACTIVE_END);
    By name = By.id(id + Ids.NAME);
    By description = By.id(id + Ids.DESCRIPTION);
    By reportingDescription = By.id(id + Ids.REPORTING_DESCRIPTION);
    By isActive = By.id(id + Ids.IS_ACTIVE);
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
    
    
}
